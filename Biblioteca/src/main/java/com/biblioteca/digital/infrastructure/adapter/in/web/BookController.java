package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.service.factory.FileUploaderFactory;
import com.biblioteca.digital.domain.service.factory.creators.FileUploaderCreator;

import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

	private final BookUseCase bookUseCase;
	private final FileUploaderFactory fileUploaderFactory; // ⭐ Factory inyectada

	public BookController(BookUseCase bookUseCase, FileUploaderFactory fileUploaderFactory) {
		this.bookUseCase = bookUseCase;
		this.fileUploaderFactory = fileUploaderFactory; // ⭐ DIP
	}

	@PostMapping(value = "/upload")
	public ResponseEntity<Book> createBook(@RequestPart("book") String bookJson,
			@RequestPart("file") MultipartFile file) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Book book = mapper.readValue(bookJson, Book.class);

			// ⭐ VALIDACIÓN FORMATO (mantiene seguridad)
			if (!book.getFormato().equals(detectFileType(file.getBytes()))) {
				throw new IllegalArgumentException(
						"❌ Formato JSON (" + book.getFormato() + ") no coincide con archivo detectado");
			}

			// ⭐ FACTORY METHOD: Creator maneja creación + Template Method upload
			FileUploaderCreator creator = fileUploaderFactory.getCreator(book.getFormato());
			String path = creator.upload(book.getIsbn(), file.getBytes()); // Template Method

			book.setArchivoPath(path);
			Book created = bookUseCase.createBook(book);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
		}
	}

	// ⭐ Detecta tipo (igual que tenías)
	private BookFormato detectFileType(byte[] bytes) {
		if (bytes.length < 8)
			throw new IllegalArgumentException("Archivo muy pequeño");

		String header8 = new String(bytes, 0, 8);

		if (header8.startsWith("%PDF-"))
			return BookFormato.PDF;

		if (header8.startsWith("PK") && bytes.length >= 60) {
			String mimetype = new String(bytes, 32, Math.min(25, bytes.length - 32), StandardCharsets.UTF_8)
					.toLowerCase();
			if (mimetype.contains("epub"))
				return BookFormato.EPUB;
		}

		// MOBI flexible
		String first200 = new String(bytes, 0, Math.min(200, bytes.length), StandardCharsets.UTF_8).toLowerCase();
		if (first200.contains("MOBI") || first200.contains("TEXT"))
			return BookFormato.MOBI;

		throw new IllegalArgumentException("Tipo no soportado: " + header8);
	}

	// Resto endpoints igual...
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
