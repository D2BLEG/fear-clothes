package com.fearclothes.model;

import jakarta.persistence.*;

/**
 * Clase abstracta que representa cualquier prenda del catálogo (HERENCIA).
 * Calzado, Camiseta y Chamarra la extienden y aportan sus propios atributos.
 * Mapeo JOINED: "prendas" guarda lo común y cada subclase su tabla (calzados, camisetas, chamarras)
 * con sus atributos específicos. La columna "categoria" indica el tipo de prenda.
 */
@Entity
@Table(name = "prendas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Prenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    private String color;

    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condicion condicion;

    @ManyToOne(optional = false)
    private Marca marca;

    @ManyToOne(optional = false)
    private Talla talla;

    protected Prenda() {
    }

    protected Prenda(Categoria categoria) {
        this.categoria = categoria;
    }

    /** Dato propio de cada tipo de prenda (silueta, cuello, impermeabilidad). Lo implementa cada subclase. */
    public abstract String getDetalleEspecifico();

    public String getResumen() {
        return marca.getNombre() + " " + nombre + " · Talla " + talla.getEtiqueta();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Condicion getCondicion() {
        return condicion;
    }

    public void setCondicion(Condicion condicion) {
        this.condicion = condicion;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }
}
