package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario {

    protected Administrador() {
    }

    public Administrador(String nombre, String email, String password) {
        super(nombre, email, password);
    }

    @Override
    public String getRol() {
        return "ADMIN";
    }
}
