package com.fearclothes.model;

public enum TipoPublicacion {
    DIRECTA("Venta directa"),
    OFERTA("Acepta ofertas"),
    SUBASTA("Subasta");

    private final String etiqueta;

    TipoPublicacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
