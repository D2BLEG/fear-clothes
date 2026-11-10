package com.fearclothes.model;

public enum Condicion {
    NUEVA("Nueva con etiquetas"),
    COMO_NUEVA("Como nueva"),
    USADA("Usada");

    private final String etiqueta;

    Condicion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
