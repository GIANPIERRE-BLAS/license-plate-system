package com.colegiocesarvallejo.matriculas_platform.entity;

public enum EstadoPago {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    VENCIDO("Vencido"),
    PARCIAL("Parcial"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}