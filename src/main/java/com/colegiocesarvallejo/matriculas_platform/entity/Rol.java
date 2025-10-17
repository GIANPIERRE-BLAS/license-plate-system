package com.colegiocesarvallejo.matriculas_platform.entity;

public enum Rol {
    PADRE("Padre de Familia"),
    ADMIN("Administrador"),
    PROFESOR("Profesor");

    private final String displayName;

    Rol(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
