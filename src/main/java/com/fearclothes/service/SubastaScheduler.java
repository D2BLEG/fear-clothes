package com.fearclothes.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Revisa cada minuto si hay subastas que ya vencieron y las cierra. */
@Component
public class SubastaScheduler {

    private final PublicacionService publicaciones;

    public SubastaScheduler(PublicacionService publicaciones) {
        this.publicaciones = publicaciones;
    }

    @Scheduled(initialDelay = 30_000, fixedRate = 60_000)
    public void cerrarVencidas() {
        publicaciones.cerrarSubastasVencidas();
    }
}
