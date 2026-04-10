package com.biblioteca.digital.domain.bridge.acceso;

public class AccesoPremium implements Acceso {

    @Override
    public boolean puedeDescargar() {
        return true;
    }

    @Override
    public boolean accesoCompleto() {
        return true;
    }

    @Override
    public String tipo() {
        return "PREMIUM";
    }
}