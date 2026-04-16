package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.model.dto.AuthRequest;
import com.biblioteca.digital.domain.model.dto.AuthResponse;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.infrastructure.config.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserUseCase userUseCase;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserUseCase userUseCase, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userUseCase = userUseCase;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        Optional<User> existingUser = Optional.ofNullable(userUseCase.getUserByEmail(request.getEmail()));
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRol("USER");
        newUser.setSuscripcionActiva(false);

        User created = userUseCase.createUser(newUser);

        String token = jwtUtils.generateToken(
                created.getId(),
                created.getEmail(),
                created.getName(),
                created.getRol()
        );

        AuthResponse response = new AuthResponse(
                token,
                created.getId(),
                created.getName(),
                created.getEmail(),
                created.getRol()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = userUseCase.getUserByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtils.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRol()
        );

        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRol()
        );

        return ResponseEntity.ok(response);
    }
}