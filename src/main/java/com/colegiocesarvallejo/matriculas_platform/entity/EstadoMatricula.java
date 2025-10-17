package com.colegiocesarvallejo.matriculas_platform.entity;

public enum EstadoMatricula {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En Proceso"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada"),
    COMPLETADA("Completada");

    private final String descripcion;

    EstadoMatricula(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
