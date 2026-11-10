package com.fearclothes.venta;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;

import java.math.BigDecimal;

/**
 * Estilo StockX: el vendedor fija un precio de venta ("ask"). Los compradores pueden
 * pagarlo al instante o dejar ofertas ("bids") más bajas que el vendedor decide aceptar.
 */
public class VentaPorOferta extends TipoVentaBase {

    @Override
    public TipoPublicacion tipo() {
        return TipoPublicacion.OFERTA;
    }

    @Override
    public String etiquetaPrecio() {
        return "Precio de venta";
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
        if (monto.compareTo(publicacion.getPrecio()) >= 0) {
            throw new ReglaNegocioException(
                    "Tu oferta alcanza el precio de venta: usa «Comprar ahora» en su lugar");
        }
    }
}
