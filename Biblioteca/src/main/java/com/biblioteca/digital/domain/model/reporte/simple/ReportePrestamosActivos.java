package com.biblioteca.digital.domain.model.reporte.simple;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.reporte.ReporteBase;

import java.util.List;

public class ReportePrestamosActivos extends ReporteBase {
    private final List<Prestamo> prestamos;
    
    public ReportePrestamosActivos(List<Prestamo> prestamos) {
        super("Reporte de Préstamos Activos");
        this.prestamos = prestamos;
    }
    
    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  PRÉSTAMOS ACTIVOS\n");
        sb.append("═══════════════════════════════════════════════\n");
        
        if (prestamos.isEmpty()) {
            sb.append("  No hay préstamos activos actualmente.\n");
        } else {
            sb.append(String.format("  Total: %d préstamos activos\n\n", prestamos.size()));
            
            for (Prestamo p : prestamos) {
                sb.append(String.format("  ID: %d | Usuario: %d | Libro: %d\n",
                    p.getId(), p.getUsuarioId(), p.getLibroId()));
                sb.append(String.format("  Fecha Préstamo: %s | Devolución: %s\n",
                    p.getFechaPrestamo(), p.getFechaDevolucionEsperada()));
                if (p.getObservaciones() != null && !p.getObservaciones().isEmpty()) {
                    sb.append(String.format("  Obs: %s\n", p.getObservaciones()));
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}