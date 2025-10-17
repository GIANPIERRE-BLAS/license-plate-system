package com.colegiocesarvallejo.matriculas_platform.dto.matriculas;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoMatricula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoMatriculaDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoMatricula nuevoEstado;

    private String observaciones;
    private String motivoRechazo;

    @NotNull(message = "El ID del usuario que procesa es obligatorio")
    private Long procesadoPorId;
}