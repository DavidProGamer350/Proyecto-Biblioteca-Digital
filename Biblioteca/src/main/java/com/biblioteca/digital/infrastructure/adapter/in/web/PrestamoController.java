package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoFacade;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoFacade prestamoFacade;
    private final PrestamoUseCase prestamoUseCase;

    public PrestamoController(PrestamoFacade prestamoFacade, PrestamoUseCase prestamoUseCase) {
        this.prestamoFacade = prestamoFacade;
        this.prestamoUseCase = prestamoUseCase;
    }

    @PostMapping("/completo")
    public ResponseEntity<Prestamo> crearPrestamoCompleto(@RequestBody Map<String, Object> json) {
        Prestamo prestamo = prestamoFacade.crearPrestamoCompleto(json);
        return ResponseEntity.ok(prestamo);
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Map<String, Object> json) {
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

    @PostMapping("/{id}/devolver")
    public ResponseEntity<Prestamo> devolverPrestamo(@PathVariable Long id) {
        Prestamo prestamo = prestamoFacade.devolverPrestamo(id);
        return ResponseEntity.ok(prestamo);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Prestamo> renovarPrestamo(@PathVariable Long id, @RequestParam int dias) {
        Prestamo prestamo = prestamoFacade.renovarPrestamo(id, dias);
        return ResponseEntity.ok(prestamo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoUseCase.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}