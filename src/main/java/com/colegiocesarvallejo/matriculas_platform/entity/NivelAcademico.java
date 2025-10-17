package com.colegiocesarvallejo.matriculas_platform.entity;

public enum NivelAcademico {
    INICIAL("Inicial"),
    PRIMARIA("Primaria"),
    SECUNDARIA("Secundaria");

    private final String descripcion;

    NivelAcademico(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}