package com.biblioteca.digital.infrastructure.adapter.out.persistence;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

	private final SpringDataUserRepository repository;

	public JpaUserRepositoryAdapter(SpringDataUserRepository repository) {
		this.repository = repository;
	}

	@Override
	public User save(User user) {
		UserEntity entity = new UserEntity();
		mapToEntity(user, entity);

		UserEntity saved = repository.save(entity);

		return mapToDomain(saved);
	}

	@Override
	public List<User> findAll() {
		return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
	}

	// User → UserEntity
	private void mapToEntity(User user, UserEntity entity) {
		entity.setName(user.getName());
		entity.setEmail(user.getEmail());
		entity.setPasswordHash(user.getPasswordHash());
		entity.setRol(user.getRol());
		entity.setSuscripcionActiva(user.isSuscripcionActiva());
		entity.setFechaSuscripcion(user.getFechaSuscripcion());
		entity.setFechaExpiracionSuscripcion(user.getFechaExpiracionSuscripcion());
	}

	// UserEntity → User
	private User mapToDomain(UserEntity entity) {
		User user = new User();
		user.setId(entity.getId());
		user.setName(entity.getName());
		user.setEmail(entity.getEmail());
		user.setPasswordHash(entity.getPasswordHash());
		user.setRol(entity.getRol());
		user.setSuscripcionActiva(entity.isSuscripcionActiva()); // ← CORREGIDO
		user.setFechaSuscripcion(entity.getFechaSuscripcion());
		user.setFechaExpiracionSuscripcion(entity.getFechaExpiracionSuscripcion());
		return user;
	}

	@Override
    public User findById(Long id) {
        return repository.findById(id)
            .map(this::mapToDomain)
            .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
            .map(this::mapToDomain)
            .orElse(null);
    }

    @Override
    public User update(Long id, User user) {
		Optional<UserEntity> optionalEntity = repository.findById(id);
        if (optionalEntity.isEmpty()) return null;
        
        UserEntity entity = optionalEntity.get();
        mapToEntity(user, entity); // Actualiza campos
        entity.setId(id); // Mantiene ID
        
        UserEntity updated = repository.save(entity);
        return mapToDomain(updated);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
