package com.biblioteca.digital.domain.bridge.acceso;

public class AccesoGratis implements Acceso {

    @Override
    public boolean puedeDescargar() {
        return false;
    }

    @Override
    public boolean accesoCompleto() {
        return false;
    }

    @Override
    public String tipo() {
        return "GRATIS";
    }
}