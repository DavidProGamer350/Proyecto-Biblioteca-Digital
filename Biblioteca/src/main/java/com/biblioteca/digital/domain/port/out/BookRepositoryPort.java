package com.biblioteca.digital.domain.port.out;

import com.biblioteca.digital.domain.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book);
    List<Book> findAll();
    Book findByIsbn(String isbn);
    Book update(Long id, Book book);
    void deleteById(Long id);
    Book findById(Long id);

}
