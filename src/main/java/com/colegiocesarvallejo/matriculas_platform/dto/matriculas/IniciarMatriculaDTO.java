package com.colegiocesarvallejo.matriculas_platform.dto.matriculas;

import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniciarMatriculaDTO {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotBlank(message = "El año académico es obligatorio")
    @Size(max = 10, message = "El año académico no puede exceder 10 caracteres")
    private String anioAcademico;

    @NotBlank(message = "El grado es obligatorio")
    @Size(max = 20, message = "El grado no puede exceder 20 caracteres")
    private String grado;

    @Size(max = 10, message = "La sección no puede exceder 10 caracteres")
    private String seccion;

    @NotNull(message = "El nivel académico es obligatorio")
    private NivelAcademico nivelAcademico;

    private String observaciones;
    private String comentariosApoderado;

    @NotNull(message = "Debe seleccionar al menos un curso")
    @Size(min = 1, message = "Debe seleccionar al menos un curso")
    private List<Long> cursosIds;

    private String createdBy;
}