package com.biblioteca.digital.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class BookEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 300)
	private String titulo;

	@Column(nullable = false, length = 200)
	private String autor;

	@Column(name = "isbn", unique = true, length = 20)
	private String isbn;

	@Column(name = "formato", nullable = false, length = 10)
	private String formato;

	@Column(name = "archivo_path", length = 500)
	private String archivoPath;

	// Constructores + Getters/Setters (igual que UserEntity)
	public BookEntity() {
	}

	// GETTERS/SETTERS (todos)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getArchivoPath() {
		return archivoPath;
	}

	public void setArchivoPath(String archivoPath) {
		this.archivoPath = archivoPath;
	}
}
