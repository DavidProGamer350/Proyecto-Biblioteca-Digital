package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.subscription.SubscriptionManager;
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
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
		User user = userUseCase.getUserByEmail(email);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		User updated = userUseCase.updateUser(id, user);
		return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
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

		boolean premium = SubscriptionManager.INSTANCE.isPremium(user);

		return ResponseEntity.ok(premium);
	}

}
