package com.biblioteca.digital.domain.bridge.libro;

import com.biblioteca.digital.domain.bridge.acceso.Acceso;

public abstract class LibroBridge {

    protected Acceso acceso;

    protected LibroBridge(Acceso acceso) {
        this.acceso = acceso;
    }

    public boolean puedeDescargar() {
        return acceso.puedeDescargar();
    }

    public boolean accesoCompleto() {
        return acceso.accesoCompleto();
    }

    public String tipoAcceso() {
        return acceso.tipo();
    }

    public abstract String getFormato();
}