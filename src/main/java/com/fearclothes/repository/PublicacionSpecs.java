package com.fearclothes.repository;

import com.fearclothes.model.Categoria;
import com.fearclothes.model.EstadoPublicacion;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Filtros dinámicos del catálogo (texto, marca, talla, categoría y tipo de venta). */
public final class PublicacionSpecs {

    private PublicacionSpecs() {
    }

    public static Specification<Publicacion> activasConFiltros(
            String texto, Long marcaId, Long tallaId, Categoria categoria, TipoPublicacion tipo) {

        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("estado"), EstadoPublicacion.ACTIVA));

            if (texto != null && !texto.isBlank()) {
                String patron = "%" + texto.trim().toLowerCase() + "%";
                filtros.add(cb.or(
                        cb.like(cb.lower(root.get("prenda").<String>get("nombre")), patron),
                        cb.like(cb.lower(root.get("prenda").get("marca").<String>get("nombre")), patron)));
            }
            if (marcaId != null) {
                filtros.add(cb.equal(root.get("prenda").get("marca").get("id"), marcaId));
            }
            if (tallaId != null) {
                filtros.add(cb.equal(root.get("prenda").get("talla").get("id"), tallaId));
            }
            if (categoria != null) {
                filtros.add(cb.equal(root.get("prenda").get("categoria"), categoria));
            }
            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipo"), tipo));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }
}
