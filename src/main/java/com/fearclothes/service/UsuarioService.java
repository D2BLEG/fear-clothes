package com.fearclothes.service;

import com.fearclothes.dto.PerfilForm;
import com.fearclothes.dto.RegistroForm;
import com.fearclothes.exception.ReglaNegocioException;
import com.fearclothes.model.Comprador;
import com.fearclothes.model.Usuario;
import com.fearclothes.model.Vendedor;
import com.fearclothes.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Transactional
    public Usuario registrar(RegistroForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (usuarios.existsByEmail(email)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        String hash = encoder.encode(form.getPassword());
        Usuario usuario = "VENDEDOR".equals(form.getRol())
                ? new Vendedor(form.getNombre().trim(), email, hash)
                : new Comprador(form.getNombre().trim(), email, hash);
        return usuarios.save(usuario);
    }

    /** Actualiza el nombre y, si se escribió una nueva, la contraseña del usuario con sesión iniciada. */
    @Transactional
    public Usuario actualizarPerfil(Long usuarioId, PerfilForm form) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe"));
        usuario.setNombre(form.getNombre().trim());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            usuario.setPassword(encoder.encode(form.getPassword()));
        }
        return usuario;
    }

    /** Devuelve el usuario con sesión iniciada, o null si es un visitante anónimo. */
    @Transactional(readOnly = true)
    public Usuario actual(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return null;
        }
        return usuarios.findByEmail(auth.getName()).orElse(null);
    }

    /** Listado para el panel de administración. */
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarios.findAll(Sort.by("id"));
    }

    /** Activa o desactiva una cuenta (un admin no puede desactivarse a sí mismo ni a otro admin). */
    @Transactional
    public void cambiarEstado(Long id, Usuario actor) {
        Usuario usuario = usuarios.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe"));
        if (actor != null && usuario.getId().equals(actor.getId())) {
            throw new ReglaNegocioException("No puedes desactivar tu propia cuenta");
        }
        if ("ADMIN".equals(usuario.getRol())) {
            throw new ReglaNegocioException("No se puede desactivar a otro administrador");
        }
        usuario.setActivo(!usuario.isActivo());
    }
}
