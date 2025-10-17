package com.colegiocesarvallejo.matriculas_platform.service.estudiante;

import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.ActualizarEstudianteDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.EstudianteResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.RegistroEstudianteDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoEstudiante;
import com.colegiocesarvallejo.matriculas_platform.entity.Estudiante;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import com.colegiocesarvallejo.matriculas_platform.repository.EstudianteRepository;
import com.colegiocesarvallejo.matriculas_platform.repository.UsuarioRepository;
import com.colegiocesarvallejo.matriculas_platform.service.notificaciones.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public EstudianteResponseDTO crearEstudiante(RegistroEstudianteDTO dto) {
        log.info("Creando nuevo estudiante: {} {}", dto.getNombres(), dto.getApellidos());

        Usuario apoderado = usuarioRepository.findById(dto.getApoderadoId())
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado"));

        if (estudianteRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un estudiante con el documento: " + dto.getNumeroDocumento());
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombres(dto.getNombres());
        estudiante.setApellidos(dto.getApellidos());
        estudiante.setNumeroDocumento(dto.getNumeroDocumento());
        estudiante.setTipoDocumento(dto.getTipoDocumento());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        estudiante.setGenero(dto.getGenero());
        estudiante.setDireccion(dto.getDireccion());
        estudiante.setTelefono(dto.getTelefono());
        estudiante.setEmail(dto.getEmail());
        estudiante.setApoderado(apoderado);
        estudiante.setNombreContactoEmergencia(dto.getNombreContactoEmergencia());
        estudiante.setTelefonoEmergencia(dto.getTelefonoEmergencia());
        estudiante.setAlergias(dto.getAlergias());
        estudiante.setCondicionesMedicas(dto.getCondicionesMedicas());
        estudiante.setTipoSangre(dto.getTipoSangre());
        estudiante.setCreatedBy(dto.getCreatedBy());

        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        try {
            notificacionService.enviarNotificacion(
                    apoderado.getId(),
                    "Estudiante Registrado",
                    "Se ha registrado exitosamente al estudiante " + estudiante.getNombreCompleto()
            );
        } catch (Exception e) {
            log.warn("Error al enviar notificación al apoderado {}: {}", apoderado.getId(), e.getMessage());
        }

        log.info("Estudiante creado exitosamente con ID: {}", estudianteGuardado.getId());
        return convertirAResponseDTO(estudianteGuardado);
    }

    public EstudianteResponseDTO actualizarEstudiante(Long id, ActualizarEstudianteDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setNombres(dto.getNombres());
        estudiante.setApellidos(dto.getApellidos());
        estudiante.setDireccion(dto.getDireccion());
        estudiante.setTelefono(dto.getTelefono());
        estudiante.setEmail(dto.getEmail());
        estudiante.setGradoActual(dto.getGradoActual());
        estudiante.setSeccionActual(dto.getSeccionActual());
        estudiante.setNombreContactoEmergencia(dto.getNombreContactoEmergencia());
        estudiante.setTelefonoEmergencia(dto.getTelefonoEmergencia());
        estudiante.setAlergias(dto.getAlergias());
        estudiante.setCondicionesMedicas(dto.getCondicionesMedicas());
        estudiante.setTipoSangre(dto.getTipoSangre());
        if (dto.getGradoActual() != null && !dto.getGradoActual().isBlank()) {
            estudiante.setGradoActual(dto.getGradoActual());
        }
        if (dto.getSeccionActual() != null && !dto.getSeccionActual().isBlank()) {
            estudiante.setSeccionActual(dto.getSeccionActual());
        }
        if (dto.getAñoAcademico() != null && !dto.getAñoAcademico().isBlank()) {
            estudiante.setAñoAcademico(dto.getAñoAcademico());
        }

        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

        try {
            notificacionService.enviarNotificacion(
                    estudiante.getApoderado().getId(),
                    "Estudiante Actualizado",
                    "Se ha actualizado el estudiante " + estudiante.getNombreCompleto()
            );
        } catch (Exception e) {
            log.warn("Error al enviar notificación al apoderado {}: {}", estudiante.getApoderado().getId(), e.getMessage());
        }

        return convertirAResponseDTO(estudianteActualizado);
    }

    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> obtenerEstudiantesPorApoderado(Long apoderadoId) {
        return estudianteRepository.findByApoderadoId(apoderadoId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EstudianteResponseDTO> buscarEstudiantes(String busqueda, Pageable pageable) {
        Page<Estudiante> estudiantes;
        if (busqueda == null || busqueda.trim().isEmpty()) {
            estudiantes = estudianteRepository.findAll(pageable);
        } else {
            estudiantes = estudianteRepository.buscarEstudiantes(busqueda, pageable);
        }
        return estudiantes.map(this::convertirAResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<EstudianteResponseDTO> obtenerEstudiantesPorApoderadoPaginado(Long apoderadoId, Pageable pageable) {
        return estudianteRepository.findByApoderadoId(apoderadoId, pageable)
                .map(this::convertirAResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> buscarEstudiantesPorApoderado(Long apoderadoId, String termino) {
        return estudianteRepository.findByApoderadoId(apoderadoId).stream()
                .filter(e -> e.getNombreCompleto().toLowerCase().contains(termino.toLowerCase()) ||
                        (e.getNumeroDocumento() != null && e.getNumeroDocumento().contains(termino)))
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EstudianteResponseDTO> obtenerPorId(Long id) {
        return estudianteRepository.findById(id).map(this::convertirAResponseDTO);
    }

    public void cambiarEstado(Long id, EstadoEstudiante nuevoEstado, String motivo) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        EstadoEstudiante estadoAnterior = estudiante.getEstadoEstudiante();
        estudiante.setEstadoEstudiante(nuevoEstado);
        estudianteRepository.save(estudiante);

        try {
            notificacionService.enviarNotificacion(
                    estudiante.getApoderado().getId(),
                    "Cambio de Estado",
                    String.format("El estado del estudiante %s ha cambiado de %s a %s. %s",
                            estudiante.getNombreCompleto(),
                            estadoAnterior != null ? estadoAnterior.getDescripcion() : "N/A",
                            nuevoEstado.getDescripcion(),
                            motivo != null ? "Motivo: " + motivo : "")
            );
        } catch (Exception e) {
            log.warn("Error al enviar notificación al apoderado {}: {}", estudiante.getApoderado().getId(), e.getMessage());
        }

        log.info("Estado del estudiante {} cambiado de {} a {}",
                estudiante.getNombreCompleto(), estadoAnterior, nuevoEstado);
    }

    private EstudianteResponseDTO convertirAResponseDTO(Estudiante estudiante) {
        EstudianteResponseDTO dto = new EstudianteResponseDTO();
        dto.setId(estudiante.getId());
        dto.setNombres(estudiante.getNombres());
        dto.setApellidos(estudiante.getApellidos());
        dto.setNombreCompleto(estudiante.getNombreCompleto());
        dto.setNumeroDocumento(estudiante.getNumeroDocumento());
        dto.setTipoDocumento(estudiante.getTipoDocumento() != null ? estudiante.getTipoDocumento().name() : null);
        dto.setFechaNacimiento(estudiante.getFechaNacimiento());
        dto.setEdad(estudiante.getEdad());
        dto.setGenero(estudiante.getGenero() != null ? estudiante.getGenero().name() : null);
        dto.setDireccion(estudiante.getDireccion());
        dto.setTelefono(estudiante.getTelefono());
        dto.setEmail(estudiante.getEmail());
        dto.setGradoActual(estudiante.getGradoActual());
        dto.setSeccionActual(estudiante.getSeccionActual());
        dto.setAñoAcademico(estudiante.getAñoAcademico());
        dto.setEstadoEstudiante(estudiante.getEstadoEstudiante());
        dto.setNombreContactoEmergencia(estudiante.getNombreContactoEmergencia());
        dto.setTelefonoEmergencia(estudiante.getTelefonoEmergencia());
        dto.setAlergias(estudiante.getAlergias());
        dto.setCondicionesMedicas(estudiante.getCondicionesMedicas());
        dto.setTipoSangre(estudiante.getTipoSangre());
        if (estudiante.getApoderado() != null) {
            dto.setApoderadoId(estudiante.getApoderado().getId());
            dto.setNombreApoderado(estudiante.getApoderado().getNombre());
            dto.setEmailApoderado(estudiante.getApoderado().getEmail());
        }
        dto.setCreatedAt(estudiante.getCreatedAt());
        return dto;
    }


}
