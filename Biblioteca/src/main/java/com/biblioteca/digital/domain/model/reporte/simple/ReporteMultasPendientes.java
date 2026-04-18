package com.biblioteca.digital.domain.model.reporte.simple;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.reporte.ReporteBase;

import java.util.List;

public class ReporteMultasPendientes extends ReporteBase {
    private final List<Prestamo> prestamos;
    
    public ReporteMultasPendientes(List<Prestamo> prestamos) {
        super("Reporte de Multas Pendientes");
        this.prestamos = prestamos;
    }
    
    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  MULTAS PENDIENTES\n");
        sb.append("═══════════════════════════════════════════════\n");
        
        if (prestamos.isEmpty()) {
            sb.append("  No hay multas pendientes actualmente.\n");
        } else {
            int totalMultas = prestamos.stream()
                .mapToInt(p -> p.getMultasAcumuladas() != null ? p.getMultasAcumuladas() : 0)
                .sum();
            
            sb.append(String.format("  Total: %d préstamos con multas\n", prestamos.size()));
            sb.append(String.format("  Monto total acumulado: $%d\n\n", totalMultas));
            
            for (Prestamo p : prestamos) {
                sb.append(String.format("  ID Préstamo: %d | Usuario: %d\n",
                    p.getId(), p.getUsuarioId()));
                sb.append(String.format("  Multa: $%d\n", p.getMultasAcumuladas() != null ? p.getMultasAcumuladas() : 0));
                
                if ("DEVUELTO".equals(p.getEstado())) {
                    sb.append("  Estado: DEVUELTO (multa por pagar)\n");
                } else if ("ACTIVO".equals(p.getEstado())) {
                    sb.append("  Estado: ACTIVO (pendiente de devolución)\n");
                }
                sb.append("\n");
            }
            
            sb.append("───────────────────────────────────────────────\n");
            sb.append(String.format("  TOTAL A COBRAR: $%d\n", totalMultas));
        }
        
        return sb.toString();
    }
}