package com.colegiocesarvallejo.matriculas_platform.dto.dashboard;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResumenDTO {
    private Long id;
    private String numeroComprobante;
    private String concepto;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private Long diasParaVencimiento;
    private EstadoPago estado;
}
