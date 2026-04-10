package com.biblioteca.digital.domain.bridge.acceso;

public interface Acceso {

    boolean puedeDescargar();

    boolean accesoCompleto();

    String tipo();
}