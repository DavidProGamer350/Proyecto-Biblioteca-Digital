package com.biblioteca.digital.domain.port.out;

import com.biblioteca.digital.domain.model.User;
import java.util.List;

public interface UserRepositoryPort {
	User save(User user);

	List<User> findAll();

	User findById(Long id);

	User findByEmail(String email);

	User update(Long id, User user);

	void deleteById(Long id);
}
