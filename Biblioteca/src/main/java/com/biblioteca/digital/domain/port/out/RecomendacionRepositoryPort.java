package com.biblioteca.digital.domain.port.out;

import com.biblioteca.digital.domain.model.Recomendacion;
import java.util.List;

public interface RecomendacionRepositoryPort {
	
	Recomendacion save(Recomendacion recomendacion);

	List<Recomendacion> findAll();

	Recomendacion findById(Long id);

	Recomendacion findByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

	Recomendacion update(Long id, Recomendacion recomendacion);

	void deleteById(Long id);
}