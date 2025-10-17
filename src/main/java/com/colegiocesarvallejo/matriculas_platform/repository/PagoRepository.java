package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import com.colegiocesarvallejo.matriculas_platform.entity.Pago;
import com.colegiocesarvallejo.matriculas_platform.entity.TipoPago;
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
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByNumeroComprobante(String numeroComprobante);
    List<Pago> findByEstudianteId(Long estudianteId);
    List<Pago> findByApoderadoId(Long apoderadoId);
    List<Pago> findByMatriculaId(Long matriculaId);

    List<Pago> findByEstado(EstadoPago estado);

    Page<Pago> findByEstado(EstadoPago estado, Pageable pageable);

    List<Pago> findByTipoPago(TipoPago tipoPago);
    List<Pago> findByEstadoAndTipoPago(EstadoPago estado, TipoPago tipoPago);

    List<Pago> findByFechaVencimientoLessThanEqualAndEstado(LocalDate fecha, EstadoPago estado);
    List<Pago> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);

    Page<Pago> findByApoderadoId(Long apoderadoId, Pageable pageable);
    Page<Pago> findByEstudianteId(Long estudianteId, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.apoderado.id = :apoderadoId AND p.estado IN :estados")
    List<Pago> encontrarPorApoderadoYEstados(@Param("apoderadoId") Long apoderadoId,
                                             @Param("estados") List<EstadoPago> estados);

    @Query("SELECT SUM(p.montoTotal) FROM Pago p WHERE p.estado = 'PAGADO' AND p.fechaPago BETWEEN :inicio AND :fin")
    Double calcularIngresosPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                      @Param("fin") LocalDateTime fin);

    @Query("SELECT p.tipoPago, COUNT(p), SUM(p.montoTotal) " +
            "FROM Pago p WHERE p.fechaEmision BETWEEN :inicio AND :fin GROUP BY p.tipoPago")
    List<Object[]> obtenerEstadisticasPorTipo(@Param("inicio") LocalDate inicio,
                                              @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.fechaVencimiento < CURRENT_DATE AND p.estado = 'PENDIENTE'")
    Long contarPagosVencidos();

    @Query("SELECT SUM(p.montoPendiente) FROM Pago p " +
            "WHERE p.apoderado.id = :apoderadoId " +
            "AND p.estado != 'PAGADO' " +
            "AND p.montoPendiente > 0.05")
    Double calcularDeudaTotalApoderado(@Param("apoderadoId") Long apoderadoId);

    @Query("SELECT p FROM Pago p WHERE p.estado = 'VENCIDO' AND p.fechaVencimiento <= :fecha")
    List<Pago> encontrarPagosVencidosHasta(@Param("fecha") LocalDate fecha);

    @Query("SELECT new com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoDTO(" +
            "p.id, p.concepto, p.descripcion, p.montoTotal, p.montoPendiente, p.estado, " +
            "p.numeroComprobante, p.fechaVencimiento, p.fechaPago, p.metodoPago, " +
            "CONCAT(m.estudiante.nombres, ' ', m.estudiante.apellidos), m.grado) " +
            "FROM Pago p JOIN p.matricula m WHERE p.apoderado.id = :apoderadoId")
    List<PagoDTO> findPagosByApoderadoId(@Param("apoderadoId") Long apoderadoId);

    boolean existsByNumeroComprobante(String numeroComprobante);
    boolean existsByMatriculaId(Long matriculaId);
}