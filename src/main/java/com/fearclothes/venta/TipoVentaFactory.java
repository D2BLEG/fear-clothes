package com.fearclothes.venta;

import com.fearclothes.model.TipoPublicacion;

import java.util.EnumMap;
import java.util.Map;

/** PATRÓN FACTORY: entrega la estrategia que corresponde a cada tipo de publicación. */
public final class TipoVentaFactory {

    private static final Map<TipoPublicacion, TipoVenta> ESTRATEGIAS = new EnumMap<>(TipoPublicacion.class);

    static {
        ESTRATEGIAS.put(TipoPublicacion.DIRECTA, new VentaDirecta());
        ESTRATEGIAS.put(TipoPublicacion.OFERTA, new VentaPorOferta());
        ESTRATEGIAS.put(TipoPublicacion.SUBASTA, new Subasta());
    }

    private TipoVentaFactory() {
    }

    public static TipoVenta obtener(TipoPublicacion tipo) {
        return ESTRATEGIAS.get(tipo);
    }
}
