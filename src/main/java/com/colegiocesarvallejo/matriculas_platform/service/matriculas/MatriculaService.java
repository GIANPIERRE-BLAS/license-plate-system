package com.colegiocesarvallejo.matriculas_platform.service.matriculas;

import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.CursoMatriculaDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.IniciarMatriculaDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.MatriculaResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.*;
import com.colegiocesarvallejo.matriculas_platform.repository.*;
import com.colegiocesarvallejo.matriculas_platform.service.notificaciones.NotificacionService;
import com.colegiocesarvallejo.matriculas_platform.service.pagos.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaCursoRepository matriculaCursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoService pagoService;
    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    public MatriculaResponseDTO iniciarMatricula(IniciarMatriculaDTO dto) {
        log.info("Iniciando matrícula para estudiante ID: {}", dto.getEstudianteId());

        Estudiante estudiante = estudianteRepository.findById(dto.getEstudianteId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (matriculaRepository.existsByEstudianteIdAndAnioAcademico(dto.getEstudianteId(), dto.getAnioAcademico())) {
            throw new RuntimeException("Ya existe una matrícula para este estudiante en el año " + dto.getAnioAcademico());
        }

        Matricula matricula = new Matricula();
        matricula.setEstudiante(estudiante);
        matricula.setApoderado(estudiante.getApoderado());
        matricula.setAnioAcademico(dto.getAnioAcademico());
        matricula.setGrado(dto.getGrado());
        matricula.setSeccion(dto.getSeccion());
        matricula.setNivelAcademico(dto.getNivelAcademico());
        matricula.setObservaciones(dto.getObservaciones());
        matricula.setComentariosApoderado(dto.getComentariosApoderado());
        matricula.setCreatedBy(dto.getCreatedBy());
        matricula.setFechaVencimiento(LocalDate.now().plusDays(30));
        matricula.setEstado(EstadoMatricula.PENDIENTE);
        matricula.setPagosGenerados(false);

        Matricula matriculaGuardada = matriculaRepository.save(matricula);

        estudiante.setGradoActual(matriculaGuardada.getGrado());
        estudiante.setSeccionActual(matriculaGuardada.getSeccion());
        estudiante.setAñoAcademico(matriculaGuardada.getAnioAcademico());
        estudianteRepository.save(estudiante);

        if (dto.getCursosIds() != null && !dto.getCursosIds().isEmpty()) {
            inscribirEnCursos(matriculaGuardada.getId(), dto.getCursosIds());
        }

        log.info("Matrícula creada sin generar pagos. Se generarán al aprobar.");

        notificacionService.enviarNotificacion(
                estudiante.getApoderado().getId(),
                "Matrícula Iniciada",
                String.format("Se ha iniciado la matrícula de %s. Número: %s",
                        estudiante.getNombreCompleto(),
                        matriculaGuardada.getNumeroMatricula())
        );

        notificacionService.enviarNotificacionGlobal(
                "Nueva Matrícula",
                String.format("Nueva matrícula registrada: %s - %s",
                        matriculaGuardada.getNumeroMatricula(),
                        estudiante.getNombreCompleto()),
                "info"
        );

        try {
            messagingTemplate.convertAndSend("/topic/matriculas/nuevas", convertirAResponseDTO(matriculaGuardada));
        } catch (Exception e) {
            log.error("Error al enviar notificación WebSocket", e);
        }

        return convertirAResponseDTO(matriculaGuardada);
    }

    public MatriculaResponseDTO aprobarMatricula(Long matriculaId, String observaciones, Long procesadoPorId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        if (!matricula.puedeSerAprobada()) {
            throw new RuntimeException("La matrícula no puede ser aprobada en su estado actual: " + matricula.getEstado());
        }

        matricula.setEstado(EstadoMatricula.APROBADA);
        matricula.setFechaAprobacion(LocalDateTime.now());
        matricula.setObservaciones(observaciones);
        matricula.setProcesadoPor(usuarioRepository.findById(procesadoPorId).orElse(null));
        matricula.setFechaProcesamiento(LocalDateTime.now());

        List<MatriculaCurso> cursos = matriculaCursoRepository.findByMatriculaId(matriculaId);
        for (MatriculaCurso mc : cursos) {
            mc.setEstado(EstadoMatricula.APROBADA);
            mc.setFechaAprobacion(LocalDateTime.now());
            matriculaCursoRepository.save(mc);
        }

        Estudiante estudiante = matricula.getEstudiante();
        estudiante.setGradoActual(matricula.getGrado());
        estudiante.setSeccionActual(matricula.getSeccion());
        estudiante.setAñoAcademico(matricula.getAnioAcademico());
        estudianteRepository.save(estudiante);

        Matricula matriculaAprobada = matriculaRepository.save(matricula);
        recalcularMontos(matriculaAprobada);

        notificacionService.enviarNotificacion(
                matricula.getApoderado().getId(),
                "¡Matrícula Aprobada!",
                String.format("La matrícula de %s ha sido APROBADA. Número: %s",
                        estudiante.getNombreCompleto(),
                        matricula.getNumeroMatricula())
        );

        try {
            messagingTemplate.convertAndSend("/topic/matriculas/aprobadas", convertirAResponseDTO(matriculaAprobada));
        } catch (Exception e) {
            log.error("Error al enviar notificación WebSocket", e);
        }

        return convertirAResponseDTO(matriculaAprobada);
    }

    public MatriculaResponseDTO rechazarMatricula(Long matriculaId, String motivo, Long procesadoPorId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        matricula.setEstado(EstadoMatricula.RECHAZADA);
        matricula.setFechaRechazo(LocalDateTime.now());
        matricula.setMotivoRechazo(motivo);
        matricula.setProcesadoPor(usuarioRepository.findById(procesadoPorId).orElse(null));
        matricula.setFechaProcesamiento(LocalDateTime.now());

        List<MatriculaCurso> cursos = matriculaCursoRepository.findByMatriculaId(matriculaId);
        for (MatriculaCurso mc : cursos) {
            Curso curso = mc.getCurso();
            curso.setCapacidadActual(Math.max(0, curso.getCapacidadActual() - 1));
            cursoRepository.save(curso);

            mc.setEstado(EstadoMatricula.RECHAZADA);
            matriculaCursoRepository.save(mc);
        }

        Matricula matriculaRechazada = matriculaRepository.save(matricula);
        recalcularMontos(matriculaRechazada);

        notificacionService.enviarNotificacion(
                matricula.getApoderado().getId(),
                "Matrícula Rechazada",
                String.format("La matrícula de %s ha sido RECHAZADA. Motivo: %s",
                        matricula.getEstudiante().getNombreCompleto(),
                        motivo)
        );

        try {
            messagingTemplate.convertAndSend("/topic/matriculas/rechazadas", convertirAResponseDTO(matriculaRechazada));
        } catch (Exception e) {
            log.error("Error al enviar notificación WebSocket", e);
        }

        return convertirAResponseDTO(matriculaRechazada);
    }

    @Transactional(readOnly = true)
    public Optional<MatriculaResponseDTO> obtenerPorId(Long id) {
        return matriculaRepository.findById(id)
                .map(m -> {
                    recalcularMontos(m);
                    return convertirAResponseDTO(m);
                });
    }


    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> obtenerMatriculasPorApoderado(Long apoderadoId) {
        return matriculaRepository.findByApoderadoId(apoderadoId).stream()
                .peek(this::recalcularMontos)
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> obtenerMatriculasPorEstudiante(Long estudianteId) {
        return matriculaRepository.findByEstudianteId(estudianteId).stream()
                .peek(this::recalcularMontos)
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> obtenerMatriculasPorEstudianteYAnio(Long estudianteId, String anio) {
        return matriculaRepository.findByEstudianteIdAndAnioAcademicoLike(estudianteId, anio)
                .stream()
                .peek(this::recalcularMontos)
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> obtenerAniosAcademicos() {
        return matriculaRepository.findDistinctAnioAcademico();
    }

    @Transactional(readOnly = true)
    public Page<MatriculaResponseDTO> obtenerTodas(Pageable pageable) {
        return matriculaRepository.findAll(pageable)
                .map(this::convertirAResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<MatriculaResponseDTO> obtenerMatriculasPorEstado(EstadoMatricula estado, Pageable pageable) {
        return matriculaRepository.findByEstado(estado, pageable)
                .map(this::convertirAResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<MatriculaResponseDTO> buscarMatriculas(String busqueda, Pageable pageable) {
        return matriculaRepository.buscarPorEstudianteONumero(busqueda, pageable)
                .map(this::convertirAResponseDTO);
    }


    private void inscribirEnCursos(Long matriculaId, List<Long> cursosIds) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        for (Long cursoId : cursosIds) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + cursoId));

            if (!curso.tieneCapacidadDisponible()) {
                throw new RuntimeException("El curso " + curso.getNombre() + " no tiene capacidad disponible");
            }

            if (matriculaCursoRepository.existsByMatriculaIdAndCursoId(matriculaId, cursoId)) continue;

            MatriculaCurso mc = new MatriculaCurso();
            mc.setMatricula(matricula);
            mc.setCurso(curso);
            mc.setCostoMatricula(curso.getCostoMatricula());
            mc.setCostoMensualidad(curso.getCostoMensualidad());
            mc.setCostoMateriales(curso.getCostoMateriales());
            matriculaCursoRepository.save(mc);

            curso.setCapacidadActual(curso.getCapacidadActual() + 1);
            cursoRepository.save(curso);
        }

        recalcularMontos(matricula);
    }

    private void recalcularMontos(Matricula matricula) {
        List<MatriculaCurso> cursos = matriculaCursoRepository.findByMatriculaId(matricula.getId());

        BigDecimal montoMatricula = BigDecimal.ZERO;
        BigDecimal montoMensualidad = BigDecimal.ZERO;
        BigDecimal montoMateriales = BigDecimal.ZERO;

        for (MatriculaCurso mc : cursos) {
            if (mc.getCostoMatricula() != null) montoMatricula = montoMatricula.add(mc.getCostoMatricula());
            if (mc.getCostoMensualidad() != null) montoMensualidad = montoMensualidad.add(mc.getCostoMensualidad());
            if (mc.getCostoMateriales() != null) montoMateriales = montoMateriales.add(mc.getCostoMateriales());
        }

        matricula.setMontoMatricula(montoMatricula);
        matricula.setMontoMensualidad(montoMensualidad);
        matricula.setMontoMateriales(montoMateriales);
        matricula.setMontoTotal(montoMatricula.add(montoMensualidad).add(montoMateriales));

        if (matricula.getDescuentoAplicado() != null) {
            matricula.setMontoFinal(matricula.getMontoTotal().subtract(matricula.getDescuentoAplicado()));
        } else {
            matricula.setMontoFinal(matricula.getMontoTotal());
        }

        matriculaRepository.save(matricula);
    }

    private MatriculaResponseDTO convertirAResponseDTO(Matricula matricula) {
        MatriculaResponseDTO dto = new MatriculaResponseDTO();
        dto.setId(matricula.getId());
        dto.setNumeroMatricula(matricula.getNumeroMatricula());
        dto.setEstudianteId(matricula.getEstudiante().getId());
        dto.setNombreEstudiante(matricula.getEstudiante().getNombreCompleto());
        dto.setDocumentoEstudiante(matricula.getEstudiante().getNumeroDocumento());
        dto.setApoderadoId(matricula.getApoderado().getId());
        dto.setNombreApoderado(matricula.getApoderado().getNombre());
        dto.setEmailApoderado(matricula.getApoderado().getEmail());
        dto.setAnioAcademico(matricula.getAnioAcademico());
        dto.setGrado(matricula.getGrado());
        dto.setSeccion(matricula.getSeccion());
        dto.setNivelAcademico(matricula.getNivelAcademico());
        dto.setEstado(matricula.getEstado());
        dto.setEstadoDescripcion(matricula.getEstado().getDescripcion());
        dto.setFechaSolicitud(matricula.getFechaSolicitud());
        dto.setFechaAprobacion(matricula.getFechaAprobacion());
        dto.setFechaRechazo(matricula.getFechaRechazo());
        dto.setFechaVencimiento(matricula.getFechaVencimiento());
        dto.setMontoTotal(matricula.getMontoTotal());
        dto.setMontoFinal(matricula.getMontoFinal());
        dto.setObservaciones(matricula.getObservaciones());
        dto.setMotivoRechazo(matricula.getMotivoRechazo());
        dto.setDocumentosCompletos(matricula.getDocumentosCompletos());
        dto.setRequiereEntrevista(matricula.getRequiereEntrevista());
        dto.setEntrevistaCompletada(matricula.getEntrevistaCompletada());
        dto.setPorcentajeCompletitud(matricula.getPorcentajeCompletitud());
        dto.setCreatedAt(matricula.getCreatedAt());
        dto.setPagosGenerados(matricula.getPagosGenerados());

        List<MatriculaCurso> cursos = matriculaCursoRepository.findByMatriculaId(matricula.getId());
        dto.setTotalCursos(cursos.size());
        dto.setCursos(cursos.stream().map(this::convertirCursoMatriculaDTO).collect(Collectors.toList()));

        return dto;
    }

    private CursoMatriculaDTO convertirCursoMatriculaDTO(MatriculaCurso mc) {
        CursoMatriculaDTO dto = new CursoMatriculaDTO();
        dto.setCursoId(mc.getCurso().getId());
        dto.setCodigoCurso(mc.getCurso().getCodigo());
        dto.setNombreCurso(mc.getCurso().getNombre());
        dto.setHorario(mc.getHorarioPersonalizado());
        dto.setAula(mc.getAulaAsignada() != null ? mc.getAulaAsignada() : mc.getCurso().getAula());
        dto.setEstado(mc.getEstado());
        dto.setCostoTotal(mc.getTotal());
        dto.setAprobado(mc.isAprobado());
        return dto;
    }
}