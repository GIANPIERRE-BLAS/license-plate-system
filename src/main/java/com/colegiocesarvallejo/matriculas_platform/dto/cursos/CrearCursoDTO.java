package com.colegiocesarvallejo.matriculas_platform.dto.cursos;

import com.colegiocesarvallejo.matriculas_platform.entity.ModalidadCurso;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCursoDTO {

    @NotBlank(message = "El código del curso es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El nivel académico es obligatorio")
    private NivelAcademico nivelAcademico;

    @NotBlank(message = "El grado es obligatorio")
    @Size(max = 20, message = "El grado no puede exceder 20 caracteres")
    private String grado;

    @Size(max = 10, message = "La sección no puede exceder 10 caracteres")
    private String seccion;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    private Integer creditos;

    @NotNull(message = "Las horas semanales son obligatorias")
    @Min(value = 1, message = "Las horas semanales deben ser al menos 1")
    private Integer horasSemanales;

    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadCurso modalidad;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    private Integer capacidadMaxima;

    private Integer edadMinima;
    private Integer edadMaxima;

    @Size(max = 50, message = "Los días de la semana no pueden exceder 50 caracteres")
    private String diasSemana;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    @Size(max = 50, message = "El aula no puede exceder 50 caracteres")
    private String aula;

    @DecimalMin(value = "0.0", message = "El costo de matrícula debe ser mayor o igual a 0")
    private BigDecimal costoMatricula;

    @DecimalMin(value = "0.0", message = "El costo de mensualidad debe ser mayor o igual a 0")
    private BigDecimal costoMensualidad;

    @DecimalMin(value = "0.0", message = "El costo de materiales debe ser mayor o igual a 0")
    private BigDecimal costoMateriales;

    private Long profesorId;

    @NotBlank(message = "El año académico es obligatorio")
    @Size(max = 10, message = "El año académico no puede exceder 10 caracteres")
    private String añoAcademico;

    @Size(max = 20, message = "El período no puede exceder 20 caracteres")
    private String periodo;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String createdBy;
}