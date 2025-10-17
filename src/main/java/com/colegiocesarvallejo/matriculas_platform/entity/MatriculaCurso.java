package com.colegiocesarvallejo.matriculas_platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "matricula_cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado = EstadoMatricula.PENDIENTE;

    @Column(name = "costo_matricula", precision = 10, scale = 2)
    private BigDecimal costoMatricula;

    @Column(name = "costo_mensualidad", precision = 10, scale = 2)
    private BigDecimal costoMensualidad;

    @Column(name = "costo_materiales", precision = 10, scale = 2)
    private BigDecimal costoMateriales;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_inicio_clases")
    private LocalDateTime fechaInicioClases;

    @Column(name = "requiere_requisitos", nullable = false)
    private Boolean requiereRequisitos = false;

    @Column(name = "requisitos_cumplidos", nullable = false)
    private Boolean requisitosCumplidos = false;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "horario_personalizado")
    private String horarioPersonalizado;

    @Column(name = "aula_asignada")
    private String aulaAsignada;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaInscripcion = LocalDateTime.now();
        if (curso != null) {
            if (costoMatricula == null) costoMatricula = curso.getCostoMatricula();
            if (costoMensualidad == null) costoMensualidad = curso.getCostoMensualidad();
            if (costoMateriales == null) costoMateriales = curso.getCostoMateriales();
            calcularTotal();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calcularTotal();
    }

    private void calcularTotal() {
        total = BigDecimal.ZERO;

        if (costoMatricula != null) {
            total = total.add(costoMatricula);
        }
        if (costoMensualidad != null) {
            total = total.add(costoMensualidad);
        }
        if (costoMateriales != null) {
            total = total.add(costoMateriales);
        }
        if (descuento != null) {
            total = total.subtract(descuento);
        }
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
    }

    public boolean isAprobado() {
        return estado == EstadoMatricula.APROBADA || estado == EstadoMatricula.COMPLETADA;
    }

    public boolean isPendiente() {
        return estado == EstadoMatricula.PENDIENTE;
    }

    public boolean puedeIniciarClases() {
        return isAprobado() && (!requiereRequisitos || requisitosCumplidos);
    }

    public String getEstadoDescriptivo() {
        if (isPendiente() && requiereRequisitos && !requisitosCumplidos) {
            return "Pendiente - Faltan requisitos";
        }
        return estado.getDescripcion();
    }
}