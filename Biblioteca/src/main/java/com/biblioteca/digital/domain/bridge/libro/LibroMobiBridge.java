package com.biblioteca.digital.domain.bridge.libro;

import com.biblioteca.digital.domain.bridge.acceso.Acceso;

public class LibroMobiBridge extends LibroBridge {

    public LibroMobiBridge(Acceso acceso) {
        super(acceso);
    }

    @Override
    public String getFormato() {
        return "MOBI";
    }
}