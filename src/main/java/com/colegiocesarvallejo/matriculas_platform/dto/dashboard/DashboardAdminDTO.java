package com.colegiocesarvallejo.matriculas_platform.dto.dashboard;

import com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas.EstadisticasCursoDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas.EstadisticasMatriculaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdminDTO {
    private Long totalEstudiantes;
    private Long totalMatriculas;
    private Long matriculasPendientes;
    private Long cursosActivos;
    private BigDecimal ingresosMes;
    private BigDecimal deudaTotal;
    private Long pagosVencidos;
    private Double ocupacionPromedio;
    private List<AlertaDTO> alertas;
    private EstadisticasMatriculaDTO estadisticasMatricula;
    private List<EstadisticasCursoDTO> cursosPopulares;
}
