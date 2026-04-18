package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.port.in.PrestamoFacade;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoFacade prestamoFacade;
    private final PrestamoUseCase prestamoUseCase;

    public PrestamoController(PrestamoFacade prestamoFacade, PrestamoUseCase prestamoUseCase) {
        this.prestamoFacade = prestamoFacade;
        this.prestamoUseCase = prestamoUseCase;
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Map<String, Object> json) {
        return ResponseEntity.ok(prestamoFacade.crearPrestamoCompleto(json));
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
        return ResponseEntity.ok(prestamoFacade.devolverPrestamo(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Prestamo> renovarPrestamo(@PathVariable Long id, @RequestParam int dias) {
        return ResponseEntity.ok(prestamoFacade.renovarPrestamo(id, dias));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoUseCase.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}