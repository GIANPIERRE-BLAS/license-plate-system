package com.colegiocesarvallejo.matriculas_platform.entity;

public enum TipoPago {
    MATRICULA("Matrícula"),
    MENSUALIDAD("Mensualidad"),
    MATERIALES("Materiales"),
    UNIFORME("Uniforme"),
    ACTIVIDADES("Actividades Extracurriculares"),
    OTROS("Otros");

    private final String descripcion;

    TipoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}