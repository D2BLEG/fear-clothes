package com.fearclothes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Edición del perfil del usuario con sesión iniciada. */
public class PerfilForm {

    @NotBlank(message = "Escribe tu nombre")
    @Size(max = 80)
    private String nombre;

    /** Opcional: si se deja vacío, la contraseña no cambia. */
    @Pattern(regexp = "^$|.{6,}", message = "Mínimo 6 caracteres")
    private String password;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
