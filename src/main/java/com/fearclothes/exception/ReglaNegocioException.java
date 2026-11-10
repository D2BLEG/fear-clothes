package com.fearclothes.exception;

/** Se lanza cuando una operación viola una regla del negocio (oferta muy baja, subasta cerrada, etc.). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
