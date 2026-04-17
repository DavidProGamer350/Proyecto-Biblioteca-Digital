package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import com.biblioteca.digital.domain.port.out.PrestamoRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PrestamoService implements PrestamoUseCase {

    private final PrestamoRepositoryPort repository;

    public PrestamoService(PrestamoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Prestamo crearPrestamo(Prestamo prestamo) {
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
        return repository.save(prestamo);
    }

    @Override
    public void eliminarPrestamo(Long id) {
        repository.deleteById(id);
    }
}