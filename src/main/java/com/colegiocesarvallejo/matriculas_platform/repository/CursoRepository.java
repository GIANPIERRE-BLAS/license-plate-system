package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.Curso;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByCodigo(String codigo);
    List<Curso> findByProfesorId(Long profesorId);
    List<Curso> findByNivelAcademicoAndActivoTrueAndPermiteMatriculaTrue(NivelAcademico nivel);
    List<Curso> findByNivelAcademicoAndGrado(NivelAcademico nivel, String grado);
    List<Curso> findByNivelAcademicoAndGradoAndSeccion(NivelAcademico nivel, String grado, String seccion);
    List<Curso> findByAnioAcademico(String anioAcademico);
    List<Curso> findByAnioAcademicoAndPeriodo(String anioAcademico, String periodo);
    List<Curso> findByActivoTrue();
    List<Curso> findByPermiteMatriculaTrue();
    List<Curso> findByActivoTrueAndPermiteMatriculaTrue();

    @Query("SELECT c FROM Curso c LEFT JOIN FETCH c.profesor")
    List<Curso> findAllWithProfesor();

    @Query("""
        SELECT c 
        FROM Curso c LEFT JOIN FETCH c.profesor 
        WHERE COALESCE(c.capacidadActual, 0) < COALESCE(c.capacidadMaxima, 0) 
          AND c.activo = true 
          AND c.permiteMatricula = true
        """)
    List<Curso> encontrarCursosDisponibles();

    @Query("""
        SELECT c 
        FROM Curso c LEFT JOIN FETCH c.profesor 
        WHERE c.nivelAcademico = :nivel 
          AND c.grado = :grado 
          AND c.activo = true 
          AND c.permiteMatricula = true
        """)
    List<Curso> encontrarCursosDisponiblesPorNivelYGrado(@Param("nivel") NivelAcademico nivel,
                                                         @Param("grado") String grado);

    @Query("""
        SELECT c 
        FROM Curso c 
        WHERE c.nombre LIKE %:busqueda% 
           OR c.codigo LIKE %:busqueda% 
           OR c.descripcion LIKE %:busqueda%
        """)
    Page<Curso> buscarCursos(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("""
        SELECT c.nivelAcademico, c.grado, COUNT(c) 
        FROM Curso c 
        WHERE c.activo = true 
        GROUP BY c.nivelAcademico, c.grado
        """)
    List<Object[]> obtenerOfertaAcademicaPorNivelYGrado();

    boolean existsByCodigo(String codigo);

    @Query("""
        SELECT c 
        FROM Curso c LEFT JOIN FETCH c.profesor 
        WHERE c.anioAcademico LIKE :anio% 
          AND c.activo = true 
          AND c.permiteMatricula = true
        """)
    List<Curso> findCursosPorAnio(@Param("anio") String anio);
}
