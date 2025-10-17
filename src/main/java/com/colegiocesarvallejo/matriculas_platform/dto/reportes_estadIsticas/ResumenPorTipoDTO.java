package com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas;

import com.colegiocesarvallejo.matriculas_platform.entity.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPorTipoDTO {
    private TipoPago tipoPago;
    private Long cantidad;
    private BigDecimal montoTotal;
    private Double porcentaje;
}