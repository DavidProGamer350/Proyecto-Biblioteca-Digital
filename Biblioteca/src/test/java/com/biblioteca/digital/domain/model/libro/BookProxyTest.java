package com.biblioteca.digital.domain.model.libro;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BookProxyTest {

    @TempDir
    Path tempDir;

    private Book book;
    private String basePath;
    private byte[] contenidoOriginal;

    @BeforeEach
    void setUp() throws IOException {
        contenidoOriginal = "Contenido de prueba del libro".getBytes();
        String fileName = "test-book.pdf";
        Path testFile = tempDir.resolve(fileName);
        Files.write(testFile, contenidoOriginal);

        book = new Book(1L, "Test Book", "Test Author", "978-3-16-148410-0",
                BookFormato.PDF, fileName);
        basePath = tempDir.toString();
    }

    @Test
    void testProxyImplementaBookContent() {
        BookProxy proxy = new BookProxy(book, basePath);
        assertTrue(proxy instanceof BookContent);
    }

    @Test
    void testGetMetadataRetornaBook() {
        BookProxy proxy = new BookProxy(book, basePath);
        Book metadata = proxy.getMetadata();

        assertNotNull(metadata);
        assertEquals(book.getId(), metadata.getId());
        assertEquals(book.getTitulo(), metadata.getTitulo());
        assertEquals(book.getAutor(), metadata.getAutor());
    }

    @Test
    void testContenidoNoCargadoInicialmente() {
        BookProxy proxy = new BookProxy(book, basePath);
        assertFalse(proxy.estaCargado());
    }

    @Test
    void testGetContenidoCargaElArchivo() {
        BookProxy proxy = new BookProxy(book, basePath);
        byte[] contenido = proxy.getContenido();

        assertTrue(proxy.estaCargado());
        assertArrayEquals(contenidoOriginal, contenido);
    }

    @Test
    void testDescargarCargaElArchivo() {
        BookProxy proxy = new BookProxy(book, basePath);
        byte[] contenido = proxy.descargar();

        assertTrue(proxy.estaCargado());
        assertArrayEquals(contenidoOriginal, contenido);
    }

    @Test
    void testContenidoEsCached() {
        BookProxy proxy = new BookProxy(book, basePath);
        byte[] primeraLlamada = proxy.getContenido();
        byte[] segundaLlamada = proxy.getContenido();

        assertSame(primeraLlamada, segundaLlamada);
    }

    @Test
    void testGetLibroId() {
        BookProxy proxy = new BookProxy(book, basePath);
        assertEquals(1L, proxy.getLibroId());
    }

    @Test
    void testGetTitulo() {
        BookProxy proxy = new BookProxy(book, basePath);
        assertEquals("Test Book", proxy.getTitulo());
    }

    @Test
    void testThrowsExceptionCuandoArchivoNoExiste() {
        Book bookSinArchivo = new Book(2L, "Sin Archivo", "Autor", "978-0-00-000000-0",
                BookFormato.PDF, "no-existe.pdf");
        BookProxy proxy = new BookProxy(bookSinArchivo, basePath);

        assertThrows(IllegalStateException.class, () -> proxy.getContenido());
    }

    @Test
    void testThrowsExceptionCuandoRutaEsNula() {
        Book bookRutaNula = new Book(3L, "Ruta Nula", "Autor", "978-0-00-000001-0",
                BookFormato.EPUB, null);
        BookProxy proxy = new BookProxy(bookRutaNula, basePath);

        assertThrows(IllegalStateException.class, () -> proxy.getContenido());
    }

    @Test
    void testThrowsExceptionCuandoRutaEstaVacia() {
        Book bookRutaVacia = new Book(4L, "Ruta Vacía", "Autor", "978-0-00-000002-0",
                BookFormato.MOBI, "");
        BookProxy proxy = new BookProxy(bookRutaVacia, basePath);

        assertThrows(IllegalStateException.class, () -> proxy.getContenido());
    }
}