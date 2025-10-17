package com.colegiocesarvallejo.matriculas_platform.dto.pagos;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private String concepto;
    private String descripcion;
    private BigDecimal montoTotal;
    private BigDecimal montoPendiente;
    private EstadoPago estado;
    private String numeroComprobante;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estudiante;
    private String grado;
}