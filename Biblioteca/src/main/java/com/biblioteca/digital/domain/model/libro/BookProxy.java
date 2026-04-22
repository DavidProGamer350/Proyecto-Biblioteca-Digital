package com.biblioteca.digital.domain.model.libro;

import com.biblioteca.digital.domain.model.Book;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BookProxy implements BookContent {
    private final Book book;
    private final String basePath;
    private byte[] contenido;
    private boolean cargado;

    public BookProxy(Book book, String basePath) {
        this.book = book;
        this.basePath = basePath;
        this.contenido = null;
        this.cargado = false;
    }

    @Override
    public Book getMetadata() {
        return book;
    }

    @Override
    public synchronized byte[] getContenido() {
        if (!cargado) {
            cargarContenido();
        }
        return contenido;
    }

    @Override
    public synchronized byte[] descargar() {
        if (!cargado) {
            cargarContenido();
        }
        return contenido;
    }

    @Override
    public boolean estaCargado() {
        return cargado;
    }

    private void cargarContenido() {
        try {
            String archivoPath = book.getArchivoPath();
            if (archivoPath == null || archivoPath.isEmpty()) {
                throw new IllegalStateException("Ruta de archivo no disponible para el libro: " + book.getId());
            }
            Path path = Paths.get(basePath, archivoPath);
            if (!Files.exists(path)) {
                throw new IllegalStateException("Archivo no encontrado: " + path);
            }
            contenido = Files.readAllBytes(path);
            cargado = true;
        } catch (IOException e) {
            throw new IllegalStateException("Error al cargar contenido del libro: " + book.getId(), e);
        }
    }

    public Long getLibroId() {
        return book.getId();
    }

    public String getTitulo() {
        return book.getTitulo();
    }
}