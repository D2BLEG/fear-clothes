package com.fearclothes.model;

public enum EstadoPublicacion {
    ACTIVA("Activa"),
    VENDIDA("Vendida"),
    CERRADA("Cerrada");

    private final String etiqueta;

    EstadoPublicacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
