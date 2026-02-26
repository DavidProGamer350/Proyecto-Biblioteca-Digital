package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.FileUploaderFactory;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

	private final BookUseCase bookUseCase;

	public BookController(BookUseCase bookUseCase) {
		this.bookUseCase = bookUseCase;
	}

	@PostMapping(value = "/upload")
	public ResponseEntity<Book> createBook(@RequestPart("book") String bookJson,
			@RequestPart("file") MultipartFile file) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Book book = mapper.readValue(bookJson, Book.class);

			// ⭐ NUEVA VALIDACIÓN
			if (!book.getFormato().equals(detectFileType(file.getBytes()))) {
				throw new IllegalArgumentException(
						"❌ Formato JSON (" + book.getFormato() + ") no coincide con archivo detectado");
			}

			FileUploader uploader = FileUploaderFactory.getUploader(book.getFormato());
			String path = uploader.upload(book.getIsbn(), file.getBytes());
			book.setArchivoPath(path);

			Book created = bookUseCase.createBook(book);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
		}
	}

	// ⭐ MÉTODO HELPER
	private BookFormato detectFileType(byte[] bytes) {
		if (bytes.length < 8)
			throw new IllegalArgumentException("Archivo muy pequeño");

		String header8 = new String(bytes, 0, 8);

		if (header8.startsWith("%PDF-"))
			return BookFormato.PDF;

		// EPUB: busca mimetype en todo archivo
		String full = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
		if (full.contains("application/epub+zip"))
			return BookFormato.EPUB;

		   
	    // MOBI: busca "MOBI" en primeros 100 bytes (flexible)
	    if (bytes.length >= 68) {
	        String first100 = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
	        if (first100.contains("MOBI") || first100.contains("TEXT")) {
	            return BookFormato.MOBI;
	        }
	    }
		throw new IllegalArgumentException("Tipo no soportado: " + header8);
	}

	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks() {
		return ResponseEntity.ok(bookUseCase.findAllBooks());
	}

	@GetMapping("/isbn/{isbn}")
	public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
		Book book = bookUseCase.findBookByIsbn(isbn);
		return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
		Book updated = bookUseCase.updateBook(id, book);
		return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		bookUseCase.deleteBook(id);
		return ResponseEntity.noContent().build();
	}

}
