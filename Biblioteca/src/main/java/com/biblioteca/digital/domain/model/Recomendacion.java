package com.biblioteca.digital.domain.model;

import java.time.LocalDate;

public class Recomendacion implements Prototype<Recomendacion> {
	private Long id;
	private Long usuarioId;
	private Long libroId;
	private String motivo;
	private String tipo; // "premium", "historial", "novedades"
	private String prioridad; // "alta", "media", "baja"
	private LocalDate fechaGeneracion;
	private Boolean activa;

	public Recomendacion() {
	}

	public Recomendacion(Long id, Long usuarioId, Long libroId, String motivo, String tipo, String prioridad,
			LocalDate fechaGeneracion, Boolean activa) {
		this.id = id;
		this.usuarioId = usuarioId;
		this.libroId = libroId;
		this.motivo = motivo;
		this.tipo = tipo;
		this.prioridad = prioridad;
		this.fechaGeneracion = fechaGeneracion;
		this.activa = activa;
	}

	public Recomendacion(Recomendacion other) {
		this.id = other.id;
		this.usuarioId = other.usuarioId;
		this.libroId = other.libroId;
		this.motivo = other.motivo;
		this.tipo = other.tipo;
		this.prioridad = other.prioridad;
		this.fechaGeneracion = other.fechaGeneracion;
		this.activa = other.activa;
	}

	@Override
	public Recomendacion copy() {
		return new Recomendacion(this);
	}

	// Getters y setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Long getLibroId() {
		return libroId;
	}

	public void setLibroId(Long libroId) {
		this.libroId = libroId;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}

	public LocalDate getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(LocalDate fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	public Boolean getActiva() {
		return activa;
	}

	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
}