package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "camisetas")
public class Camiseta extends Prenda {

    private String tipoCuello;

    protected Camiseta() {
    }

    public Camiseta(String tipoCuello) {
        super(Categoria.CAMISETA);
        this.tipoCuello = tipoCuello;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Cuello: " + (tipoCuello == null || tipoCuello.isBlank() ? "no especificado" : tipoCuello);
    }

    public String getTipoCuello() {
        return tipoCuello;
    }

    public void setTipoCuello(String tipoCuello) {
        this.tipoCuello = tipoCuello;
    }
}
