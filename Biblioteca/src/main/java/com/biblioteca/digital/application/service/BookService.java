package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.port.out.BookRepositoryPort;
import java.util.List;

public class BookService implements BookUseCase {

    private final BookRepositoryPort bookRepositoryPort;

    public BookService(BookRepositoryPort bookRepositoryPort) {
        this.bookRepositoryPort = bookRepositoryPort;
    }

    @Override
    public Book createBook(Book book) {
        // ⭐ TEMPORAL: path fijo (Factory después)
        book.setArchivoPath("/uploads/" + book.getIsbn() + "." + book.getFormato().name().toLowerCase());
        return bookRepositoryPort.save(book);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepositoryPort.findAll();
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        return bookRepositoryPort.findByIsbn(isbn);
    }
    
    @Override
    public Book updateBook(Long id, Book book) {
        return bookRepositoryPort.update(id, book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepositoryPort.deleteById(id);
    }
}
