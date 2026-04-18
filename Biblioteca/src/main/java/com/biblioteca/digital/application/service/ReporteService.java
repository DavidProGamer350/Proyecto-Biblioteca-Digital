package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.reporte.Reporte;
import com.biblioteca.digital.domain.model.reporte.compuesto.ReporteBibliotecaCompleto;
import com.biblioteca.digital.domain.model.reporte.simple.ReporteMultasPendientes;
import com.biblioteca.digital.domain.model.reporte.simple.ReportePrestamosActivos;
import com.biblioteca.digital.domain.model.reporte.simple.ReportePrestamosVencidos;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class ReporteService {
    
    private final PrestamoUseCase prestamoUseCase;
    
    public ReporteService(PrestamoUseCase prestamoUseCase) {
        this.prestamoUseCase = prestamoUseCase;
    }
    
    public Reporte generarReporteActivos() {
        List<Prestamo> prestamos = prestamoUseCase.listarPrestamos();
        List<Prestamo> activos = prestamos.stream()
            .filter(p -> "ACTIVO".equals(p.getEstado()))
            .toList();
        return new ReportePrestamosActivos(activos);
    }
    
    public Reporte generarReporteVencidos() {
        List<Prestamo> prestamos = prestamoUseCase.listarPrestamos();
        LocalDate hoy = LocalDate.now();
        List<Prestamo> vencidos = prestamos.stream()
            .filter(p -> "ACTIVO".equals(p.getEstado()))
            .filter(p -> p.getFechaDevolucionEsperada() != null && 
                        p.getFechaDevolucionEsperada().isBefore(hoy))
            .toList();
        return new ReportePrestamosVencidos(vencidos);
    }
    
    public Reporte generarReporteMultas() {
        List<Prestamo> prestamos = prestamoUseCase.listarPrestamos();
        List<Prestamo> conMultas = prestamos.stream()
            .filter(p -> p.getMultasAcumuladas() != null && p.getMultasAcumuladas() > 0)
            .toList();
        return new ReporteMultasPendientes(conMultas);
    }
    
    public Reporte generarReporteCompleto() {
        return new ReporteBibliotecaCompleto(Arrays.asList(
            generarReporteActivos(),
            generarReporteVencidos(),
            generarReporteMultas()
        ));
    }
}