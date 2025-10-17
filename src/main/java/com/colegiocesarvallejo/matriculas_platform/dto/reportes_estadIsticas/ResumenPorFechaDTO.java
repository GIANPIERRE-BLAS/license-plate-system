package com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPorFechaDTO {
    private LocalDate fecha;
    private Long cantidadPagos;
    private BigDecimal montoTotal;
}
