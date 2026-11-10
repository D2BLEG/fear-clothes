package com.fearclothes.service;

import com.fearclothes.dto.PublicacionForm;
import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.factory.PrendaFactory;
import com.fearclothes.model.*;
import com.fearclothes.repository.*;
import com.fearclothes.storage.AlmacenArchivos;
import com.fearclothes.venta.TipoVenta;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Lógica de negocio de publicaciones, ofertas, compras y cierre de subastas. */
@Service
public class PublicacionService {

    private final PublicacionRepository publicaciones;
    private final PujaRepository pujas;
    private final OrdenRepository ordenes;
    private final MarcaRepository marcas;
    private final TallaRepository tallas;
    private final AlmacenArchivos almacen;

    public PublicacionService(PublicacionRepository publicaciones, PujaRepository pujas,
                              OrdenRepository ordenes, MarcaRepository marcas, TallaRepository tallas,
                              AlmacenArchivos almacen) {
        this.publicaciones = publicaciones;
        this.pujas = pujas;
        this.ordenes = ordenes;
        this.marcas = marcas;
        this.tallas = tallas;
        this.almacen = almacen;
    }

    // ------------------------------------------------------------ consultas

    @Transactional(readOnly = true)
    public List<Publicacion> buscar(String texto, Long marcaId, Long tallaId,
                                    Categoria categoria, TipoPublicacion tipo) {
        return publicaciones.findAll(
                PublicacionSpecs.activasConFiltros(texto, marcaId, tallaId, categoria, tipo),
                Sort.by(Sort.Direction.DESC, "fechaCreacion"));
    }

