package com.biblioteca.digital.domain.model.reporte.compuesto;

import com.biblioteca.digital.domain.model.reporte.Reporte;
import com.biblioteca.digital.domain.model.reporte.ReporteBase;

import java.util.ArrayList;
import java.util.List;

public class ReporteCompuesto extends ReporteBase {
    private List<Reporte> hijos = new ArrayList<>();
    
    public ReporteCompuesto(String titulo) {
        super(titulo);
    }
    
    public ReporteCompuesto(String titulo, List<Reporte> hijos) {
        super(titulo);
        this.hijos = hijos;
    }
    
    public void agregarHijo(Reporte reporte) {
        hijos.add(reporte);
    }
    
    public void eliminarHijo(Reporte reporte) {
        hijos.remove(reporte);
    }
    
    public List<Reporte> getHijos() {
        return hijos;
    }
    
    @Override
    public boolean isCompuesto() {
        return true;
    }
    
    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  ").append(getTitulo().toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════════════\n\n");
        
        for (Reporte hijo : hijos) {
            sb.append(hijo.generar());
            sb.append("\n───────────────────────────────────────────────\n\n");
        }
        
        return sb.toString();
    }
}