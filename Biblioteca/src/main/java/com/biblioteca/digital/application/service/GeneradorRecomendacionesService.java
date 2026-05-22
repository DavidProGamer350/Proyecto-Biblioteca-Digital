package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Book;
import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.Recomendacion;
import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.BookUseCase;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import com.biblioteca.digital.domain.port.in.RecomendacionUseCase;
import com.biblioteca.digital.domain.port.in.UserUseCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GeneradorRecomendacionesService {

    private final PrestamoUseCase prestamoUseCase;
    private final BookUseCase bookUseCase;
    private final RecomendacionUseCase recomendacionUseCase;
    private final UserUseCase userUseCase;

    public GeneradorRecomendacionesService(PrestamoUseCase prestamoUseCase, BookUseCase bookUseCase,
            RecomendacionUseCase recomendacionUseCase, UserUseCase userUseCase) {
        this.prestamoUseCase = prestamoUseCase;
        this.bookUseCase = bookUseCase;
        this.recomendacionUseCase = recomendacionUseCase;
        this.userUseCase = userUseCase;
    }

    public List<Recomendacion> generarRecomendaciones(Long usuarioId) {
        User user = userUseCase.getUserById(usuarioId);
        boolean isPremium = user != null && user.isSuscripcionActiva();

        List<Prestamo> allLoans = prestamoUseCase.listarPrestamos();
        List<Book> allBooks = bookUseCase.findAllBooks();

        Set<Long> borrowedBookIds = allLoans.stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId))
                .map(Prestamo::getLibroId)
                .collect(Collectors.toSet());

        Set<String> userAuthors = allBooks.stream()
                .filter(b -> borrowedBookIds.contains(b.getId()))
                .map(Book::getAutor)
                .collect(Collectors.toSet());

        Map<BookFormato, Long> formatCount = allBooks.stream()
                .filter(b -> borrowedBookIds.contains(b.getId()))
                .collect(Collectors.groupingBy(Book::getFormato, Collectors.counting()));

        BookFormato preferredFormat = formatCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<Long, Long> bookLoanCount = new HashMap<>();
        for (Prestamo p : allLoans) {
            if (!p.getUsuarioId().equals(usuarioId)) {
                bookLoanCount.merge(p.getLibroId(), 1L, Long::sum);
            }
        }

        Set<Long> popularBookIds = bookLoanCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<Book> availableBooks = allBooks.stream()
                .filter(b -> !borrowedBookIds.contains(b.getId()))
                .collect(Collectors.toList());

        List<ScoredBook> scored = new ArrayList<>();
        for (Book book : availableBooks) {
            int score = 0;
            List<String> reasons = new ArrayList<>();

            if (userAuthors.contains(book.getAutor())) {
                score += 5;
                reasons.add("Mismo autor que leíste antes");
            }

            if (popularBookIds.contains(book.getId())) {
                score += 3;
                reasons.add("Popular entre otros lectores");
            }

            if (preferredFormat != null && book.getFormato() == preferredFormat) {
                score += 2;
                reasons.add("Formato que prefieres");
            }

            if (isPremium) {
                score += 1;
            }

            if (score > 0) {
                scored.add(new ScoredBook(book, score, reasons));
            }
        }

        scored.sort((a, b) -> b.score - a.score);
        List<ScoredBook> top = scored.subList(0, Math.min(10, scored.size()));

        // Desactivar recomendaciones anteriores
        List<Recomendacion> anteriores = recomendacionUseCase.getRecomendacionesByUsuarioId(usuarioId);
        for (Recomendacion r : anteriores) {
            r.setActiva(false);
            recomendacionUseCase.updateRecomendacion(r.getId(), r);
        }

        List<Recomendacion> result = new ArrayList<>();
        for (ScoredBook sb : top) {
            String tipo = isPremium ? "premium" : "historial";
            Recomendacion rec = recomendacionUseCase.generarRecomendacion(tipo, usuarioId, sb.book.getId());
            rec.setMotivo(String.join(", ", sb.reasons));
            rec.setPrioridad(sb.score >= 7 ? "alta" : sb.score >= 4 ? "media" : "baja");
            rec = recomendacionUseCase.createRecomendacion(rec);
            result.add(rec);
        }

        return result;
    }

    private static class ScoredBook {
        final Book book;
        final int score;
        final List<String> reasons;

        ScoredBook(Book book, int score, List<String> reasons) {
            this.book = book;
            this.score = score;
            this.reasons = reasons;
        }
    }
}
