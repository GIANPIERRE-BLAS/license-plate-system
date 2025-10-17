package com.colegiocesarvallejo.matriculas_platform.entity;

public enum TipoDocumento {
    DNI("Documento Nacional de Identidad"),
    CE("Carnet de Extranjería"),
    PASAPORTE("Pasaporte");

    private final String descripcion;

    TipoDocumento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

