package com.fearclothes.venta;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase abstracta con las validaciones comunes a todos los tipos de venta.
 * Usa el patrón TEMPLATE METHOD: validarPuja() fija los pasos y delega el último
 * ({@link #validarMonto}) a cada subclase.
 */
public abstract class TipoVentaBase implements TipoVenta {

    @Override
    public final void validarPuja(Publicacion publicacion, Usuario postor, BigDecimal monto) {
        validarPublicacionActiva(publicacion);
        validarNoEsElVendedor(publicacion, postor, "No puedes ofertar en tu propia publicación");
        if (monto == null || monto.signum() <= 0) {
            throw new ReglaNegocioException("Escribe un monto válido");
        }
        validarMonto(publicacion, monto);
    }

    @Override
    public void validarCompra(Publicacion publicacion, Usuario comprador) {
        validarPublicacionActiva(publicacion);
        validarNoEsElVendedor(publicacion, comprador, "No puedes comprar tu propia publicación");
        if (!permiteCompraInmediata()) {
            throw new ReglaNegocioException("Esta publicación no se puede comprar al instante");
        }
    }

    @Override
    public boolean debeCerrarse(Publicacion publicacion, LocalDateTime ahora) {
        return false;
    }

    /** Regla específica del monto de la oferta. */
    protected abstract void validarMonto(Publicacion publicacion, BigDecimal monto);

    private void validarPublicacionActiva(Publicacion publicacion) {
        if (!publicacion.estaActiva()) {
            throw new ReglaNegocioException("La publicación ya no está activa");
        }
    }

    private void validarNoEsElVendedor(Publicacion publicacion, Usuario usuario, String mensaje) {
        if (publicacion.getVendedor().getId().equals(usuario.getId())) {
            throw new ReglaNegocioException(mensaje);
        }
    }
}
