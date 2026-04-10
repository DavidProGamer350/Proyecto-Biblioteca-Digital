package com.biblioteca.digital.domain.bridge.libro;

import com.biblioteca.digital.domain.bridge.acceso.Acceso;

public class LibroEpubBridge extends LibroBridge {

    public LibroEpubBridge(Acceso acceso) {
        super(acceso);
    }

    @Override
    public String getFormato() {
        return "EPUB";
    }
}