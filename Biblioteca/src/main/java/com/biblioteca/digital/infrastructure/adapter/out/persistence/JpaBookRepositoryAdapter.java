package com.biblioteca.digital.infrastructure.adapter.out.persistence;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.port.out.BookRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaBookRepositoryAdapter implements BookRepositoryPort {

    private final SpringDataBookRepository repository;

    public JpaBookRepositoryAdapter(SpringDataBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = new BookEntity();
        mapToEntity(book, entity);
        BookEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
            .map(this::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Book findByIsbn(String isbn) {
        return repository.findByIsbn(isbn)
            .map(this::mapToDomain)
            .orElse(null);
    }

    @Override
    public Book update(Long id, Book book) {
        Optional<BookEntity> optionalEntity = repository.findById(id);
        if (optionalEntity.isEmpty()) return null;
        
        BookEntity entity = optionalEntity.get();
        mapToEntity(book, entity);
        entity.setId(id);
        
        BookEntity updated = repository.save(entity);
        return mapToDomain(updated);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void mapToEntity(Book book, BookEntity entity) {
        entity.setTitulo(book.getTitulo());
        entity.setAutor(book.getAutor());
        entity.setIsbn(book.getIsbn());
        entity.setFormato(book.getFormato().name());
        entity.setArchivoPath(book.getArchivoPath());
    }

    private Book mapToDomain(BookEntity entity) {
        return new Book(
            entity.getId(),
            entity.getTitulo(),
            entity.getAutor(),
            entity.getIsbn(),
            BookFormato.valueOf(entity.getFormato()),
            entity.getArchivoPath()
        );
    }
}


