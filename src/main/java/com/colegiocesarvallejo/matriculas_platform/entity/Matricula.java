package com.colegiocesarvallejo.matriculas_platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matriculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_matricula", unique = true, nullable = false, length = 20)
    private String numeroMatricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apoderado_id", nullable = false)
    private Usuario apoderado;

    @Column(name = "anio_academico", nullable = false, length = 10)
    private String anioAcademico;

    @Column(nullable = false, length = 20)
    private String grado;

    @Column(length = 10)
    private String seccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_academico", nullable = false)
    private NivelAcademico nivelAcademico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado = EstadoMatricula.PENDIENTE;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_rechazo")
    private LocalDateTime fechaRechazo;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "monto_matricula", precision = 10, scale = 2)
    private BigDecimal montoMatricula;

    @Column(name = "monto_mensualidad", precision = 10, scale = 2)
    private BigDecimal montoMensualidad;

    @Column(name = "monto_materiales", precision = 10, scale = 2)
    private BigDecimal montoMateriales;

    @Column(name = "monto_total", precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(name = "monto_final", precision = 10, scale = 2)
    private BigDecimal montoFinal;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "comentarios_apoderado", columnDefinition = "TEXT")
    private String comentariosApoderado;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesado_por")
    private Usuario procesadoPor;

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @Column(name = "requiere_documentos", nullable = false)
    private Boolean requiereDocumentos = true;

    @Column(name = "documentos_completos", nullable = false)
    private Boolean documentosCompletos = false;

    @Column(name = "requiere_entrevista", nullable = false)
    private Boolean requiereEntrevista = false;

    @Column(name = "entrevista_completada", nullable = false)
    private Boolean entrevistaCompletada = false;

    @Column(name = "fecha_entrevista")
    private LocalDateTime fechaEntrevista;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatriculaCurso> cursos;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentoMatricula> documentos;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "pagos_generados")
    private Boolean pagosGenerados = false;

    public Boolean getPagosGenerados() {
        return pagosGenerados != null ? pagosGenerados : false;
    }

    public void setPagosGenerados(Boolean pagosGenerados) {
        this.pagosGenerados = pagosGenerados;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaSolicitud = LocalDateTime.now();
        if (numeroMatricula == null) {
            numeroMatricula = generarNumeroMatricula();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    private String generarNumeroMatricula() {
        int anio = LocalDateTime.now().getYear();
        long timestamp = System.currentTimeMillis() % 1000000;
        return String.format("MAT-%d-%06d", anio, timestamp);
    }

    public boolean isPendiente() {
        return estado == EstadoMatricula.PENDIENTE;
    }

    public boolean isAprobada() {
        return estado == EstadoMatricula.APROBADA || estado == EstadoMatricula.COMPLETADA;
    }

    public boolean isRechazada() {
        return estado == EstadoMatricula.RECHAZADA;
    }

    public boolean puedeSerAprobada() {
        return estado == EstadoMatricula.PENDIENTE || estado == EstadoMatricula.EN_PROCESO;
    }

    public boolean requiereAccionApoderado() {
        return estado == EstadoMatricula.PENDIENTE &&
                (!documentosCompletos || (requiereEntrevista && !entrevistaCompletada));
    }

    public double getPorcentajeCompletitud() {
        int total = 4;
        int completados = 0;

        if (documentosCompletos) completados++;
        if (!requiereEntrevista || entrevistaCompletada) completados++;
        if (cursos != null && !cursos.isEmpty()) completados++;
        if (montoFinal != null && montoFinal.compareTo(BigDecimal.ZERO) > 0) completados++;

        return (completados * 100.0) / total;
    }
}