package com.fearclothes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroForm {

    @NotBlank(message = "Escribe tu nombre")
    @Size(max = 80)
    private String nombre;

    @NotBlank(message = "Escribe tu correo")
    @Email(message = "Correo inválido")
    private String email;

    @NotBlank(message = "Escribe una contraseña")
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;

    /** COMPRADOR o VENDEDOR. */
    private String rol = "COMPRADOR";

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
