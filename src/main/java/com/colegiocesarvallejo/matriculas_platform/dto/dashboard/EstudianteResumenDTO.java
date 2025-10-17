package com.colegiocesarvallejo.matriculas_platform.dto.dashboard;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoEstudiante;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoMatricula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResumenDTO {
    private Long id;
    private String nombreCompleto;
    private String grado;
    private String seccion;
    private EstadoEstudiante estado;
    private String numeroMatricula;
    private EstadoMatricula estadoMatricula;
    private Double promedioGeneral;
    private BigDecimal deudaPendiente;
}
