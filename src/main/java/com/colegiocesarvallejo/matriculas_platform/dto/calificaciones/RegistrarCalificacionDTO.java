package com.colegiocesarvallejo.matriculas_platform.dto.calificaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarCalificacionDTO {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long profesorId;

    @NotBlank(message = "El año académico es obligatorio")
    private String añoAcademico;

    @NotBlank(message = "El período es obligatorio")
    private String periodo;

    @NotBlank(message = "El tipo de evaluación es obligatorio")
    private String tipoEvaluacion;

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    private String nombreEvaluacion;

    private String descripcion;

    @DecimalMin(value = "0.0", message = "La nota numérica debe ser mayor o igual a 0")
    private BigDecimal notaNumerica;

    private String notaLiteral;
    private String notaConceptual;

    @NotNull(message = "La nota mínima de aprobación es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima debe ser mayor o igual a 0")
    private BigDecimal notaMinimaAprobacion;

    @NotNull(message = "La nota máxima posible es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota máxima debe ser mayor o igual a 0")
    private BigDecimal notaMaximaPosible;

    @DecimalMin(value = "0.0", message = "El peso debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El peso no puede ser mayor a 100")
    private BigDecimal pesoEvaluacion;

    @NotNull(message = "La fecha de evaluación es obligatoria")
    private LocalDate fechaEvaluacion;

    private String observacionesProfesor;
    private String fortalezas;
    private String areasMejora;
    private Integer asistenciaClases;
    private Integer totalClases;
    private String competenciasDesarrolladas;
    private String habilidadesDestacadas;
    private String recomendaciones;
    private String createdBy;
}