package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.application.service.GeneradorRecomendacionesService;
import com.biblioteca.digital.domain.model.Recomendacion;
import com.biblioteca.digital.domain.port.in.RecomendacionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recomendaciones")
public class RecomendacionesController {

    private final RecomendacionUseCase recomendacionUseCase;
    private final GeneradorRecomendacionesService generadorRecomendacionesService;

    public RecomendacionesController(RecomendacionUseCase recomendacionUseCase,
            GeneradorRecomendacionesService generadorRecomendacionesService) {
        this.recomendacionUseCase = recomendacionUseCase;
        this.generadorRecomendacionesService = generadorRecomendacionesService;
    }

    @PostMapping
    public ResponseEntity<Recomendacion> createRecomendacion(@RequestBody Recomendacion recomendacion) {
        Recomendacion created = recomendacionUseCase.createRecomendacion(recomendacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Recomendacion>> getAllRecomendaciones() {
        return ResponseEntity.ok(recomendacionUseCase.getAllRecomendaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recomendacion> getRecomendacionById(@PathVariable Long id) {
        Recomendacion rec = recomendacionUseCase.getRecomendacionById(id);
        return rec != null ? ResponseEntity.ok(rec) : ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Recomendacion>> getRecomendacionesByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(recomendacionUseCase.getRecomendacionesByUsuarioId(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/libro/{libroId}")
    public ResponseEntity<Recomendacion> getRecomendacionByUsuarioAndLibro(
            @PathVariable Long usuarioId, @PathVariable Long libroId) {
        Recomendacion rec = recomendacionUseCase.getRecomendacionByUsuarioAndLibro(usuarioId, libroId);
        return rec != null ? ResponseEntity.ok(rec) : ResponseEntity.notFound().build();
    }

    @PostMapping("/generar/{usuarioId}")
    public ResponseEntity<List<Recomendacion>> generarRecomendaciones(@PathVariable Long usuarioId) {
        List<Recomendacion> generadas = generadorRecomendacionesService.generarRecomendaciones(usuarioId);
        return ResponseEntity.ok(generadas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recomendacion> updateRecomendacion(@PathVariable Long id, @RequestBody Recomendacion recomendacion) {
        Recomendacion updated = recomendacionUseCase.updateRecomendacion(id, recomendacion);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecomendacion(@PathVariable Long id) {
        recomendacionUseCase.deleteRecomendacion(id);
        return ResponseEntity.noContent().build();
    }
}
