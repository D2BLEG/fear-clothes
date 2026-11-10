package com.fearclothes.venta;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Puja;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Subasta con fecha de cierre: gana la puja más alta. Cada puja debe superar
 * a la anterior por al menos {@link #INCREMENTO_MINIMO}.
 */
public class Subasta extends TipoVentaBase {

    public static final BigDecimal INCREMENTO_MINIMO = new BigDecimal("50");

    @Override
    public TipoPublicacion tipo() {
        return TipoPublicacion.SUBASTA;
    }

    @Override
    public String etiquetaPrecio() {
        return "Puja actual";
    }

    @Override
    public BigDecimal precioActual(Publicacion publicacion) {
        Puja mejor = publicacion.getPujaMasAlta();
        return mejor == null ? publicacion.getPrecio() : mejor.getMonto();
    }

    @Override
    public boolean permiteCompraInmediata() {
        return false;
    }

    @Override
    public boolean debeCerrarse(Publicacion publicacion, LocalDateTime ahora) {
        return publicacion.getFechaCierre() != null && !ahora.isBefore(publicacion.getFechaCierre());
    }

    @Override
    protected void validarMonto(Publicacion publicacion, BigDecimal monto) {
        if (debeCerrarse(publicacion, LocalDateTime.now())) {
            throw new ReglaNegocioException("La subasta ya terminó");
        }
        Puja mejor = publicacion.getPujaMasAlta();
        BigDecimal minimo = mejor == null
                ? publicacion.getPrecio()
                : mejor.getMonto().add(INCREMENTO_MINIMO);
        if (monto.compareTo(minimo) < 0) {
            throw new ReglaNegocioException("La puja mínima es $" + minimo.stripTrailingZeros().toPlainString());
        }
    }
}
