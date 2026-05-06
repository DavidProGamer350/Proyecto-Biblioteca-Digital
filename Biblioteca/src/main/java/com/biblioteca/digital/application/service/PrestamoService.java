// com.biblioteca.digital.application.service.PrestamoService.java
package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import com.biblioteca.digital.domain.port.out.PrestamoRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService implements PrestamoUseCase {

    private final PrestamoRepositoryPort repository;

    public PrestamoService(PrestamoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Prestamo crearPrestamo(Prestamo prestamo) {
        // ⭐ BUILDER usado por Controller (se ve en JSON directo)
        return repository.save(prestamo);
    }

    @Override
    public List<Prestamo> listarPrestamos() {
        return repository.findAll();
    }

    @Override
    public Prestamo buscarPrestamo(Long id) {
        return repository.findById(id);
    }

    @Override
    public Prestamo actualizarPrestamo(Long id, Prestamo prestamo) {
        Prestamo existing = repository.findById(id);
        // Update logic con Builder si quieres
        return repository.save(prestamo);
    }

    @Override
    public void eliminarPrestamo(Long id) {
        repository.deleteById(id);
    }
}
