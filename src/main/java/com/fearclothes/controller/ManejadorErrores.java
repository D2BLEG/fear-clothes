package com.fearclothes.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Manejo global de errores que ocurren antes de llegar a un controlador. */
@ControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String imagenMuyGrande(RedirectAttributes flash) {
        flash.addFlashAttribute("error", "La imagen pesa más de 5 MB. Elige una más ligera.");
        return "redirect:/vender";
    }
}
