package com.colegiocesarvallejo.matriculas_platform.entity;

public enum EstadoEstudiante {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    SUSPENDIDO("Suspendido"),
    GRADUADO("Graduado"),
    RETIRADO("Retirado");

    private final String descripcion;

    EstadoEstudiante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
