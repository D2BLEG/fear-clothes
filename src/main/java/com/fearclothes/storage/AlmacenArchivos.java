package com.fearclothes.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstracción para guardar imágenes. Hoy se implementa en disco local ({@link AlmacenLocal});
 * al pasar a AWS bastará con crear otra implementación que use S3, sin tocar el resto del código.
 */
public interface AlmacenArchivos {

    /** Guarda la imagen y devuelve la URL con la que se puede mostrar. */
    String guardar(MultipartFile archivo);
}
