package com.biblioteca.digital.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataBookRepository extends JpaRepository<BookEntity, Long> {
    
    Optional<BookEntity> findByIsbn(String isbn);
}
