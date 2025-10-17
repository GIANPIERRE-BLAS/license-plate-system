package com.colegiocesarvallejo.matriculas_platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Usuario profesor;

    @Column(name = "anio_academico", nullable = false, length = 10)
    private String anioAcademico;

    @Column(name = "periodo", nullable = false, length = 20)
    private String periodo;

    @Column(name = "tipo_evaluacion", nullable = false, length = 50)
    private String tipoEvaluacion;

    @Column(name = "nombre_evaluacion", nullable = false, length = 100)
    private String nombreEvaluacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "nota_numerica", precision = 4, scale = 2)
    private BigDecimal notaNumerica;

    @Column(name = "nota_literal", length = 5)
    private String notaLiteral;

    @Column(name = "nota_conceptual", length = 50)
    private String notaConceptual;

    @Column(name = "nota_minima_aprobacion", precision = 4, scale = 2)
    private BigDecimal notaMinimaAprobacion;

    @Column(name = "nota_maxima_posible", precision = 4, scale = 2)
    private BigDecimal notaMaximaPosible;

    @Column(name = "peso_evaluacion", precision = 4, scale = 2)
    private BigDecimal pesoEvaluacion;

    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado = false;

    @Column(name = "requiere_recuperacion", nullable = false)
    private Boolean requiereRecuperacion = false;

    @Column(name = "es_recuperacion", nullable = false)
    private Boolean esRecuperacion = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calificacion_original_id")
    private Calificacion calificacionOriginal;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDate fechaEvaluacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "observaciones_profesor", columnDefinition = "TEXT")
    private String observacionesProfesor;

    @Column(name = "comentarios_estudiante", columnDefinition = "TEXT")
    private String comentariosEstudiante;

    @Column(name = "fortalezas", columnDefinition = "TEXT")
    private String fortalezas;

    @Column(name = "areas_mejora", columnDefinition = "TEXT")
    private String areasMejora;

    @Column(name = "asistencia_clases")
    private Integer asistenciaClases;

    @Column(name = "total_clases")
    private Integer totalClases;

    @Column(name = "porcentaje_asistencia", precision = 5, scale = 2)
    private BigDecimal porcentajeAsistencia;

    @Column(name = "competencias_desarrolladas", columnDefinition = "TEXT")
    private String competenciasDesarrolladas;

    @Column(name = "habilidades_destacadas", columnDefinition = "TEXT")
    private String habilidadesDestacadas;

    @Column(name = "recomendaciones", columnDefinition = "TEXT")
    private String recomendaciones;

    @Column(name = "publicado", nullable = false)
    private Boolean publicado = false;

    @Column(name = "notificado_apoderado", nullable = false)
    private Boolean notificadoApoderado = false;

    @Column(name = "fecha_notificacion_apoderado")
    private LocalDateTime fechaNotificacionApoderado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaRegistro = LocalDateTime.now();
        calcularAprobacion();
        calcularPorcentajeAsistencia();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calcularAprobacion();
        calcularPorcentajeAsistencia();
    }

    private void calcularAprobacion() {
        if (notaNumerica != null && notaMinimaAprobacion != null) {
            aprobado = notaNumerica.compareTo(notaMinimaAprobacion) >= 0;
            requiereRecuperacion = !aprobado && !esRecuperacion;
        }
    }

    private void calcularPorcentajeAsistencia() {
        if (asistenciaClases != null && totalClases != null && totalClases > 0) {
            porcentajeAsistencia = BigDecimal.valueOf(asistenciaClases)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalClases), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public String getNotaFormateada() {
        if (notaNumerica != null) {
            return notaNumerica.toString();
        } else if (notaLiteral != null) {
            return notaLiteral;
        } else if (notaConceptual != null) {
            return notaConceptual;
        }
        return "Sin calificar";
    }

    public String getEstadoAcademico() {
        if (aprobado) {
            return "Aprobado";
        } else if (requiereRecuperacion) {
            return "Requiere Recuperación";
        } else {
            return "Desaprobado";
        }
    }

    public BigDecimal getPorcentajeNota() {
        if (notaNumerica != null && notaMaximaPosible != null && notaMaximaPosible.compareTo(BigDecimal.ZERO) > 0) {
            return notaNumerica.multiply(BigDecimal.valueOf(100)).divide(notaMaximaPosible, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public boolean tieneAsistenciaSuficiente() {
        if (porcentajeAsistencia == null) return true;
        return porcentajeAsistencia.compareTo(BigDecimal.valueOf(70)) >= 0;
    }

    public boolean puedeRecuperar() {
        return requiereRecuperacion && !esRecuperacion && tieneAsistenciaSuficiente();
    }
}
