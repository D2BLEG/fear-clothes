package com.fearclothes.controller;

import com.fearclothes.dto.PerfilForm;
import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Usuario;
import com.fearclothes.service.PublicacionService;
import com.fearclothes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Páginas del usuario con sesión: sus compras, sus ventas y su perfil. */
@Controller
public class CuentaController {

    private final PublicacionService publicaciones;
    private final UsuarioService usuarios;

    public CuentaController(PublicacionService publicaciones, UsuarioService usuarios) {
        this.publicaciones = publicaciones;
        this.usuarios = usuarios;
    }

    @GetMapping("/mis-compras")
    public String misCompras(Authentication auth, Model model) {
        Usuario usuario = usuarios.actual(auth);
        model.addAttribute("ordenes", publicaciones.comprasDe(usuario));
        return "mis-compras";
    }

    @GetMapping("/mis-ventas")
    public String misVentas(Authentication auth, Model model) {
        Usuario usuario = usuarios.actual(auth);
        model.addAttribute("publicaciones", publicaciones.delVendedor(usuario));
        model.addAttribute("ordenes", publicaciones.ventasDe(usuario));
        return "mis-ventas";
    }

    // ------------------------------------------------------------ perfil (Update de usuario)

    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        Usuario usuario = usuarios.actual(auth);
        PerfilForm form = new PerfilForm();
        form.setNombre(usuario.getNombre());
        model.addAttribute("form", form);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String guardarPerfil(@Valid @ModelAttribute("form") PerfilForm form, BindingResult resultado,
                                Authentication auth, Model model, RedirectAttributes flash) {
        Usuario usuario = usuarios.actual(auth);
        if (resultado.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "perfil";
        }
        try {
            usuarios.actualizarPerfil(usuario.getId(), form);
            flash.addFlashAttribute("mensaje", "Tu perfil se actualizó");
            return "redirect:/perfil";
        } catch (ReglaNegocioException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());
            return "perfil";
        }
    }
}
