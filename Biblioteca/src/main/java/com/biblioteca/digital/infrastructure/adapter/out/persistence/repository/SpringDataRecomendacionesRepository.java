package com.biblioteca.digital.infrastructure.adapter.out.persistence.repository;

import com.biblioteca.digital.infrastructure.adapter.out.persistence.entity.RecomendacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataRecomendacionesRepository extends JpaRepository<RecomendacionEntity, Long> {
	Optional<RecomendacionEntity> findByUsuarioIdAndLibroId(Long usuarioId, Long libroId);
}