package com.colegiocesarvallejo.matriculas_platform.dto.cursos;

import com.colegiocesarvallejo.matriculas_platform.entity.Curso;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CursoResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NivelAcademico nivelAcademico;

    private String grado;
    private String seccion;
    private Integer creditos;
    private Integer horasSemanales;
    private String modalidad;
    private Integer capacidadMaxima;
    private Integer capacidadActual;
    private Integer capacidadDisponible;
    private String diasSemana;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    private String aula;
    private String horarioCompleto;

    private BigDecimal costoMatricula;
    private BigDecimal costoMensualidad;
    private BigDecimal costoMateriales;
    private BigDecimal costoTotal;

    private String nombreProfesor;
    private Long profesorId;

    private String anioAcademico;
    private String periodo;
    private Boolean activo;
    private Boolean permiteMatricula;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public CursoResponseDTO() {}

    public CursoResponseDTO(Curso curso) {
        this.id = curso.getId();
        this.codigo = curso.getCodigo() != null ? curso.getCodigo() : "-";
        this.nombre = curso.getNombre() != null ? curso.getNombre() : "-";
        this.descripcion = curso.getDescripcion() != null ? curso.getDescripcion() : "-";
        this.nivelAcademico = curso.getNivelAcademico();
        this.grado = curso.getGrado() != null ? curso.getGrado() : "-";
        this.seccion = curso.getSeccion() != null ? curso.getSeccion() : "-";
        this.creditos = curso.getCreditos() != null ? curso.getCreditos() : 0;
        this.horasSemanales = curso.getHorasSemanales() != null ? curso.getHorasSemanales() : 0;
        this.modalidad = curso.getModalidad() != null ? curso.getModalidad().name() : "-";
        this.capacidadMaxima = curso.getCapacidadMaxima() != null ? curso.getCapacidadMaxima() : 0;
        this.capacidadActual = curso.getCapacidadActual() != null ? curso.getCapacidadActual() : 0;
        this.capacidadDisponible = curso.getCapacidadDisponible() != null ? curso.getCapacidadDisponible() : 0;
        this.diasSemana = curso.getDiasSemana() != null ? curso.getDiasSemana() : "-";
        this.horaInicio = curso.getHoraInicio();
        this.horaFin = curso.getHoraFin();
        this.aula = curso.getAula() != null ? curso.getAula() : "-";
        this.horarioCompleto = curso.getHorarioCompleto() != null ? curso.getHorarioCompleto() : "-";
        this.costoMatricula = curso.getCostoMatricula() != null ? curso.getCostoMatricula() : BigDecimal.ZERO;
        this.costoMensualidad = curso.getCostoMensualidad() != null ? curso.getCostoMensualidad() : BigDecimal.ZERO;
        this.costoMateriales = curso.getCostoMateriales() != null ? curso.getCostoMateriales() : BigDecimal.ZERO;
        this.costoTotal = this.costoMatricula.add(this.costoMensualidad).add(this.costoMateriales);
        if (curso.getProfesor() != null) {
            this.nombreProfesor = curso.getProfesor().getNombre();
            this.profesorId = curso.getProfesor().getId();
        } else {
            this.nombreProfesor = "Sin asignar";
            this.profesorId = null;
        }
        this.anioAcademico = curso.getAnioAcademico() != null ? curso.getAnioAcademico() : "-";
        this.periodo = curso.getPeriodo() != null ? curso.getPeriodo() : "-";
        this.activo = curso.getActivo() != null ? curso.getActivo() : false;
        this.permiteMatricula = curso.getPermiteMatricula() != null ? curso.getPermiteMatricula() : false;
        this.fechaInicio = curso.getFechaInicio();
        this.fechaFin = curso.getFechaFin();
        this.createdAt = curso.getCreatedAt();
    }
}
