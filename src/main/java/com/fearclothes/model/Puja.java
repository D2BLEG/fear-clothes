package com.fearclothes.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Oferta (bid) hecha por un usuario sobre una publicación de tipo OFERTA o SUBASTA. */
@Entity
@Table(name = "pujas")
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Publicacion publicacion;

    @ManyToOne(optional = false)
    private Usuario postor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    protected Puja() {
    }

    public Puja(Publicacion publicacion, Usuario postor, BigDecimal monto) {
        this.publicacion = publicacion;
        this.postor = postor;
        this.monto = monto;
    }

    public Long getId() {
        return id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public Usuario getPostor() {
        return postor;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
