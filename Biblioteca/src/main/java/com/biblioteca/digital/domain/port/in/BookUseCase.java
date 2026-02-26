package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.Book;
import java.util.List;

public interface BookUseCase {
	Book createBook(Book book);

	List<Book> findAllBooks();

	Book findBookByIsbn(String isbn);
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
