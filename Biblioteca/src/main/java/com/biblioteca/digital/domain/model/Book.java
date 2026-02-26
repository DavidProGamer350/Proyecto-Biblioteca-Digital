package com.biblioteca.digital.domain.model;

public class Book {
	private Long id;
	private String titulo;
	private String autor;
	private String isbn;
	private BookFormato formato;
	private String archivoPath;

	// Constructor vacío (para mapeo)
	public Book() {
	}

	// Constructor completo
	public Book(Long id, String titulo, String autor, String isbn, BookFormato formato, String archivoPath) {
		this.id = id;
		this.titulo = titulo;
		this.autor = autor;
		this.isbn = isbn;
		this.formato = formato;
		this.archivoPath = archivoPath;
	}

	// GETTERS / SETTERS
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

	public BookFormato getFormato() {
		return formato;
	}

	public void setFormato(BookFormato formato) {
		this.formato = formato;
	}

	public String getArchivoPath() {
		return archivoPath;
	}

	public void setArchivoPath(String archivoPath) {
		this.archivoPath = archivoPath;
	}
}
