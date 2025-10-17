package com.colegiocesarvallejo.matriculas_platform.dto.pagos;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import com.colegiocesarvallejo.matriculas_platform.entity.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private String numeroComprobante;
    private String nombreEstudiante;
    private String nombreApoderado;
    private TipoPago tipoPago;
    private String concepto;
    private String descripcion;
    private BigDecimal montoOriginal;
    private BigDecimal descuento;
    private BigDecimal recargo;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal montoPendiente;
    private EstadoPago estado;
    private String estadoDescripcion;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String numeroOperacion;
    private Boolean esPagoFraccionado;
    private Integer numeroCuota;
    private Integer totalCuotas;
    private Long diasVencimiento;
    private BigDecimal porcentajePagado;
    private LocalDateTime createdAt;
    private String grado;
    private String banco;
    private String observaciones;
}
