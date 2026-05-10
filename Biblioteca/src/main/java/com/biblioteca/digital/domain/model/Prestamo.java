package com.biblioteca.digital.domain.model;

import java.time.LocalDate;
import java.util.List;

public class Prestamo {

	private Long id;
	private Long usuarioId; // Obligatorio
	private Long libroId; // Obligatorio
	private LocalDate fechaPrestamo; // Obligatorio
	private LocalDate fechaDevolucionEsperada;
	private LocalDate fechaDevolucionReal;
	private String estado; // "ACTIVO", "VENCIDO", "DEVUELTO"
	private String observaciones;
	private String codigoPrestamo;
	private Integer multasAcumuladas;
	private String notificacionEmail;
	private Boolean requiereAprobacion;
    private String motivoRechazo;
    private Integer vecesRenovado;

	// Constructor privado (solo Builder)
	private Prestamo(Builder builder) {
		this.id = builder.id;
		this.usuarioId = builder.usuarioId;
		this.libroId = builder.libroId;
		this.fechaPrestamo = builder.fechaPrestamo;
		this.fechaDevolucionEsperada = builder.fechaDevolucionEsperada;
		this.fechaDevolucionReal = builder.fechaDevolucionReal;
		this.estado = builder.estado;
		this.observaciones = builder.observaciones;
		this.codigoPrestamo = builder.codigoPrestamo;
		this.multasAcumuladas = builder.multasAcumuladas;
		this.notificacionEmail = builder.notificacionEmail;
		this.requiereAprobacion = builder.requiereAprobacion;
		this.motivoRechazo = builder.motivoRechazo;
		this.vecesRenovado = builder.vecesRenovado;
	}

	public Long getId() {
		return id;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public Long getLibroId() {
		return libroId;
	}

	public LocalDate getFechaPrestamo() {
		return fechaPrestamo;
	}

	public LocalDate getFechaDevolucionEsperada() {
		return fechaDevolucionEsperada;
	}

	public LocalDate getFechaDevolucionReal() {
		return fechaDevolucionReal;
	}

	public String getEstado() {
		return estado;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public String getCodigoPrestamo() {
		return codigoPrestamo;
	}

	public Integer getMultasAcumuladas() {
		return multasAcumuladas;
	}


	public String getNotificacionEmail() {
		return notificacionEmail;
	}


	public Boolean getRequiereAprobacion() {
		return requiereAprobacion;
	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	public Integer getVecesRenovado() {
		return vecesRenovado;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Long id;
		private Long usuarioId;
		private Long libroId;
		private LocalDate fechaPrestamo;
		private LocalDate fechaDevolucionEsperada;
		private LocalDate fechaDevolucionReal;
		private String estado;
		private String observaciones;
		private String codigoPrestamo;
		private Integer multasAcumuladas;
		private String notificacionEmail;
		private Boolean requiereAprobacion;
		private String motivoRechazo;
		private Integer vecesRenovado;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder usuarioId(Long usuarioId) {
			this.usuarioId = usuarioId;
			return this;
		}

		public Builder libroId(Long libroId) {
			this.libroId = libroId;
			return this;
		}

		public Builder fechaPrestamo(LocalDate fechaPrestamo) {
			this.fechaPrestamo = fechaPrestamo;
			return this;
		}

		public Builder fechaDevolucionEsperada(LocalDate fecha) {
			this.fechaDevolucionEsperada = fecha;
			return this;
		}

		public Builder fechaDevolucionReal(LocalDate fechaDevolucionReal) {
			this.fechaDevolucionReal = fechaDevolucionReal;
			return this;
		}

		public Builder estado(String estado) {
			this.estado = estado;
			return this;
		}

		public Builder observaciones(String observaciones) {
			this.observaciones = observaciones;
			return this;
		}

		public Builder codigoPrestamo(String codigoPrestamo) {
			this.codigoPrestamo = codigoPrestamo;
			return this;
		}

		public Builder multasAcumuladas(Integer multasAcumuladas) {
			this.multasAcumuladas = multasAcumuladas;
			return this;
		}


		public Builder notificacionEmail(String notificacionEmail) {
			this.notificacionEmail = notificacionEmail;
			return this;
		}

		public Builder requiereAprobacion(Boolean requiereAprobacion) {
			this.requiereAprobacion = requiereAprobacion;
			return this;
		}

		public Builder motivoRechazo(String motivoRechazo) {
			this.motivoRechazo = motivoRechazo;
			return this;
		}

		public Builder vecesRenovado(Integer vecesRenovado) {
			this.vecesRenovado = vecesRenovado;
			return this;
		}

		public Prestamo build() {
			return new Prestamo(this);
		}
	}
}