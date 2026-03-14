// com.biblioteca.digital.infrastructure.adapter.out.persistence.adapter.JpaPrestamoRepositoryAdapter.java
package com.biblioteca.digital.infrastructure.adapter.out.persistence.adapter;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.out.PrestamoRepositoryPort;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.entity.PrestamoEntity;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.repository.SpringDataPrestamoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JpaPrestamoRepositoryAdapter implements PrestamoRepositoryPort {

    private final SpringDataPrestamoRepository repository;

    public JpaPrestamoRepositoryAdapter(SpringDataPrestamoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Prestamo save(Prestamo prestamo) {
        PrestamoEntity entity = new PrestamoEntity();
        mapToEntity(prestamo, entity);
        PrestamoEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Prestamo> findAll() {
        return repository.findAll().stream()
            .map(this::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Prestamo findById(Long id) {
        return repository.findById(id)
            .map(this::mapToDomain)
            .orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void mapToEntity(Prestamo prestamo, PrestamoEntity entity) {
        entity.setUsuarioId(prestamo.getUsuarioId());
        entity.setLibroId(prestamo.getLibroId());
        entity.setFechaPrestamo(prestamo.getFechaPrestamo());
        entity.setFechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada());
        entity.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
        entity.setEstado(prestamo.getEstado());
        entity.setObservaciones(prestamo.getObservaciones());
        entity.setCodigoPrestamo(prestamo.getCodigoPrestamo());
        entity.setMultasAcumuladas(prestamo.getMultasAcumuladas());
        entity.setNotificacionEmail(prestamo.getNotificacionEmail());
        entity.setRequiereAprobacion(prestamo.getRequiereAprobacion());
        entity.setMotivoRechazo(prestamo.getMotivoRechazo());
    }

    private Prestamo mapToDomain(PrestamoEntity entity) {
        return Prestamo.builder()
            .id(entity.getId())
            .usuarioId(entity.getUsuarioId())
            .libroId(entity.getLibroId())
            .fechaPrestamo(entity.getFechaPrestamo())
            .fechaDevolucionEsperada(entity.getFechaDevolucionEsperada())
            .fechaDevolucionReal(entity.getFechaDevolucionReal())
            .estado(entity.getEstado())
            .observaciones(entity.getObservaciones())
            .codigoPrestamo(entity.getCodigoPrestamo())
            .multasAcumuladas(entity.getMultasAcumuladas())
            .notificacionEmail(entity.getNotificacionEmail())
            .requiereAprobacion(entity.getRequiereAprobacion())
            .motivoRechazo(entity.getMotivoRechazo())
            .build();
    }
}
