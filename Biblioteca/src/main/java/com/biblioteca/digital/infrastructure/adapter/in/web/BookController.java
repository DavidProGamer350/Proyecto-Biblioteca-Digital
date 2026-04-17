package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.upload.factory.UploadAbstractFactory;

import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

	private final BookUseCase bookUseCase;
	private final UploadAbstractFactory uploadFactory;

	public BookController(BookUseCase bookUseCase, UploadAbstractFactory uploadFactory) {
		this.bookUseCase = bookUseCase;
		this.uploadFactory = uploadFactory; // ⭐ DIP
	}

    @PostMapping(value = "/upload")
    public ResponseEntity<Book> createBook(@RequestPart("book") String bookJson,
            @RequestPart("file") MultipartFile file) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Book book = mapper.readValue(bookJson, Book.class);

            byte[] fileBytes = file.getBytes();

            if (!book.getFormato().equals(detectFileType(fileBytes))) {
                throw new IllegalArgumentException("Formato JSON no coincide con archivo detectado");
            }

            FileUploaderCreator creator = uploadFactory.getCreator(book.getFormato());  // ⭐ CAMBIADO

            String path;
            if (book.getFormato() == BookFormato.MOBI) {
                path = "/uploads/" + book.getIsbn() + ".mobi";
            } else {
                path = creator.upload(book.getIsbn(), fileBytes); // PDF/EPUB normal
            }
            
            book.setArchivoPath(path);
            Book created = bookUseCase.createBook(book);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

private BookFormato detectFileType(byte[] bytes) {
		if (bytes.length < 8)
			throw new IllegalArgumentException("Archivo muy pequeño");

		String header8 = new String(bytes, 0, 8, StandardCharsets.UTF_8);

		if (header8.startsWith("%PDF-"))
			return BookFormato.PDF;

		if (header8.startsWith("PK") && bytes.length >= 60) {
			String mimetype = new String(bytes, 30, Math.min(25, bytes.length - 30), StandardCharsets.UTF_8).toLowerCase();
			if (mimetype.contains("epub"))
				return BookFormato.EPUB;
		}

		if (header8.startsWith("BZh") || header8.startsWith("TEXt") || header8.toLowerCase().contains("bibliote"))
			return BookFormato.MOBI;

		String first200 = new String(bytes, 0, Math.min(200, bytes.length), StandardCharsets.UTF_8).toLowerCase();
		if (first200.contains("mobi") || first200.contains("text"))
			return BookFormato.MOBI;

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
