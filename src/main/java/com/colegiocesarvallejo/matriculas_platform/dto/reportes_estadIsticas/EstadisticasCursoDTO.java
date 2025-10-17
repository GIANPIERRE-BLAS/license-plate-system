package com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasCursoDTO {
    private Long cursoId;
    private String nombreCurso;
    private String codigoCurso;
    private String grado;
    private String seccion;
    private Integer capacidadMaxima;
    private Integer capacidadActual;
    private Double porcentajeOcupacion;
    private Long totalEstudiantes;
    private Double promedioGeneral;
    private Long estudiantesAprobados;
    private Long estudiantesDesaprobados;
    private Double porcentajeAprobacion;
}