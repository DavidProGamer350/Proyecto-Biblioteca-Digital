package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.User;
import java.util.List;

public interface UserUseCase {
	User createUser(User user);

	List<User> getAllUsers();

	User getUserById(Long id); // ← NUEVO

	User getUserByEmail(String email); // ← NUEVO

	User updateUser(Long id, User user);

	void deleteUser(Long id);

}
