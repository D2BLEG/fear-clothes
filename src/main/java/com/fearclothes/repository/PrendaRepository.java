package com.fearclothes.repository;

import com.fearclothes.model.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrendaRepository extends JpaRepository<Prenda, Long> {

    boolean existsByMarcaId(Long marcaId);

    boolean existsByTallaId(Long tallaId);
}
