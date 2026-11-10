package com.fearclothes.model;

import jakarta.persistence.*;

/**
 * Clase abstracta base de los usuarios (HERENCIA + ABSTRACCIÓN).
 * Mapeo JOINED: los datos comunes viven en "usuarios" y cada subclase guarda solo
 * sus datos propios en su tabla (compradores, vendedores, administradores), enlazada por la misma llave.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean activo = true;

    protected Usuario() {
    }

    protected Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    /** Rol de seguridad (sin prefijo ROLE_). Cada subclase define el suyo (POLIMORFISMO). */
    public abstract String getRol();

    /** Por defecto un usuario no puede publicar; Vendedor lo redefine (SOBRESCRITURA). */
    public boolean puedePublicar() {
        return false;
    }

    public String getDescripcionPerfil() {
        return nombre + " (" + getRol() + ")";
    }

    public Long getId() {
        return id;
    }

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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
