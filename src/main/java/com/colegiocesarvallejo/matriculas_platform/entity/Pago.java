package com.colegiocesarvallejo.matriculas_platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_comprobante", unique = true, length = 30)
    private String numeroComprobante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apoderado_id", nullable = false)
    private Usuario apoderado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "concepto", nullable = false, length = 200)
    private String concepto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "monto_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "recargo", precision = 10, scale = 2)
    private BigDecimal recargo = BigDecimal.ZERO;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_pagado", precision = 10, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Column(name = "monto_pendiente", precision = 10, scale = 2)
    private BigDecimal montoPendiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_vencido")
    private LocalDate fechaVencido;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion;

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "numero_tarjeta_ultimos4", length = 4)
    private String numeroTarjetaUltimos4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesado_por")
    private Usuario procesadoPor;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "es_pago_fraccionado", nullable = false)
    private Boolean esPagoFraccionado = false;

    @Column(name = "numero_cuota")
    private Integer numeroCuota;

    @Column(name = "total_cuotas")
    private Integer totalCuotas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_principal_id")
    private Pago pagoPrincipal;

    @Column(name = "genera_boleta", nullable = false)
    private Boolean generaBoleta = true;

    @Column(name = "numero_boleta", length = 20)
    private String numeroBoleta;

    @Column(name = "ruc_factura", length = 11)
    private String rucFactura;

    @Column(name = "razon_social_factura", length = 200)
    private String razonSocialFactura;

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

        if (numeroComprobante == null) {
            numeroComprobante = generarNumeroComprobanteTemp();
        }


        calcularMontos();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        actualizarEstadoInteligente();
    }

    private String generarNumeroComprobanteTemp() {
        int año = LocalDateTime.now().getYear();
        long timestamp = System.currentTimeMillis() % 1000000;
        return String.format("TEMP-%d-%06d", año, timestamp);
    }

    private void calcularMontos() {

        if (descuento == null) descuento = BigDecimal.ZERO;
        if (recargo == null) recargo = BigDecimal.ZERO;
        if (montoPagado == null) montoPagado = BigDecimal.ZERO;

        montoTotal = montoOriginal
                .subtract(descuento)
                .add(recargo)
                .setScale(2, RoundingMode.HALF_UP);

        montoPendiente = montoTotal
                .subtract(montoPagado)
                .setScale(2, RoundingMode.HALF_UP);

        if (montoPendiente.abs().compareTo(new BigDecimal("0.01")) < 0) {
            montoPendiente = BigDecimal.ZERO;
        }

        actualizarEstadoInteligente();
    }

    private void actualizarEstadoInteligente() {
        if (montoPendiente == null || montoTotal == null) return;
        if (montoPendiente.abs().compareTo(new BigDecimal("0.01")) < 0) {
            montoPendiente = BigDecimal.ZERO;
        }

        if (montoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            estado = EstadoPago.PAGADO;
            if (fechaPago == null) {
                fechaPago = LocalDateTime.now();
            }
        } else if (montoPagado != null && montoPagado.compareTo(BigDecimal.ZERO) > 0
                && montoPendiente.compareTo(montoTotal) < 0) {
            estado = EstadoPago.PARCIAL;
        } else if (estado != EstadoPago.PAGADO && LocalDate.now().isAfter(fechaVencimiento)) {
            estado = EstadoPago.VENCIDO;
            if (fechaVencido == null) {
                fechaVencido = LocalDate.now();
            }
        } else if (estado == EstadoPago.VENCIDO && montoPendiente.compareTo(montoTotal) == 0) {
            estado = EstadoPago.VENCIDO;
        }
    }

    public boolean isPagado() {
        return estado == EstadoPago.PAGADO;
    }

    public boolean isPendiente() {
        return estado == EstadoPago.PENDIENTE;
    }

    public boolean isVencido() {
        return estado == EstadoPago.VENCIDO ||
                (estado != EstadoPago.PAGADO && LocalDate.now().isAfter(fechaVencimiento));
    }

    public boolean isParcial() {
        return estado == EstadoPago.PARCIAL;
    }

    public long getDiasVencimiento() {
        if (fechaVencimiento == null) return 0;
        return LocalDate.now().toEpochDay() - fechaVencimiento.toEpochDay();
    }

    public BigDecimal getPorcentajePagado() {
        if (montoTotal == null || montoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (montoPagado == null) {
            return BigDecimal.ZERO;
        }
        return montoPagado
                .multiply(BigDecimal.valueOf(100))
                .divide(montoTotal, 2, RoundingMode.HALF_UP);
    }
    public void recalcularMontos() {
        calcularMontos();
    }
}