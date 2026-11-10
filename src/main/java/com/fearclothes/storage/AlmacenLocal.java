package com.fearclothes.storage;

import com.fearclothes.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Guarda las imágenes en la carpeta local "uploads" y las sirve bajo /uploads/**. */
@Service
public class AlmacenLocal implements AlmacenArchivos {

    private final Path carpeta;

    public AlmacenLocal(@Value("${app.uploads.dir:uploads}") String directorio) {
        this.carpeta = Paths.get(directorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(carpeta);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + carpeta, e);
        }
    }

    public Path getCarpeta() {
        return carpeta;
    }

    @Override
    public String guardar(MultipartFile archivo) {
        String extension = extensionPara(archivo.getContentType());
        if (extension == null) {
            throw new ReglaNegocioException("La imagen debe ser JPG, PNG o WEBP");
        }
        // El nombre lo generamos nosotros (UUID): nunca se usa el nombre que manda el usuario
        String nombre = UUID.randomUUID() + extension;
        try (InputStream entrada = archivo.getInputStream()) {
            Files.copy(entrada, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo guardar la imagen, intenta de nuevo");
        }
        return "/uploads/" + nombre;
    }

    private String extensionPara(String tipoContenido) {
        if (tipoContenido == null) {
            return null;
        }
        return switch (tipoContenido) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> null;
        };
    }
}
