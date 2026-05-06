package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Recomendacion;
import com.biblioteca.digital.domain.port.in.RecomendacionUseCase;
import com.biblioteca.digital.domain.port.out.RecomendacionRepositoryPort;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import com.biblioteca.digital.domain.service.RecomendacionRegistry;
import java.time.LocalDate;
import java.util.List;

public class RecomendacionService implements RecomendacionUseCase {

	private final RecomendacionRepositoryPort recomendacionRepositoryPort;

	public RecomendacionService(RecomendacionRepositoryPort recomendacionRepositoryPort) {
		this.recomendacionRepositoryPort = recomendacionRepositoryPort;
		cargarPrototipos();
	}

	public void cargarPrototipos() {

		// PROTOTIPO PREMIUM
		Recomendacion premium = new Recomendacion();
		premium.setMotivo("Recomendación para usuarios premium");
		premium.setTipo("premium");
		premium.setPrioridad("alta");
		premium.setFechaGeneracion(LocalDate.now());
		premium.setActiva(true);

		RecomendacionRegistry.registrar("premium", premium);

		// PROTOTIPO HISTORIAL
		Recomendacion historial = new Recomendacion();
		historial.setMotivo("Basado en tu historial de lectura");
		historial.setTipo("historial");
		historial.setPrioridad("media");
		historial.setFechaGeneracion(LocalDate.now());
		historial.setActiva(true);

		RecomendacionRegistry.registrar("historial", historial);
	}

	public Recomendacion generarRecomendacion(String tipo, Long usuarioId, Long libroId) {
		Recomendacion rec = RecomendacionRegistry.crear(tipo);
		rec.setId(System.currentTimeMillis());
		rec.setUsuarioId(usuarioId);
		rec.setLibroId(libroId);
		rec.setFechaGeneracion(LocalDate.now());
		return rec;
	}

	@Override
	public Recomendacion createRecomendacion(Recomendacion recomendacion) {
		return recomendacionRepositoryPort.save(recomendacion);
	}

	@Override
	public List<Recomendacion> getAllRecomendaciones() {
		return recomendacionRepositoryPort.findAll();
	}

	@Override
	public Recomendacion getRecomendacionById(Long id) {
		return recomendacionRepositoryPort.findById(id);
	}

	@Override
	public Recomendacion getRecomendacionByUsuarioAndLibro(Long usuarioId, Long libroId) {
		return recomendacionRepositoryPort.findByUsuarioIdAndLibroId(usuarioId, libroId);
	}

	@Override
	public Recomendacion updateRecomendacion(Long id, Recomendacion recomendacion) {
		return recomendacionRepositoryPort.update(id, recomendacion);
	}

	@Override
	public void deleteRecomendacion(Long id) {
		recomendacionRepositoryPort.deleteById(id);
	}
}