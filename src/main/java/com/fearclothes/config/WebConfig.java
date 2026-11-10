package com.fearclothes.config;

import com.fearclothes.storage.AlmacenLocal;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Publica la carpeta "uploads" en la URL /uploads/** para que el navegador pueda mostrar las imágenes. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AlmacenLocal almacen;

    public WebConfig(AlmacenLocal almacen) {
        this.almacen = almacen;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = almacen.getCarpeta().toUri().toString();
        if (!ubicacion.endsWith("/")) {
            ubicacion += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(ubicacion);
    }
}
