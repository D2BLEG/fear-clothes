package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "calzados")
public class Calzado extends Prenda {

    private String silueta;

    protected Calzado() {
    }

    public Calzado(String silueta) {
        super(Categoria.CALZADO);
        this.silueta = silueta;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Silueta: " + (silueta == null || silueta.isBlank() ? "no especificada" : silueta);
    }

    public String getSilueta() {
        return silueta;
    }

    public void setSilueta(String silueta) {
        this.silueta = silueta;
    }
}
