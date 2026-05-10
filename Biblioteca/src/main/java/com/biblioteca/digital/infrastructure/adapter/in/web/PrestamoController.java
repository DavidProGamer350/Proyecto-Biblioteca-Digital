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
    public ResponseEntity<Prestamo> actualizarPrestamo(@PathVariable Long id, @RequestBody Map<String, Object> json) {
        Prestamo existing = prestamoUseCase.buscarPrestamo(id);
        if (existing == null) return ResponseEntity.notFound().build();

        Prestamo.Builder builder = Prestamo.builder()
                .id(existing.getId())
                .usuarioId(existing.getUsuarioId())
                .libroId(existing.getLibroId())
                .fechaPrestamo(existing.getFechaPrestamo())
                .fechaDevolucionEsperada(
                    json.containsKey("fechaDevolucionEsperada") && json.get("fechaDevolucionEsperada") != null
                        ? java.time.LocalDate.parse((String) json.get("fechaDevolucionEsperada"))
                        : existing.getFechaDevolucionEsperada())
                .fechaDevolucionReal(existing.getFechaDevolucionReal())
                .estado(
                    json.containsKey("estado") && json.get("estado") != null
                        ? (String) json.get("estado")
                        : existing.getEstado())
                .observaciones(
                    json.containsKey("observaciones") && json.get("observaciones") != null
                        ? (String) json.get("observaciones")
                        : existing.getObservaciones())
                .multasAcumuladas(
                    json.containsKey("multasAcumuladas") && json.get("multasAcumuladas") != null
                        ? ((Number) json.get("multasAcumuladas")).intValue()
                        : existing.getMultasAcumuladas())
                .motivoRechazo(existing.getMotivoRechazo())
                .vecesRenovado(existing.getVecesRenovado())
                .notificacionEmail(existing.getNotificacionEmail())
                .codigoPrestamo(existing.getCodigoPrestamo())
                .requiereAprobacion(existing.getRequiereAprobacion());

        Prestamo updated = builder.build();
        return ResponseEntity.ok(prestamoUseCase.actualizarPrestamo(id, updated));
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<Prestamo> devolverPrestamo(@PathVariable Long id) {
        Prestamo prestamo = prestamoUseCase.devolverPrestamo(id);
        return prestamo != null ?
                ResponseEntity.ok(prestamo) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoUseCase.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}