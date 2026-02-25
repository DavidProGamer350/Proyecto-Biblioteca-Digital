package com.biblioteca.digital.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password_hash", length = 255)
	private String passwordHash;

	@Column(nullable = false, length = 20)
	private String rol; // "USER", "ADMIN"

	@Column(name = "suscripcion_activa")
	private boolean suscripcionActiva = false;

	@Column(name = "fecha_suscripcion")
	private LocalDate fechaSuscripcion;

	@Column(name = "fecha_expiracion_suscripcion")
	private LocalDate fechaExpiracionSuscripcion;

	// Constructores
	public UserEntity() {
	}

	public UserEntity(Long id, String name, String email, String passwordHash, String rol, boolean suscripcionActiva,
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

	// GETTERS y SETTERS (todos obligatorios)
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
		this.rol = rol;
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
