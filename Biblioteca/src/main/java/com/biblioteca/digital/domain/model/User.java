package com.biblioteca.digital.domain.model;

import java.time.LocalDate;

public class User {
	private Long id;
	private String name;
	private String email;
	private String passwordHash;
	private String rol; // "USER", "ADMIN"
	private boolean suscripcionActiva;
	private LocalDate fechaSuscripcion;
	private LocalDate fechaExpiracionSuscripcion;

	// Constructores
	public User() {
	}

	public User(Long id, String name, String email, String passwordHash, String rol, boolean suscripcionActiva,
			LocalDate fechaSuscripcion, LocalDate fechaExpiracionSuscripcion) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
		this.rol = rol;
		this.suscripcionActiva = suscripcionActiva;
		this.fechaSuscripcion = fechaSuscripcion;
		this.fechaExpiracionSuscripcion = fechaExpiracionSuscripcion;
	}

	// GETTERS y SETTERS
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		rol = rol;
	}

	public boolean isSuscripcionActiva() {
		return suscripcionActiva;
	}

	public void setSuscripcionActiva(boolean suscripcionActiva) {
		this.suscripcionActiva = suscripcionActiva;
	}

	public LocalDate getFechaSuscripcion() {
		return fechaSuscripcion;
	}

	public void setFechaSuscripcion(LocalDate fechaSuscripcion) {
		this.fechaSuscripcion = fechaSuscripcion;
	}

	public LocalDate getFechaExpiracionSuscripcion() {
		return fechaExpiracionSuscripcion;
	}

	public void setFechaExpiracionSuscripcion(LocalDate fechaExpiracionSuscripcion) {
		this.fechaExpiracionSuscripcion = fechaExpiracionSuscripcion;
	}
}
