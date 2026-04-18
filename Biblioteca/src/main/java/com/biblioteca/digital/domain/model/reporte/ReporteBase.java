package com.biblioteca.digital.domain.model.reporte;

public abstract class ReporteBase implements Reporte {
    private String titulo;
    
    public ReporteBase(String titulo) {
        this.titulo = titulo;
    }
    
    @Override
    public String getTitulo() {
        return titulo;
    }
    
    @Override
    public boolean isCompuesto() {
        return false;
    }
}