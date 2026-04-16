package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        // Cifrar contraseña con BCrypt
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepositoryPort.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepositoryPort.findById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email);
    }
    
    @Override
    public User updateUser(Long id, User user) {
        // Cifrar contraseña con BCrypt si se proporciona una nueva
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepositoryPort.update(id, user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepositoryPort.deleteById(id);
    }
}
