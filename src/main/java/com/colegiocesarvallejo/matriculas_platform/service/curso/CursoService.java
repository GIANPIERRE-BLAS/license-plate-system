package com.colegiocesarvallejo.matriculas_platform.service.curso;

import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoRequestDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CursoService {

    List<CursoResponseDTO> obtenerTodos();

    CursoResponseDTO obtenerPorId(Long id);

    CursoResponseDTO crearCurso(CursoRequestDTO dto);

    CursoResponseDTO actualizarCurso(Long id, CursoRequestDTO dto);

    void eliminarCurso(Long id);

    List<CursoResponseDTO> obtenerCursosDisponibles();

    List<CursoResponseDTO> findCursosByNivel(NivelAcademico nivel);

    List<CursoResponseDTO> obtenerCursosPorNivelYGrado(NivelAcademico nivel, String grado);

    List<CursoResponseDTO> obtenerCursosConProfesor();

    Page<CursoResponseDTO> buscarCursos(String busqueda, Pageable pageable);
}
