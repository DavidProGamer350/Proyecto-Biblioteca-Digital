package com.biblioteca.digital.infrastructure.config;

import com.biblioteca.digital.application.service.UserService;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import com.biblioteca.digital.domain.port.out.UserRepositoryPort;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.JpaUserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort) {
        return new UserService(userRepositoryPort);
    }

}
