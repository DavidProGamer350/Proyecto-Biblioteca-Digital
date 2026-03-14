// com.biblioteca.digital.domain.port.out.PrestamoRepositoryPort.java
package com.biblioteca.digital.domain.port.out;

import com.biblioteca.digital.domain.model.Prestamo;
import java.util.List;

public interface PrestamoRepositoryPort {
    Prestamo save(Prestamo prestamo);
    List<Prestamo> findAll();
    Prestamo findById(Long id);
    void deleteById(Long id);
}
