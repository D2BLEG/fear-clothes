package com.fearclothes.venta;

import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;
import com.fearclothes.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PATRÓN STRATEGY: cada forma de vender (directa, ofertas, subasta) implementa
 * esta interfaz con sus propias reglas. Las clases que la usan (Publicacion, servicios)
 * no saben cuál es la implementación concreta: eso es POLIMORFISMO.
 */
public interface TipoVenta {

    TipoPublicacion tipo();

    /** Texto que se muestra junto al precio ("Precio", "Puja actual"...). */
    String etiquetaPrecio();

    /** Precio vigente de la publicación según las reglas de este tipo de venta. */
    BigDecimal precioActual(Publicacion publicacion);

    /** Indica si se puede comprar al instante al precio publicado. */
    boolean permiteCompraInmediata();

    /** Valida (o lanza ReglaNegocioException) una oferta de {@code postor} por {@code monto}. */
    void validarPuja(Publicacion publicacion, Usuario postor, BigDecimal monto);

    /** Valida (o lanza ReglaNegocioException) una compra inmediata. */
    void validarCompra(Publicacion publicacion, Usuario comprador);

    /** True si la publicación debe cerrarse automáticamente en {@code ahora}. */
    boolean debeCerrarse(Publicacion publicacion, LocalDateTime ahora);
}
