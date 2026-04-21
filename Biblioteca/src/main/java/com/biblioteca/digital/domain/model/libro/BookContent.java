package com.biblioteca.digital.domain.model.libro;

import com.biblioteca.digital.domain.model.Book;

public interface BookContent {
    Book getMetadata();

    byte[] getContenido();

    byte[] descargar();

    boolean estaCargado();
}