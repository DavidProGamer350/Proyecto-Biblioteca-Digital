package com.biblioteca.digital.domain.bridge;

import com.biblioteca.digital.domain.bridge.acceso.*;
import com.biblioteca.digital.domain.bridge.libro.*;

public class LibroAccessService {

    public LibroBridge construirLibro(String formato, boolean esPremium) {

        Acceso acceso = esPremium ? new AccesoPremium() : new AccesoGratis();

        switch (formato.toUpperCase()) {
            case "PDF":
                return new LibroPdfBridge(acceso);
            case "EPUB":
                return new LibroEpubBridge(acceso);
            case "MOBI":
                return new LibroMobiBridge(acceso);
            default:
                throw new IllegalArgumentException("Formato no soportado");
        }
    }
}