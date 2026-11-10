package com.fearclothes.model;

public enum Categoria {
    CALZADO("Calzado"),
    CAMISETA("Camisetas"),
    CHAMARRA("Chamarras");

    private final String etiqueta;

    Categoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
