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
    public Prestamo devolverPrestamo(Long id) {
        Prestamo existing = repository.findById(id);
        if (existing == null) return null;

        Prestamo devuelto = Prestamo.builder()
            .id(existing.getId())
            .usuarioId(existing.getUsuarioId())
            .libroId(existing.getLibroId())
            .fechaPrestamo(existing.getFechaPrestamo())
            .fechaDevolucionEsperada(existing.getFechaDevolucionEsperada())
            .fechaDevolucionReal(java.time.LocalDate.now())
            .estado("DEVUELTO")
            .observaciones(existing.getObservaciones())
            .multasAcumuladas(0)
            .requiereAprobacion(existing.getRequiereAprobacion())
            .build();

        return repository.save(devuelto);
    }

    @Override
    public void eliminarPrestamo(Long id) {
        repository.deleteById(id);
    }
}
