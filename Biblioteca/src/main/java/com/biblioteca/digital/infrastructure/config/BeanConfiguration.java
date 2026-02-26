package com.biblioteca.digital.infrastructure.config;

import com.biblioteca.digital.application.service.BookService;
import com.biblioteca.digital.application.service.UserService;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.BookRepositoryPort;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

	@Bean
	public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort) {
		return new UserService(userRepositoryPort);
	}

	@Bean
	public BookUseCase bookUseCase(BookRepositoryPort bookRepositoryPort) {
		return new BookService(bookRepositoryPort); // Adjust constructor args as needed
	}
}
