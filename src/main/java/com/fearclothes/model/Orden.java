package com.fearclothes.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Compra concretada (pago simulado). */
@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario comprador;

    @OneToOne(optional = false)
    private Publicacion publicacion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioFinal;

    private String metodoPago;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    protected Orden() {
    }

    public Orden(Usuario comprador, Publicacion publicacion, BigDecimal precioFinal, String metodoPago) {
        this.comprador = comprador;
        this.publicacion = publicacion;
        this.precioFinal = precioFinal;
        this.metodoPago = metodoPago;
    }

    public Long getId() {
        return id;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
