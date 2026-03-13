// com.biblioteca.digital.domain.port.in.PrestamoUseCase.java
package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.Prestamo;
import java.util.List;

public interface PrestamoUseCase {
    Prestamo crearPrestamo(Prestamo prestamo);
    List<Prestamo> listarPrestamos();
    Prestamo buscarPrestamo(Long id);
    Prestamo actualizarPrestamo(Long id, Prestamo prestamo);
    void eliminarPrestamo(Long id);
}
