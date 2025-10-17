package com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFinancieroDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalIngresos;
    private BigDecimal totalPendiente;
    private BigDecimal totalVencido;
    private Long totalPagosProcesados;
    private Long pagosPendientes;
    private Long pagosVencidos;
    private List<ResumenPorTipoDTO> resumenPorTipo;
    private List<ResumenPorFechaDTO> resumenPorFecha;
}