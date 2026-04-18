package com.biblioteca.digital.infrastructure.adapter.in.web;

import com.biblioteca.digital.application.service.ReporteService;
import com.biblioteca.digital.domain.model.reporte.Reporte;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }
    
    @GetMapping(value = "/prestamos/activos", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> reporteActivos() {
        Reporte reporte = reporteService.generarReporteActivos();
        return ResponseEntity.ok(reporte.generar());
    }
    
    @GetMapping(value = "/prestamos/vencidos", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> reporteVencidos() {
        Reporte reporte = reporteService.generarReporteVencidos();
        return ResponseEntity.ok(reporte.generar());
    }
    
    @GetMapping(value = "/prestamos/multas", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> reporteMultas() {
        Reporte reporte = reporteService.generarReporteMultas();
        return ResponseEntity.ok(reporte.generar());
    }
    
    @GetMapping(value = "/prestamos/completo", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> reporteCompleto() {
        Reporte reporte = reporteService.generarReporteCompleto();
        return ResponseEntity.ok(reporte.generar());
    }
}