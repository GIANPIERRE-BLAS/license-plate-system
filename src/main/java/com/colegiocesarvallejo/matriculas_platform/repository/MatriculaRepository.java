package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByNumeroMatricula(String numeroMatricula);
    List<Matricula> findByEstudianteId(Long estudianteId);
    List<Matricula> findByApoderadoId(Long apoderadoId);

    List<Matricula> findByEstado(EstadoMatricula estado);
    List<Matricula> findByEstadoAndAnioAcademico(EstadoMatricula estado, String anioAcademico);

    List<Matricula> findByAnioAcademico(String anioAcademico);
    List<Matricula> findByAnioAcademicoAndNivelAcademico(String anioAcademico, NivelAcademico nivel);

    List<Matricula> findByFechaSolicitudBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Matricula> findByFechaVencimientoLessThanEqualAndEstado(LocalDate fecha, EstadoMatricula estado);

    Page<Matricula> findByApoderadoId(Long apoderadoId, Pageable pageable);
    Page<Matricula> findByEstado(EstadoMatricula estado, Pageable pageable);
    Page<Matricula> findByAnioAcademico(String anioAcademico, Pageable pageable);

    @Query("SELECT m FROM Matricula m WHERE m.apoderado.id = :apoderadoId AND m.anioAcademico = :anio")
    List<Matricula> encontrarMatriculasPorApoderadoYAnio(@Param("apoderadoId") Long apoderadoId,
                                                         @Param("anio") String anio);

    @Query("SELECT m FROM Matricula m WHERE m.estado IN :estados AND m.anioAcademico = :anio")
    List<Matricula> encontrarPorEstadosYAnio(@Param("estados") List<EstadoMatricula> estados,
                                             @Param("anio") String anio);

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.estado = :estado AND m.anioAcademico = :anio")
    Long contarMatriculasPorEstadoYAnio(@Param("estado") EstadoMatricula estado, @Param("anio") String anio);

    @Query("SELECT m.estado, COUNT(m) FROM Matricula m WHERE m.anioAcademico = :anio GROUP BY m.estado")
    List<Object[]> obtenerEstadisticasMatriculasPorAnio(@Param("anio") String anio);

    @Query("SELECT m FROM Matricula m WHERE m.documentosCompletos = false AND m.estado = 'PENDIENTE'")
    List<Matricula> encontrarMatriculasConDocumentosPendientes();

    @Query("SELECT m FROM Matricula m WHERE m.fechaVencimiento <= CURRENT_DATE AND m.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<Matricula> encontrarMatriculasVencidas();

    @Query("SELECT m FROM Matricula m WHERE m.estudiante.id = :estudianteId AND m.anioAcademico LIKE CONCAT(:anio, '%')")
    List<Matricula> findByEstudianteIdAndAnioAcademicoLike(@Param("estudianteId") Long estudianteId,
                                                           @Param("anio") String anio);

    @Query("SELECT DISTINCT m.anioAcademico FROM Matricula m ORDER BY m.anioAcademico DESC")
    List<String> findDistinctAnioAcademico();

    @Query("SELECT m FROM Matricula m " +
            "JOIN m.estudiante e " +
            "WHERE LOWER(CONCAT(e.nombres, ' ', e.apellidos)) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "   OR LOWER(m.numeroMatricula) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Matricula> buscarPorEstudianteONumero(@Param("busqueda") String busqueda, Pageable pageable);


    boolean existsByNumeroMatricula(String numeroMatricula);
    boolean existsByEstudianteIdAndAnioAcademico(Long estudianteId, String anioAcademico);

}
