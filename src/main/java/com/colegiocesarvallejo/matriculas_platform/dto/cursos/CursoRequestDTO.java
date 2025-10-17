package com.colegiocesarvallejo.matriculas_platform.dto.cursos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CursoRequestDTO {

    private String nombre;
    private String codigo;
    private String nivelAcademico;
    private String grado;
    private String seccion;
    private Integer creditos;
    private Integer horasSemanales;
    private Integer edadMinima;
    private Integer edadMaxima;
    private BigDecimal costoMatricula;
    private BigDecimal costoMensualidad;
    private BigDecimal costoMateriales;
    private String modalidad;
    private Integer capacidadMaxima;
    private Integer capacidadActual;
    private String diasSemana;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    private String aula;
    private Long profesorId;
    private String descripcion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    private String anioAcademico;
    private String periodo;
    private Boolean activo;
    private Boolean permiteMatricula;
    private String createdBy;
}
