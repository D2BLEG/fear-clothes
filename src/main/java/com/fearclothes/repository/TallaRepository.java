package com.fearclothes.repository;

import com.fearclothes.model.Talla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallaRepository extends JpaRepository<Talla, Long> {

    List<Talla> findAllByOrderByIdAsc();

    boolean existsByEtiquetaIgnoreCase(String etiqueta);

    boolean existsByEtiquetaIgnoreCaseAndIdNot(String etiqueta, Long id);
}
