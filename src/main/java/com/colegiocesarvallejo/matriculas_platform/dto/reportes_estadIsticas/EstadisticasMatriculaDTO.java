package com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasMatriculaDTO implements Serializable {
    private Long totalMatriculas;
    private Long totalAprobadas;
    private Long totalReprobadas;
    private Long totalPendientes;
    private String anioAcademico;
    private Double porcentajeAprobacion;
}
