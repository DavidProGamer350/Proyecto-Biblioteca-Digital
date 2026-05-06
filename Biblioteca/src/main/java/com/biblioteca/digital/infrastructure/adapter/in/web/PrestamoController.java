package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import com.biblioteca.digital.infrastructure.config.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoUseCase prestamoUseCase;
    private final JwtUtils jwtUtils;

    public PrestamoController(PrestamoUseCase prestamoUseCase, JwtUtils jwtUtils) {
        this.prestamoUseCase = prestamoUseCase;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Map<String, Object> json) {
        Prestamo prestamo = Prestamo.builder()
                .usuarioId(((Number) json.get("usuarioId")).longValue())
                .libroId(((Number) json.get("libroId")).longValue())
                .fechaPrestamo(java.time.LocalDate.now())
                .fechaDevolucionEsperada(java.time.LocalDate.now().plusDays(14))
                .estado("ACTIVO")
                .observaciones((String) json.get("observaciones"))
                .requiereAprobacion(false)
                .build();

        Prestamo saved = prestamoUseCase.crearPrestamo(prestamo);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Prestamo>> listarPrestamos() {
        return ResponseEntity.ok(prestamoUseCase.listarPrestamos());
    }

    @GetMapping("/mis-prestamos")
    public ResponseEntity<List<Prestamo>> getMyLoans(@RequestHeader("Authorization") String token) {
        Long usuarioId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        List<Prestamo> todos = prestamoUseCase.listarPrestamos();
        List<Prestamo> misPrestamos = todos.stream()
                .filter(p -> usuarioId.equals(p.getUsuarioId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(misPrestamos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPrestamo(@PathVariable Long id) {
        Prestamo prestamo = prestamoUseCase.buscarPrestamo(id);
        return prestamo != null ?
                ResponseEntity.ok(prestamo) :
                ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prestamo> actualizarPrestamo(@PathVariable Long id, @RequestBody Prestamo updateData) {
        Prestamo existing = prestamoUseCase.buscarPrestamo(id);

        Prestamo updated = Prestamo.builder()
                .id(existing.getId())
                .usuarioId(existing.getUsuarioId())
                .libroId(existing.getLibroId())
                .fechaPrestamo(existing.getFechaPrestamo())
                .fechaDevolucionEsperada(existing.getFechaDevolucionEsperada())
                .fechaDevolucionReal(updateData.getFechaDevolucionReal())
                .estado(updateData.getEstado())
                .observaciones(existing.getObservaciones())
                .multasAcumuladas(updateData.getMultasAcumuladas())
                .motivoRechazo(updateData.getMotivoRechazo())
                .build();

        return ResponseEntity.ok(prestamoUseCase.actualizarPrestamo(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoUseCase.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}