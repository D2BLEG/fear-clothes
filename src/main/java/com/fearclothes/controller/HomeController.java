package com.fearclothes.controller;

import com.fearclothes.model.Categoria;
import com.fearclothes.model.TipoPublicacion;
import com.fearclothes.service.CatalogoService;
import com.fearclothes.service.PublicacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final PublicacionService publicaciones;
    private final CatalogoService catalogo;

    public HomeController(PublicacionService publicaciones, CatalogoService catalogo) {
        this.publicaciones = publicaciones;
        this.catalogo = catalogo;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Long marca,
                         @RequestParam(required = false) Long talla,
                         @RequestParam(required = false) Categoria categoria,
                         @RequestParam(required = false) TipoPublicacion tipo,
                         Model model) {
        model.addAttribute("publicaciones", publicaciones.buscar(q, marca, talla, categoria, tipo));
        model.addAttribute("marcas", catalogo.listarMarcas());
        model.addAttribute("tallas", catalogo.listarTallas());
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("tipos", TipoPublicacion.values());
        model.addAttribute("q", q);
        model.addAttribute("marcaSel", marca);
        model.addAttribute("tallaSel", talla);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("tipoSel", tipo);
        return "index";
    }
}
