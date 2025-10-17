package com.colegiocesarvallejo.matriculas_platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10, unique = true)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_academico", nullable = false)
    private NivelAcademico nivelAcademico;

    @Column(nullable = false, length = 20)
    private String grado;

    @Column(length = 10)
    private String seccion;

    @Column(nullable = false)
    private Integer creditos = 0;

    @Column(name = "horas_semanales", nullable = false)
    private Integer horasSemanales = 0;

    @Column(name = "edad_minima")
    private Integer edadMinima;

    @Column(name = "edad_maxima")
    private Integer edadMaxima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadCurso modalidad = ModalidadCurso.PRESENCIAL;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "capacidad_actual", nullable = false)
    private Integer capacidadActual = 0;

    @Column(name = "dias_semana", length = 50)
    private String diasSemana;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(length = 50)
    private String aula;

    @Column(name = "costo_matricula", precision = 10, scale = 2)
    private BigDecimal costoMatricula;

    @Column(name = "costo_mensualidad", precision = 10, scale = 2)
    private BigDecimal costoMensualidad;

    @Column(name = "costo_materiales", precision = 10, scale = 2)
    private BigDecimal costoMateriales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @Column(name = "anio_academico", nullable = false, length = 10)
    private String anioAcademico;

    @Column(name = "periodo", length = 20)
    private String periodo;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "permite_matricula", nullable = false)
    private Boolean permiteMatricula = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatriculaCurso> matriculas;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Calificacion> calificaciones;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean tieneCapacidadDisponible() {
        return capacidadActual < capacidadMaxima;
    }

    public Integer getCapacidadDisponible() {
        return capacidadMaxima - capacidadActual;
    }

    public String getHorarioCompleto() {
        StringBuilder sb = new StringBuilder();
        if (diasSemana != null) sb.append(diasSemana);
        if (horaInicio != null && horaFin != null) sb.append(" ").append(horaInicio).append(" - ").append(horaFin);
        return sb.toString().trim();
    }
}
