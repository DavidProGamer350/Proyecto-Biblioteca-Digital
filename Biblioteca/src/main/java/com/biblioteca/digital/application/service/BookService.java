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
    	Book existing = bookRepositoryPort.findById(id);
        
        // ⭐ SI HAY NUEVO ARCHIVO → actualizar path
        if (book.getArchivoPath() != null && !book.getArchivoPath().isEmpty()) {
            existing.setArchivoPath(book.getArchivoPath());
        }
        
        // Actualizar otros datos
        existing.setTitulo(book.getTitulo());
        existing.setAutor(book.getAutor());
        existing.setIsbn(book.getIsbn());
        existing.setFormato(book.getFormato());
        
        return bookRepositoryPort.save(existing);
    }


    @Override
    public void deleteBook(Long id) {
        bookRepositoryPort.deleteById(id);
    }
}
