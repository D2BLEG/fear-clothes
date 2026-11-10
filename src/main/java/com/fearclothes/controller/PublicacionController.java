package com.fearclothes.controller;

import com.fearclothes.dto.PublicacionForm;
import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.*;
import com.fearclothes.service.CatalogoService;
import com.fearclothes.service.PublicacionService;
import com.fearclothes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class PublicacionController {

    private final PublicacionService publicaciones;
    private final CatalogoService catalogo;
    private final UsuarioService usuarios;

    public PublicacionController(PublicacionService publicaciones, CatalogoService catalogo,
                                 UsuarioService usuarios) {
        this.publicaciones = publicaciones;
        this.catalogo = catalogo;
        this.usuarios = usuarios;
    }

    // ------------------------------------------------------------ detalle

    @GetMapping("/publicacion/{id}")
    public String detalle(@PathVariable Long id, Authentication auth,
                          Model model, RedirectAttributes flash) {
        Publicacion publicacion;
        try {
            publicacion = publicaciones.obtener(id);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
        Usuario usuario = usuarios.actual(auth);
        boolean esDueno = usuario != null && usuario.getId().equals(publicacion.getVendedor().getId());
        boolean esAdmin = usuario != null && "ADMIN".equals(usuario.getRol());

        model.addAttribute("p", publicacion);
        model.addAttribute("autenticado", usuario != null);
        model.addAttribute("esDueno", esDueno);
        model.addAttribute("puedeGestionar", esDueno || esAdmin);
        return "detalle";
    }

    // ------------------------------------------------------------ vender (Create)

    @GetMapping("/vender")
    public String formularioVender(Model model) {
        model.addAttribute("form", new PublicacionForm());
        cargarListas(model);
        return "vender";
    }

    @PostMapping("/vender")
    public String publicar(@Valid @ModelAttribute("form") PublicacionForm form, BindingResult resultado,
                           Authentication auth, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            cargarListas(model);
            return "vender";
        }
        try {
            Publicacion publicacion = publicaciones.publicar(form, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "¡Tu prenda ya está publicada!");
            return "redirect:/publicacion/" + publicacion.getId();
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarListas(model);
            return "vender";
        }
    }

    // ------------------------------------------------------------ editar (Update)

    @GetMapping("/publicacion/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Authentication auth,
                                   Model model, RedirectAttributes flash) {
        try {
            Publicacion publicacion = publicaciones.obtener(id);
            publicaciones.verificarPermiso(publicacion, usuarios.actual(auth));
            if (!publicacion.estaActiva()) {
                throw new ReglaNegocioException("Solo se pueden editar publicaciones activas");
            }
            model.addAttribute("form", PublicacionForm.desde(publicacion));
            prepararEdicion(model, publicacion);
            return "editar-publicacion";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/publicacion/" + id;
        }
    }

    @PostMapping("/publicacion/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") PublicacionForm form,
                             BindingResult resultado, Authentication auth,
                             Model model, RedirectAttributes flash) {
        Publicacion publicacion;
        try {
            publicacion = publicaciones.obtener(id);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
        if (resultado.hasErrors()) {
            prepararEdicion(model, publicacion);
            return "editar-publicacion";
        }
        try {
            publicaciones.editar(id, form, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "Publicación actualizada");
            return "redirect:/publicacion/" + id;
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararEdicion(model, publicacion);
            return "editar-publicacion";
        }
    }

    // ------------------------------------------------------------ eliminar / cerrar (Delete)

    @PostMapping("/publicacion/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario actor = usuarios.actual(auth);
        try {
            publicaciones.eliminar(id, actor);
            flash.addFlashAttribute("mensaje", "Publicación eliminada");
            return actor != null && "ADMIN".equals(actor.getRol()) ? "redirect:/admin" : "redirect:/mis-ventas";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/publicacion/" + id;
        }
    }

    @PostMapping("/publicacion/{id}/cerrar")
    public String cerrar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            publicaciones.cerrar(id, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "Publicación cerrada");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/publicacion/" + id;
    }

    // ------------------------------------------------------------ ofertas y compras

    @PostMapping("/publicacion/{id}/pujar")
    public String pujar(@PathVariable Long id, @RequestParam BigDecimal monto,
                        Authentication auth, RedirectAttributes flash) {
        try {
            publicaciones.pujar(id, usuarios.actual(auth), monto);
            flash.addFlashAttribute("mensaje", "Tu oferta fue registrada");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/publicacion/" + id;
    }

    @PostMapping("/publicacion/{id}/comprar")
    public String comprar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            publicaciones.comprarAhora(id, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "¡Compra realizada! (pago simulado)");
            return "redirect:/mis-compras";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/publicacion/" + id;
        }
    }

    @PostMapping("/publicacion/{id}/aceptar/{pujaId}")
    public String aceptar(@PathVariable Long id, @PathVariable Long pujaId,
                          Authentication auth, RedirectAttributes flash) {
        try {
            publicaciones.aceptarPuja(id, pujaId, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "Oferta aceptada: venta concretada");
            return "redirect:/mis-ventas";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/publicacion/" + id;
        }
    }

    // ------------------------------------------------------------ utilidades

    private void prepararEdicion(Model model, Publicacion publicacion) {
        model.addAttribute("p", publicacion);
        model.addAttribute("tienePujas", !publicacion.getPujas().isEmpty());
        cargarListas(model);
    }

    private void cargarListas(Model model) {
        model.addAttribute("marcas", catalogo.listarMarcas());
        model.addAttribute("tallas", catalogo.listarTallas());
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("condiciones", Condicion.values());
        model.addAttribute("tipos", TipoPublicacion.values());
    }
}
