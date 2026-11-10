package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "compradores")
public class Comprador extends Usuario {

    protected Comprador() {
    }

    public Comprador(String nombre, String email, String password) {
        super(nombre, email, password);
    }

    @Override
    public String getRol() {
        return "COMPRADOR";
    }
}
