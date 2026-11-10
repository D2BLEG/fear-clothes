package com.fearclothes.model;

import jakarta.persistence.*;

/** Talla de ropa (S, M, L...) o de calzado (7 MX, 8 MX...). */
@Entity
@Table(name = "tallas")
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String etiqueta;

    protected Talla() {
    }

    public Talla(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Long getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
