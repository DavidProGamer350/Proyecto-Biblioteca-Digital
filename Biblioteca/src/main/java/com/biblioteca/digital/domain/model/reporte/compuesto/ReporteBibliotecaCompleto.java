package com.biblioteca.digital.domain.model.reporte.compuesto;

import com.biblioteca.digital.domain.model.reporte.Reporte;
import java.util.List;

public class ReporteBibliotecaCompleto extends ReporteCompuesto {
    
    public ReporteBibliotecaCompleto(List<Reporte> reportes) {
        super("Reporte Biblioteca Completo", reportes);
    }
}