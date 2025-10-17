package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoEstudiante;
import com.colegiocesarvallejo.matriculas_platform.entity.Estudiante;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);
    List<Estudiante> findByApoderado(Usuario apoderado);
    List<Estudiante> findByApoderadoId(Long apoderadoId);

    List<Estudiante> findByGradoActualAndSeccionActual(String grado, String seccion);
    List<Estudiante> findByGradoActual(String grado);
    List<Estudiante> findByAñoAcademico(String añoAcademico);

    List<Estudiante> findByEstadoEstudiante(EstadoEstudiante estado);
    List<Estudiante> findByEstadoEstudianteAndAñoAcademico(EstadoEstudiante estado, String añoAcademico);

    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);

    Page<Estudiante> findByApoderadoId(Long apoderadoId, Pageable pageable);
    Page<Estudiante> findByGradoActualContainingIgnoreCase(String grado, Pageable pageable);

    @Query("""
        SELECT e 
        FROM Estudiante e 
        WHERE LOWER(CONCAT(e.nombres, ' ', e.apellidos)) LIKE LOWER(CONCAT('%', :busqueda, '%')) 
           OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :busqueda, '%'))
        """)
    Page<Estudiante> buscarEstudiantes(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("""
        SELECT e 
        FROM Estudiante e 
        WHERE e.fechaNacimiento BETWEEN :fechaInicio AND :fechaFin
        """)
    List<Estudiante> encontrarPorRangoEdad(@Param("fechaInicio") LocalDate fechaInicio,
                                           @Param("fechaFin") LocalDate fechaFin);

    @Query("""
        SELECT COUNT(e) 
        FROM Estudiante e 
        WHERE e.gradoActual = :grado AND e.estadoEstudiante = 'ACTIVO'
        """)
    Long contarEstudiantesActivosPorGrado(@Param("grado") String grado);

    @Query("""
        SELECT e.gradoActual, COUNT(e) 
        FROM Estudiante e 
        WHERE e.estadoEstudiante = 'ACTIVO' 
        GROUP BY e.gradoActual
        """)
    List<Object[]> obtenerEstadisticasPorGrado();

    Long countByEstadoEstudiante(EstadoEstudiante estado);
    Long countByGradoActual(String grado);
}
