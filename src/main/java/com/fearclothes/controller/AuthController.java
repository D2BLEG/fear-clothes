package com.fearclothes.controller;

import com.fearclothes.dto.RegistroForm;
import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarios;

    public AuthController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("form", new RegistroForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("form") RegistroForm form,
                            BindingResult resultado, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "registro";
        }
        try {
            usuarios.registrar(form);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue("email", "duplicado", e.getMessage());
            return "registro";
        }
        flash.addFlashAttribute("mensaje", "Cuenta creada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
