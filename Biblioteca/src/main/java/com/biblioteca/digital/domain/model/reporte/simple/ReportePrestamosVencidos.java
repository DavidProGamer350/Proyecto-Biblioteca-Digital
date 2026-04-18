package com.biblioteca.digital.domain.model.reporte.simple;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.reporte.ReporteBase;

import java.util.List;

public class ReportePrestamosVencidos extends ReporteBase {
    private final List<Prestamo> prestamos;
    
    public ReportePrestamosVencidos(List<Prestamo> prestamos) {
        super("Reporte de Préstamos Vencidos");
        this.prestamos = prestamos;
    }
    
    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  PRÉSTAMOS VENCIDOS\n");
        sb.append("═══════════════════════════════════════════════\n");
        
        if (prestamos.isEmpty()) {
            sb.append("  No hay préstamos vencidos actualmente.\n");
        } else {
            sb.append(String.format("  Total: %d préstamos vencidos\n\n", prestamos.size()));
            
            for (Prestamo p : prestamos) {
                sb.append(String.format("  ID: %d | Usuario: %d | Libro: %d\n",
                    p.getId(), p.getUsuarioId(), p.getLibroId()));
                sb.append(String.format("  Fecha Préstamo: %s | Venció: %s\n",
                    p.getFechaPrestamo(), p.getFechaDevolucionEsperada()));
                
                long diasVencido = java.time.temporal.ChronoUnit.DAYS.between(
                    p.getFechaDevolucionEsperada(), 
                    java.time.LocalDate.now()
                );
                sb.append(String.format("  Días de retraso: %d\n", diasVencido));
                
                if (p.getMultasAcumuladas() != null && p.getMultasAcumuladas() > 0) {
                    sb.append(String.format("  Multa acumulada: $%d\n", p.getMultasAcumuladas()));
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}