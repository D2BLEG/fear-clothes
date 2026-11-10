package com.fearclothes.model;

import com.fearclothes.venta.TipoVenta;
import com.fearclothes.venta.TipoVentaFactory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Una prenda puesta a la venta. El comportamiento que depende del tipo de venta
 * (precio actual, reglas de oferta, cierre) se delega a una estrategia {@link TipoVenta}.
 */
@Entity
@Table(name = "publicaciones")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Prenda prenda;

    @ManyToOne(optional = false)
    private Usuario vendedor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPublicacion tipo;

    /** Precio fijo (DIRECTA), precio de venta/"ask" (OFERTA) o precio inicial (SUBASTA). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    /** Solo para subastas. */
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPublicacion estado = EstadoPublicacion.ACTIVA;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL)
    @OrderBy("monto DESC")
    private List<Puja> pujas = new ArrayList<>();

    protected Publicacion() {
    }

    public Publicacion(Prenda prenda, Usuario vendedor, TipoPublicacion tipo,
                       BigDecimal precio, LocalDateTime fechaCierre) {
        this.prenda = prenda;
        this.vendedor = vendedor;
        this.tipo = tipo;
        this.precio = precio;
        this.fechaCierre = fechaCierre;
    }

    // ---- Comportamiento delegado a la estrategia (STRATEGY) ----

    public TipoVenta getTipoVenta() {
        return TipoVentaFactory.obtener(tipo);
    }

    public BigDecimal getPrecioActual() {
        return getTipoVenta().precioActual(this);
    }

    public String getEtiquetaPrecio() {
        return getTipoVenta().etiquetaPrecio();
    }

    // ---- Utilidades de dominio ----

    public Puja getPujaMasAlta() {
        return pujas.stream().max(Comparator.comparing(Puja::getMonto)).orElse(null);
    }

    public void agregarPuja(Puja puja) {
        pujas.add(puja);
    }

    public boolean estaActiva() {
        return estado == EstadoPublicacion.ACTIVA;
    }

    public Long getId() {
        return id;
    }

    public Prenda getPrenda() {
        return prenda;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public TipoPublicacion getTipo() {
        return tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public List<Puja> getPujas() {
        return pujas;
    }
}
