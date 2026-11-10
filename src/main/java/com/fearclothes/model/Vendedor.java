package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendedores")
public class Vendedor extends Usuario {

    private String nombreTienda;

    protected Vendedor() {
    }

    public Vendedor(String nombre, String email, String password) {
        super(nombre, email, password);
        this.nombreTienda = nombre;
    }

    @Override
    public String getRol() {
        return "VENDEDOR";
    }

    @Override
    public boolean puedePublicar() {
        return true;
    }

    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }
}
