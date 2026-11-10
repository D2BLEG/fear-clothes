package com.fearclothes.venta;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;

import java.math.BigDecimal;

/** Precio fijo: se compra al instante, no admite ofertas. */
public class VentaDirecta extends TipoVentaBase {

    @Override
    public TipoPublicacion tipo() {
        return TipoPublicacion.DIRECTA;
    }

    @Override
    public String etiquetaPrecio() {
        return "Precio";
    }

    @Override
    public BigDecimal precioActual(Publicacion publicacion) {
        return publicacion.getPrecio();
    }

    @Override
    public boolean permiteCompraInmediata() {
        return true;
    }

    @Override
    protected void validarMonto(Publicacion publicacion, BigDecimal monto) {
        throw new ReglaNegocioException("Esta publicación es de precio fijo y no acepta ofertas");
    }
}
