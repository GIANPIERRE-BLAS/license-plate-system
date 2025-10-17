package com.colegiocesarvallejo.matriculas_platform.service.curso;

import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoRequestDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.Curso;
import com.colegiocesarvallejo.matriculas_platform.entity.ModalidadCurso;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import com.colegiocesarvallejo.matriculas_platform.entity.Profesor;
import com.colegiocesarvallejo.matriculas_platform.repository.CursoRepository;
import com.colegiocesarvallejo.matriculas_platform.repository.ProfesorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerCursosConProfesor() {
        return cursoRepository.findAllWithProfesor()
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerTodos() {
        return cursoRepository.findAllWithProfesor()
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));
        if (curso.getProfesor() != null) curso.getProfesor().getNombre();
        return new CursoResponseDTO(curso);
    }

    @Override
    @Transactional
    public CursoResponseDTO crearCurso(CursoRequestDTO dto) {
        Curso curso = new Curso();
        mapToEntity(dto, curso, true);
        Curso guardado = cursoRepository.save(curso);
        return new CursoResponseDTO(guardado);
    }

    @Override
    @Transactional
    public CursoResponseDTO actualizarCurso(Long id, CursoRequestDTO dto) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));
        mapToEntity(dto, curso, false);
        Curso actualizado = cursoRepository.save(curso);
        return new CursoResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminarCurso(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));
        boolean tieneMatriculas = curso.getMatriculas() != null && !curso.getMatriculas().isEmpty();

        if (tieneMatriculas) {
            curso.setActivo(false);
            curso.setPermiteMatricula(false);
            cursoRepository.save(curso);
        } else {

            cursoRepository.delete(curso);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerCursosDisponibles() {
        return cursoRepository.encontrarCursosDisponibles()
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> findCursosByNivel(NivelAcademico nivel) {
        return cursoRepository.findByNivelAcademicoAndActivoTrueAndPermiteMatriculaTrue(nivel)
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerCursosPorNivelYGrado(NivelAcademico nivel, String grado) {
        return cursoRepository.encontrarCursosDisponiblesPorNivelYGrado(nivel, grado)
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> obtenerCursosPorAnio(String anio) {
        return cursoRepository.findCursosPorAnio(anio)
                .stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CursoResponseDTO> buscarCursos(String busqueda, Pageable pageable) {
        return cursoRepository.buscarCursos(busqueda, pageable)
                .map(CursoResponseDTO::new);
    }


    private void mapToEntity(CursoRequestDTO dto, Curso curso, boolean isNew) {
        if (dto.getCodigo() != null) curso.setCodigo(dto.getCodigo());
        if (dto.getNombre() != null) curso.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) curso.setDescripcion(dto.getDescripcion());

        if (dto.getNivelAcademico() != null) {
            try {
                curso.setNivelAcademico(NivelAcademico.valueOf(dto.getNivelAcademico().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Nivel académico inválido: " + dto.getNivelAcademico());
            }
        }

        if (dto.getGrado() != null) curso.setGrado(dto.getGrado());
        if (dto.getSeccion() != null) curso.setSeccion(dto.getSeccion());
        if (dto.getCreditos() != null) curso.setCreditos(dto.getCreditos());
        if (dto.getHorasSemanales() != null) curso.setHorasSemanales(dto.getHorasSemanales());
        if (dto.getEdadMinima() != null) curso.setEdadMinima(dto.getEdadMinima());
        if (dto.getEdadMaxima() != null) curso.setEdadMaxima(dto.getEdadMaxima());

        if (dto.getCostoMatricula() != null) curso.setCostoMatricula(dto.getCostoMatricula());
        if (dto.getCostoMensualidad() != null) curso.setCostoMensualidad(dto.getCostoMensualidad());
        if (dto.getCostoMateriales() != null) curso.setCostoMateriales(dto.getCostoMateriales());

        if (dto.getModalidad() != null) {
            try {
                curso.setModalidad(ModalidadCurso.valueOf(dto.getModalidad().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Modalidad inválida: " + dto.getModalidad());
            }
        }

        if (dto.getCapacidadMaxima() != null) curso.setCapacidadMaxima(dto.getCapacidadMaxima());
        if (dto.getCapacidadActual() != null) curso.setCapacidadActual(dto.getCapacidadActual());
        if (dto.getDiasSemana() != null) curso.setDiasSemana(dto.getDiasSemana());
        if (dto.getHoraInicio() != null) curso.setHoraInicio(dto.getHoraInicio());
        if (dto.getHoraFin() != null) curso.setHoraFin(dto.getHoraFin());
        if (dto.getAula() != null) curso.setAula(dto.getAula());
        if (dto.getAnioAcademico() != null) curso.setAnioAcademico(dto.getAnioAcademico());
        if (dto.getPeriodo() != null) curso.setPeriodo(dto.getPeriodo());
        if (dto.getActivo() != null) curso.setActivo(dto.getActivo());
        if (dto.getPermiteMatricula() != null) curso.setPermiteMatricula(dto.getPermiteMatricula());
        if (dto.getCreatedBy() != null) curso.setCreatedBy(dto.getCreatedBy());

        if (dto.getProfesorId() != null) {
            Profesor profesor = profesorRepository.findById(dto.getProfesorId())
                    .orElseThrow(() -> new RuntimeException("Profesor no encontrado con id: " + dto.getProfesorId()));
            curso.setProfesor(profesor);
        }

    if (dto.getFechaInicio() != null) curso.setFechaInicio(dto.getFechaInicio());
    if (dto.getFechaFin() != null) curso.setFechaFin(dto.getFechaFin());
    }
}
