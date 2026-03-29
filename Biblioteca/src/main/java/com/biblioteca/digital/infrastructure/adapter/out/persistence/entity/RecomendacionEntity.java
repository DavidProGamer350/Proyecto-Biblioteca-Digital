package com.biblioteca.digital.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "recomendaciones")
public class RecomendacionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false)
	private Long usuarioId;

	@Column(name = "libro_id", nullable = false)
	private Long libroId;

	@Column(name = "motivo", length = 500)
	private String motivo;

	@Column(name = "tipo", length = 50)
	private String tipo;

	@Column(name = "prioridad", length = 20)
	private String prioridad;

	@Column(name = "fecha_generacion")
	private LocalDate fechaGeneracion;

	@Column(name = "activa")
	private Boolean activa = true;

	// Constructores
	public RecomendacionEntity() {
	}

	public RecomendacionEntity(Long id, Long usuarioId, Long libroId, String motivo, String tipo, String prioridad,
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

	// GETTERS y SETTERS
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