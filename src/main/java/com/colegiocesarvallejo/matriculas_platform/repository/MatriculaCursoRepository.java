package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface MatriculaCursoRepository extends JpaRepository<MatriculaCurso, Long> {

    List<MatriculaCurso> findByMatriculaId(Long matriculaId);
    List<MatriculaCurso> findByCursoId(Long cursoId);
    List<MatriculaCurso> findByEstado(EstadoMatricula estado);

    @Query("SELECT mc FROM MatriculaCurso mc WHERE mc.matricula.estudiante.id = :estudianteId")
    List<MatriculaCurso> encontrarPorEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT mc FROM MatriculaCurso mc WHERE mc.curso.anioAcademico = :anio AND mc.estado = :estado")
    List<MatriculaCurso> encontrarPorAnioYEstado(@Param("anio") String anio, @Param("estado") EstadoMatricula estado);

    @Query("SELECT mc.curso.id, COUNT(mc) FROM MatriculaCurso mc WHERE mc.estado = 'APROBADA' GROUP BY mc.curso.id")
    List<Object[]> obtenerOcupacionPorCurso();

    @Query("SELECT SUM(mc.total) FROM MatriculaCurso mc WHERE mc.matricula.id = :matriculaId")
    Double calcularTotalMatricula(@Param("matriculaId") Long matriculaId);

    boolean existsByMatriculaIdAndCursoId(Long matriculaId, Long cursoId);
}