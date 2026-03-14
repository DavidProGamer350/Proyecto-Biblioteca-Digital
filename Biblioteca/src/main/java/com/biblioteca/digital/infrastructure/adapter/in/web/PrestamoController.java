package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoUseCase prestamoUseCase;

    public PrestamoController(PrestamoUseCase prestamoUseCase) {
        this.prestamoUseCase = prestamoUseCase;
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Map<String, Object> json) {
        // ⭐ BUILDER DEMOSTRADO: JSON → Builder fluente → Domain
        Prestamo prestamo = Prestamo.builder()
            .usuarioId(((Number) json.get("usuarioId")).longValue())
            .libroId(((Number) json.get("libroId")).longValue())
            .fechaPrestamo(LocalDate.now())
            .fechaDevolucionEsperada(LocalDate.now().plusDays(14))
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
        
        // ⭐ Builder MERGE: datos existentes + nuevos
        Prestamo updated = Prestamo.builder()
            .id(existing.getId())
            .usuarioId(existing.getUsuarioId())
            .libroId(existing.getLibroId())
            .fechaPrestamo(existing.getFechaPrestamo())
            .fechaDevolucionEsperada(existing.getFechaDevolucionEsperada())
            .fechaDevolucionReal(updateData.getFechaDevolucionReal())  // ← Nuevo!
            .estado(updateData.getEstado())                           // ← Nuevo!
            .observaciones(existing.getObservaciones())
            .multasAcumuladas(updateData.getMultasAcumuladas())       // ← Nuevo!
            .motivoRechazo(updateData.getMotivoRechazo())             // ← Nuevo!
            .build();
        
        return ResponseEntity.ok(prestamoUseCase.actualizarPrestamo(id, updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoUseCase.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}
