package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByEstudianteId(Long estudianteId);
    List<Calificacion> findByCursoId(Long cursoId);
    List<Calificacion> findByProfesorId(Long profesorId);

    List<Calificacion> findByEstudianteIdAndAnioAcademico(Long estudianteId, String anioAcademico);
    List<Calificacion> findByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);
    List<Calificacion> findByAnioAcademicoAndPeriodo(String anioAcademico, String periodo);
    List<Calificacion> findByCursoIdAndPeriodo(Long cursoId, String periodo);

    List<Calificacion> findByTipoEvaluacion(String tipoEvaluacion);
    List<Calificacion> findByCursoIdAndTipoEvaluacion(Long cursoId, String tipoEvaluacion);

    List<Calificacion> findByAprobado(Boolean aprobado);
    List<Calificacion> findByRequiereRecuperacion(Boolean requiereRecuperacion);
    List<Calificacion> findByPublicado(Boolean publicado);

    @Query("""
        SELECT c 
        FROM Calificacion c 
        WHERE c.estudiante.id = :estudianteId 
          AND c.curso.id = :cursoId 
          AND c.anioAcademico = :anio 
          AND c.periodo = :periodo
        """)
    List<Calificacion> encontrarCalificacionesPorEstudianteCursoPeriodo(
            @Param("estudianteId") Long estudianteId,
            @Param("cursoId") Long cursoId,
            @Param("anio") String anio,
            @Param("periodo") String periodo
    );

    @Query("""
        SELECT AVG(c.notaNumerica) 
        FROM Calificacion c 
        WHERE c.estudiante.id = :estudianteId 
          AND c.curso.id = :cursoId 
          AND c.publicado = true
        """)
    Double calcularPromedioEstudianteCurso(@Param("estudianteId") Long estudianteId,
                                           @Param("cursoId") Long cursoId);

    @Query("""
        SELECT AVG(c.notaNumerica) 
        FROM Calificacion c 
        WHERE c.estudiante.id = :estudianteId 
          AND c.anioAcademico = :anio 
          AND c.publicado = true
        """)
    Double calcularPromedioGeneralEstudiante(@Param("estudianteId") Long estudianteId,
                                             @Param("anio") String anio);

    @Query("""
        SELECT c 
        FROM Calificacion c 
        WHERE c.notificadoApoderado = false 
          AND c.publicado = true
        """)
    List<Calificacion> encontrarPendientesNotificacion();

    @Query("""
        SELECT COUNT(c) 
        FROM Calificacion c 
        WHERE c.aprobado = false 
          AND c.anioAcademico = :anio
        """)
    Long contarDesaprobadosPorAnio(@Param("anio") String anio);

    @Query("""
        SELECT c.curso.nombre, AVG(c.notaNumerica) 
        FROM Calificacion c 
        WHERE c.anioAcademico = :anio 
          AND c.publicado = true 
        GROUP BY c.curso.id, c.curso.nombre
        """)
    List<Object[]> obtenerPromediosPorCurso(@Param("anio") String anio);
}
