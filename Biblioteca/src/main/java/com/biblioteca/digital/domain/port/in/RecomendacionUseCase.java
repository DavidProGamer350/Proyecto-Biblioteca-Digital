package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.Recomendacion;
import java.util.List;

public interface RecomendacionUseCase {
	Recomendacion createRecomendacion(Recomendacion recomendacion);

	List<Recomendacion> getAllRecomendaciones();

	Recomendacion getRecomendacionById(Long id);

	Recomendacion getRecomendacionByUsuarioAndLibro(Long usuarioId, Long libroId);

	Recomendacion updateRecomendacion(Long id, Recomendacion recomendacion);

	void deleteRecomendacion(Long id);
}
