package com.biblioteca.digital.domain.bridge.libro;

import com.biblioteca.digital.domain.bridge.acceso.Acceso;

public class LibroPdfBridge extends LibroBridge {

    public LibroPdfBridge(Acceso acceso) {
        super(acceso);
    }

    @Override
    public String getFormato() {
        return "PDF";
    }
}