package com.fearclothes.factory;

import com.fearclothes.dto.PublicacionForm;
import com.fearclothes.model.*;

/** PATRÓN FACTORY: crea la subclase de Prenda correcta según la categoría elegida. */
public final class PrendaFactory {

    private PrendaFactory() {
    }

    public static Prenda crear(PublicacionForm form, Marca marca, Talla talla) {
        Prenda prenda = switch (form.getCategoria()) {
            case CALZADO -> new Calzado(form.getSilueta());
            case CAMISETA -> new Camiseta(form.getTipoCuello());
            case CHAMARRA -> new Chamarra(form.isImpermeable());
        };
        prenda.setNombre(form.getNombre().trim());
        prenda.setDescripcion(form.getDescripcion());
        prenda.setColor(form.getColor());
        prenda.setImagenUrl(form.getImagenUrl());
        prenda.setCondicion(form.getCondicion());
        prenda.setMarca(marca);
        prenda.setTalla(talla);
        return prenda;
    }

    /** Actualiza una prenda existente con los datos del formulario (la categoría no cambia). */
    public static void actualizar(Prenda prenda, PublicacionForm form, Marca marca, Talla talla) {
        prenda.setNombre(form.getNombre().trim());
        prenda.setDescripcion(form.getDescripcion());
        prenda.setColor(form.getColor());
        if (form.getImagenUrl() != null && !form.getImagenUrl().isBlank()) {
            prenda.setImagenUrl(form.getImagenUrl().trim());
        }
        prenda.setCondicion(form.getCondicion());
        prenda.setMarca(marca);
        prenda.setTalla(talla);

        if (prenda instanceof Calzado calzado) {
            calzado.setSilueta(form.getSilueta());
        } else if (prenda instanceof Camiseta camiseta) {
            camiseta.setTipoCuello(form.getTipoCuello());
        } else if (prenda instanceof Chamarra chamarra) {
            chamarra.setImpermeable(form.isImpermeable());
        }
    }
}
