package com.colegiocesarvallejo.matriculas_platform.dto.matriculas;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoMatricula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoMatriculaDTO {
    private Long cursoId;
    private String codigoCurso;
    private String nombreCurso;
    private String horario;
    private String aula;
    private EstadoMatricula estado;
    private BigDecimal costoTotal;
    private Boolean aprobado;
}