package com.fearclothes.repository;

import com.fearclothes.model.EstadoPublicacion;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;
import com.fearclothes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicacionRepository
        extends JpaRepository<Publicacion, Long>, JpaSpecificationExecutor<Publicacion> {

    List<Publicacion> findByVendedorOrderByFechaCreacionDesc(Usuario vendedor);

    List<Publicacion> findByEstadoAndTipoAndFechaCierreBefore(
            EstadoPublicacion estado, TipoPublicacion tipo, LocalDateTime limite);
}
