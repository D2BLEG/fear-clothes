package com.fearclothes.repository;

import com.fearclothes.model.Orden;
import com.fearclothes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByCompradorOrderByFechaDesc(Usuario comprador);

    List<Orden> findByPublicacion_VendedorOrderByFechaDesc(Usuario vendedor);

    boolean existsByPublicacion_Id(Long publicacionId);
}
