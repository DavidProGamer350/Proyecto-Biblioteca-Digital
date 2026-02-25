package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userUseCase.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userUseCase.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userUseCase.getUserById(id);
        return user != null ? 
            ResponseEntity.ok(user) : 
            ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userUseCase.getUserByEmail(email);
        return user != null ? 
            ResponseEntity.ok(user) : 
            ResponseEntity.notFound().build();
    }
}
