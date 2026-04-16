package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.Prestamo;
import java.util.Map;

public interface PrestamoFacade {

    Prestamo crearPrestamoCompleto(Map<String, Object> request);

    Prestamo devolverPrestamo(Long prestamoId);

    Prestamo renovarPrestamo(Long prestamoId, int dias);
}