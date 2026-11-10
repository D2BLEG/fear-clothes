package com.fearclothes.service;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Marca;
import com.fearclothes.model.Talla;
import com.fearclothes.repository.MarcaRepository;
import com.fearclothes.repository.PrendaRepository;
import com.fearclothes.repository.TallaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Administración de marcas y tallas (CRUD completo). */
@Service
public class CatalogoService {

    private final MarcaRepository marcas;
    private final TallaRepository tallas;
    private final PrendaRepository prendas;

    public CatalogoService(MarcaRepository marcas, TallaRepository tallas, PrendaRepository prendas) {
        this.marcas = marcas;
        this.tallas = tallas;
        this.prendas = prendas;
    }

    // ------------------------------------------------------------ marcas

    @Transactional(readOnly = true)
    public List<Marca> listarMarcas() {
        return marcas.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Marca obtenerMarca(Long id) {
        return marcas.findById(id).orElseThrow(() -> new ReglaNegocioException("La marca no existe"));
    }

    @Transactional
    public void crearMarca(String nombre, String pais) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("Escribe el nombre de la marca");
        }
        if (marcas.existsByNombreIgnoreCase(nombre.trim())) {
            throw new ReglaNegocioException("Esa marca ya existe");
        }
        marcas.save(new Marca(nombre.trim(), pais == null ? null : pais.trim()));
    }

    @Transactional
    public void actualizarMarca(Long id, String nombre, String pais) {
        Marca marca = obtenerMarca(id);
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("Escribe el nombre de la marca");
        }
        if (marcas.existsByNombreIgnoreCaseAndIdNot(nombre.trim(), id)) {
            throw new ReglaNegocioException("Ya existe otra marca con ese nombre");
        }
        marca.setNombre(nombre.trim());
        marca.setPais(pais == null || pais.isBlank() ? null : pais.trim());
    }

    @Transactional
    public void eliminarMarca(Long id) {
        if (prendas.existsByMarcaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar: hay prendas de esa marca");
        }
        marcas.deleteById(id);
    }

    // ------------------------------------------------------------ tallas

    @Transactional(readOnly = true)
    public List<Talla> listarTallas() {
        return tallas.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Talla obtenerTalla(Long id) {
        return tallas.findById(id).orElseThrow(() -> new ReglaNegocioException("La talla no existe"));
    }

    @Transactional
    public void crearTalla(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new ReglaNegocioException("Escribe la talla");
        }
        if (tallas.existsByEtiquetaIgnoreCase(etiqueta.trim())) {
            throw new ReglaNegocioException("Esa talla ya existe");
        }
        tallas.save(new Talla(etiqueta.trim()));
    }

    @Transactional
    public void actualizarTalla(Long id, String etiqueta) {
        Talla talla = obtenerTalla(id);
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new ReglaNegocioException("Escribe la talla");
        }
        if (tallas.existsByEtiquetaIgnoreCaseAndIdNot(etiqueta.trim(), id)) {
            throw new ReglaNegocioException("Ya existe otra talla con ese nombre");
        }
        talla.setEtiqueta(etiqueta.trim());
    }

    @Transactional
    public void eliminarTalla(Long id) {
        if (prendas.existsByTallaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar: hay prendas con esa talla");
        }
        tallas.deleteById(id);
    }
}
