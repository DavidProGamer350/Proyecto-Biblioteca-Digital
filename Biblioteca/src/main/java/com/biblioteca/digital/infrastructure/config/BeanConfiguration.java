package com.biblioteca.digital.infrastructure.config;

import com.biblioteca.digital.application.service.BookService;
import com.biblioteca.digital.application.service.RecomendacionService;
import com.biblioteca.digital.application.service.UserService;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.port.in.RecomendacionUseCase;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.BookRepositoryPort;
import com.biblioteca.digital.domain.port.out.RecomendacionRepositoryPort;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class BeanConfiguration {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return new UserService(userRepositoryPort, passwordEncoder);
    }

    @Bean
    public BookUseCase bookUseCase(BookRepositoryPort bookRepositoryPort) {
        return new BookService(bookRepositoryPort);
    }
    @Bean
    public RecomendacionUseCase recomendacionUseCase(RecomendacionRepositoryPort recomendacionRepositoryPort) {
        return new RecomendacionService(recomendacionRepositoryPort);
    }

    @Bean
    public FileUploaderFactory fileUploaderFactory(List<FileUploaderCreator> creators) {
        return new FileUploaderFactory(creators);
    }
}