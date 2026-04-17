package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.application.service.SubscriptionService;
import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserUseCase userUseCase;
	private final SubscriptionService subscriptionService;

	public UserController(UserUseCase userUseCase, SubscriptionService subscriptionService) {
		this.userUseCase = userUseCase;
		this.subscriptionService = subscriptionService;
	}
	
	@GetMapping("/test")
	public String test() {
		return "Users endpoint working";
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

	@GetMapping("/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
		User user = userUseCase.getUserByEmail(email);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		User user = userUseCase.getUserById(id);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		User existing = userUseCase.getUserById(id);
		if (existing == null) {
			return ResponseEntity.notFound().build();
		}
		User updated = userUseCase.updateUser(id, user);
		return ResponseEntity.ok(updated);
	}

@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userUseCase.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/premium")
    public ResponseEntity<Boolean> isUserPremium(@PathVariable Long id) {
	    User user = userUseCase.getUserById(id);
	    if (user == null) {
	        return ResponseEntity.notFound().build();
	    }
	    boolean premium = subscriptionService.verificarSuscripcionPremium(user);
	    return ResponseEntity.ok(premium);
	}

    @PutMapping("/{id}/premium")
    public ResponseEntity<User> togglePremium(@PathVariable Long id) {
        User user = userUseCase.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setSuscripcionActiva(!user.isSuscripcionActiva());
        User updated = userUseCase.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

}