    @Transactional(readOnly = true)
    public Publicacion obtener(Long id) {
        return publicaciones.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("La publicación no existe"));
    }

    @Transactional(readOnly = true)
    public List<Publicacion> delVendedor(Usuario vendedor) {
        return publicaciones.findByVendedorOrderByFechaCreacionDesc(vendedor);
    }

    @Transactional(readOnly = true)
    public List<Orden> comprasDe(Usuario comprador) {
        return ordenes.findByCompradorOrderByFechaDesc(comprador);
    }

    @Transactional(readOnly = true)
    public List<Orden> ventasDe(Usuario vendedor) {
        return ordenes.findByPublicacion_VendedorOrderByFechaDesc(vendedor);
    }

    // ------------------------------------------------------------ comandos

    @Transactional
    public Publicacion publicar(PublicacionForm form, Usuario vendedor) {
        if (vendedor == null || !vendedor.puedePublicar()) {
            throw new ReglaNegocioException("Tu cuenta no puede publicar prendas");
        }
        Marca marca = marcas.findById(form.getMarcaId())
                .orElseThrow(() -> new ReglaNegocioException("Marca inválida"));
        Talla talla = tallas.findById(form.getTallaId())
                .orElseThrow(() -> new ReglaNegocioException("Talla inválida"));

        // Si el vendedor subió una imagen, se guarda y su URL reemplaza a la URL escrita a mano
        if (form.getImagen() != null && !form.getImagen().isEmpty()) {
            form.setImagenUrl(almacen.guardar(form.getImagen()));
        }

        Prenda prenda = PrendaFactory.crear(form, marca, talla);

        LocalDateTime cierre = null;
        if (form.getTipo() == TipoPublicacion.SUBASTA) {
            int dias = form.getDuracionDias() == null ? 3 : form.getDuracionDias();
            cierre = LocalDateTime.now().plusDays(dias);
        }
        return publicaciones.save(new Publicacion(prenda, vendedor, form.getTipo(), form.getPrecio(), cierre));
    }

    @Transactional
    public Puja pujar(Long publicacionId, Usuario postor, BigDecimal monto) {
        Publicacion publicacion = obtener(publicacionId);
        TipoVenta tipoVenta = publicacion.getTipoVenta();          // polimorfismo
        tipoVenta.validarPuja(publicacion, postor, monto);
        Puja puja = new Puja(publicacion, postor, monto);
        publicacion.agregarPuja(puja);
        return pujas.save(puja);
    }

    @Transactional
    public Orden comprarAhora(Long publicacionId, Usuario comprador) {
        Publicacion publicacion = obtener(publicacionId);
        publicacion.getTipoVenta().validarCompra(publicacion, comprador);
        return generarOrden(publicacion, comprador, publicacion.getPrecioActual());
    }

    @Transactional
    public Orden aceptarPuja(Long publicacionId, Long pujaId, Usuario vendedor) {
        Publicacion publicacion = obtener(publicacionId);
        if (!publicacion.getVendedor().getId().equals(vendedor.getId())) {
            throw new ReglaNegocioException("Solo el vendedor puede aceptar ofertas");
        }
        if (publicacion.getTipo() != TipoPublicacion.OFERTA) {
            throw new ReglaNegocioException("Solo se aceptan ofertas en publicaciones tipo oferta");
        }
        if (!publicacion.estaActiva()) {
            throw new ReglaNegocioException("La publicación ya no está activa");
        }
        Puja puja = publicacion.getPujas().stream()
                .filter(p -> p.getId().equals(pujaId))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("La oferta no existe"));
        return generarOrden(publicacion, puja.getPostor(), puja.getMonto());
    }

    /** Edita una publicación activa. Solo el dueño o un administrador; no cambia tipo de venta ni categoría. */
    @Transactional
    public Publicacion editar(Long publicacionId, PublicacionForm form, Usuario actor) {
        Publicacion publicacion = obtener(publicacionId);
        verificarPermiso(publicacion, actor);
        if (!publicacion.estaActiva()) {
            throw new ReglaNegocioException("Solo se pueden editar publicaciones activas");
        }
        Marca marca = marcas.findById(form.getMarcaId())
                .orElseThrow(() -> new ReglaNegocioException("Marca inválida"));
        Talla talla = tallas.findById(form.getTallaId())
                .orElseThrow(() -> new ReglaNegocioException("Talla inválida"));

        if (form.getImagen() != null && !form.getImagen().isEmpty()) {
            form.setImagenUrl(almacen.guardar(form.getImagen()));
        }

        // Datos comunes y propios de cada subclase (la categoría no cambia)
        PrendaFactory.actualizar(publicacion.getPrenda(), form, marca, talla);
        // El precio solo se puede cambiar mientras nadie haya ofertado
        if (publicacion.getPujas().isEmpty()) {
            publicacion.setPrecio(form.getPrecio());
        }
        return publicacion;
    }

    /** Elimina una publicación que no tenga ofertas ni orden. Si ya tiene actividad, se debe cerrar. */
    @Transactional
    public void eliminar(Long publicacionId, Usuario actor) {
        Publicacion publicacion = obtener(publicacionId);
        verificarPermiso(publicacion, actor);
        if (!publicacion.getPujas().isEmpty() || ordenes.existsByPublicacion_Id(publicacionId)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar: ya tiene ofertas o una compra. Usa «Cerrar publicación».");
        }
        publicaciones.delete(publicacion);   // también elimina la prenda (cascade)
    }

    /** Cierra una publicación activa a petición de su dueño o de un administrador. */
    @Transactional
    public void cerrar(Long publicacionId, Usuario actor) {
        verificarPermiso(obtener(publicacionId), actor);
        cerrar(publicacionId);
    }

    /** Solo el vendedor dueño o un administrador pueden modificar una publicación. */
    public void verificarPermiso(Publicacion publicacion, Usuario actor) {
        if (actor == null) {
            throw new ReglaNegocioException("Debes iniciar sesión");
        }
        boolean esDueno = publicacion.getVendedor().getId().equals(actor.getId());
        boolean esAdmin = "ADMIN".equals(actor.getRol());
        if (!esDueno && !esAdmin) {
            throw new ReglaNegocioException("No tienes permiso para modificar esta publicación");
        }
    }

    /** Cierra una publicación activa (moderación del administrador o del propio vendedor). */
    @Transactional
    public void cerrar(Long publicacionId) {
        Publicacion publicacion = obtener(publicacionId);
        if (publicacion.estaActiva()) {
            publicacion.setEstado(EstadoPublicacion.CERRADA);
        }
    }

    /** Cierra las subastas vencidas: el mejor postor gana; sin pujas, se cierra sin venta. */
    @Transactional
    public int cerrarSubastasVencidas() {
        List<Publicacion> vencidas = publicaciones.findByEstadoAndTipoAndFechaCierreBefore(
                EstadoPublicacion.ACTIVA, TipoPublicacion.SUBASTA, LocalDateTime.now());
        for (Publicacion publicacion : vencidas) {
            Puja ganadora = publicacion.getPujaMasAlta();
            if (ganadora != null) {
                generarOrden(publicacion, ganadora.getPostor(), ganadora.getMonto());
            } else {
                publicacion.setEstado(EstadoPublicacion.CERRADA);
            }
        }
        return vencidas.size();
    }

    private Orden generarOrden(Publicacion publicacion, Usuario comprador, BigDecimal precio) {
        publicacion.setEstado(EstadoPublicacion.VENDIDA);
        return ordenes.save(new Orden(comprador, publicacion, precio, "Pago simulado"));
    }
}
