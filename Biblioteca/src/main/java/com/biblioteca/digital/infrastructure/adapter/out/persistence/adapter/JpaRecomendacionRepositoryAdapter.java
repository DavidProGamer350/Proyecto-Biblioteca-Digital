package com.biblioteca.digital.infrastructure.adapter.out.persistence.adapter;

import com.biblioteca.digital.domain.model.Recomendacion;
import com.biblioteca.digital.domain.port.out.RecomendacionRepositoryPort;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.entity.RecomendacionEntity;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.repository.SpringDataRecomendacionesRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JpaRecomendacionRepositoryAdapter implements RecomendacionRepositoryPort {

    private final SpringDataRecomendacionesRepository repository;

    public JpaRecomendacionRepositoryAdapter(SpringDataRecomendacionesRepository repository) {
        this.repository = repository;
    }

    @Override
    public Recomendacion save(Recomendacion recomendacion) {
        RecomendacionEntity entity = new RecomendacionEntity();
        mapToEntity(recomendacion, entity);
        RecomendacionEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Recomendacion> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public Recomendacion findById(Long id) {
        return repository.findById(id).map(this::mapToDomain).orElse(null);
    }

    @Override
    public Recomendacion findByUsuarioIdAndLibroId(Long usuarioId, Long libroId) {
        return repository.findByUsuarioIdAndLibroId(usuarioId, libroId).map(this::mapToDomain).orElse(null);
    }

    @Override
    public Recomendacion update(Long id, Recomendacion recomendacion) {
        return repository.findById(id)
                .map(entity -> {
                    mapToEntity(recomendacion, entity);
                    entity.setId(id);
                    return mapToDomain(repository.save(entity));
                })
                .orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void mapToEntity(Recomendacion domain, RecomendacionEntity entity) {
        entity.setUsuarioId(domain.getUsuarioId());
        entity.setLibroId(domain.getLibroId());
        entity.setMotivo(domain.getMotivo());
        entity.setTipo(domain.getTipo());
        entity.setPrioridad(domain.getPrioridad());
        entity.setFechaGeneracion(domain.getFechaGeneracion());
        entity.setActiva(domain.getActiva());
    }

    private Recomendacion mapToDomain(RecomendacionEntity entity) {
        return new Recomendacion(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getLibroId(),
                entity.getMotivo(),
                entity.getTipo(),
                entity.getPrioridad(),
                entity.getFechaGeneracion(),
                entity.getActiva());
    }
}