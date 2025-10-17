package com.colegiocesarvallejo.matriculas_platform.dto.calificaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionResponseDTO {
    private Long id;
    private String nombreEstudiante;
    private String nombreCurso;
    private String nombreProfesor;
    private String añoAcademico;
    private String periodo;
    private String tipoEvaluacion;
    private String nombreEvaluacion;
    private String descripcion;
    private String notaFormateada;
    private BigDecimal notaNumerica;
    private String notaLiteral;
    private String notaConceptual;
    private BigDecimal notaMinimaAprobacion;
    private BigDecimal notaMaximaPosible;
    private BigDecimal pesoEvaluacion;
    private Boolean aprobado;
    private String estadoAcademico;
    private Boolean requiereRecuperacion;
    private Boolean esRecuperacion;
    private LocalDate fechaEvaluacion;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaPublicacion;
    private String observacionesProfesor;
    private String fortalezas;
    private String areasMejora;
    private BigDecimal porcentajeAsistencia;
    private Boolean publicado;
    private Boolean notificadoApoderado;
    private BigDecimal porcentajeNota;
}