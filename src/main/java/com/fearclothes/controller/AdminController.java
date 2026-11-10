package com.fearclothes.controller;

import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.service.CatalogoService;
import com.fearclothes.service.PublicacionService;
import com.fearclothes.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Panel de administración: marcas, tallas, usuarios y moderación de publicaciones. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CatalogoService catalogo;
    private final PublicacionService publicaciones;
    private final UsuarioService usuarios;

    public AdminController(CatalogoService catalogo, PublicacionService publicaciones,
                           UsuarioService usuarios) {
        this.catalogo = catalogo;
        this.publicaciones = publicaciones;
        this.usuarios = usuarios;
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("marcas", catalogo.listarMarcas());
        model.addAttribute("tallas", catalogo.listarTallas());
        model.addAttribute("publicaciones", publicaciones.buscar(null, null, null, null, null));
        return "admin";
    }

    // ------------------------------------------------------------ marcas

    @PostMapping("/marcas")
    public String crearMarca(@RequestParam String nombre, @RequestParam(required = false) String pais,
                             RedirectAttributes flash) {
        return ejecutar(() -> catalogo.crearMarca(nombre, pais), "Marca agregada", flash);
    }

    @GetMapping("/marcas/{id}/editar")
    public String editarMarca(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("marca", catalogo.obtenerMarca(id));
            return "admin-marca-form";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/marcas/{id}/editar")
    public String actualizarMarca(@PathVariable Long id, @RequestParam String nombre,
                                  @RequestParam(required = false) String pais, RedirectAttributes flash) {
        try {
            catalogo.actualizarMarca(id, nombre, pais);
            flash.addFlashAttribute("mensaje", "Marca actualizada");
            return "redirect:/admin";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/marcas/" + id + "/editar";
        }
    }

    @PostMapping("/marcas/{id}/eliminar")
    public String eliminarMarca(@PathVariable Long id, RedirectAttributes flash) {
        return ejecutar(() -> catalogo.eliminarMarca(id), "Marca eliminada", flash);
    }

    // ------------------------------------------------------------ tallas

    @PostMapping("/tallas")
    public String crearTalla(@RequestParam String etiqueta, RedirectAttributes flash) {
        return ejecutar(() -> catalogo.crearTalla(etiqueta), "Talla agregada", flash);
    }

    @GetMapping("/tallas/{id}/editar")
    public String editarTalla(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("talla", catalogo.obtenerTalla(id));
            return "admin-talla-form";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/tallas/{id}/editar")
    public String actualizarTalla(@PathVariable Long id, @RequestParam String etiqueta,
                                  RedirectAttributes flash) {
        try {
            catalogo.actualizarTalla(id, etiqueta);
            flash.addFlashAttribute("mensaje", "Talla actualizada");
            return "redirect:/admin";
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tallas/" + id + "/editar";
        }
    }

    @PostMapping("/tallas/{id}/eliminar")
    public String eliminarTalla(@PathVariable Long id, RedirectAttributes flash) {
        return ejecutar(() -> catalogo.eliminarTalla(id), "Talla eliminada", flash);
    }

    // ------------------------------------------------------------ publicaciones

    @PostMapping("/publicaciones/{id}/cerrar")
    public String cerrarPublicacion(@PathVariable Long id, RedirectAttributes flash) {
        return ejecutar(() -> publicaciones.cerrar(id), "Publicación cerrada", flash);
    }

    // ------------------------------------------------------------ usuarios

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarios.listar());
        return "admin-usuarios";
    }

    @PostMapping("/usuarios/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            usuarios.cambiarEstado(id, usuarios.actual(auth));
            flash.addFlashAttribute("mensaje", "Estado de la cuenta actualizado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    private String ejecutar(Runnable accion, String exito, RedirectAttributes flash) {
        try {
            accion.run();
            flash.addFlashAttribute("mensaje", exito);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }
}
