package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import java.util.List;

public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido");
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
        return userRepositoryPort.update(id, user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepositoryPort.deleteById(id);
    }
}
