package com.biblioteca.digital.infrastructure.config;

import com.biblioteca.digital.application.service.BookService;
import com.biblioteca.digital.application.service.PrestamoService;
import com.biblioteca.digital.application.service.UserService;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.BookRepositoryPort;
import com.biblioteca.digital.domain.port.out.PrestamoRepositoryPort;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;  // ✅ IMPORT CORRECTO

@Configuration
public class BeanConfiguration {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort) {
        return new UserService(userRepositoryPort);
    }

    @Bean
    public BookUseCase bookUseCase(BookRepositoryPort bookRepositoryPort) {
        return new BookService(bookRepositoryPort);
    }

    @Bean
    public FileUploaderFactory fileUploaderFactory(List<FileUploaderCreator> creators) {
        return new FileUploaderFactory(creators);
    }
}