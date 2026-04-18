package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.reporte.Reporte;
import com.biblioteca.digital.domain.model.reporte.compuesto.ReporteBibliotecaCompleto;
import com.biblioteca.digital.domain.model.reporte.compuesto.ReporteCompuesto;
import com.biblioteca.digital.domain.port.in.PrestamoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private PrestamoUseCase prestamoUseCase;

    @InjectMocks
    private ReporteService reporteService;

    private List<Prestamo> prestamosActivos;
    private List<Prestamo> prestamosVencidos;
    private List<Prestamo> prestamosConMultas;

    @BeforeEach
    void setUp() {
        prestamosActivos = Arrays.asList(
            Prestamo.builder()
                .id(1L).usuarioId(1L).libroId(10L)
                .fechaPrestamo(LocalDate.now().minusDays(5))
                .fechaDevolucionEsperada(LocalDate.now().plusDays(9))
                .estado("ACTIVO")
                .observaciones("Préstamo test 1")
                .build(),
            Prestamo.builder()
                .id(2L).usuarioId(2L).libroId(20L)
                .fechaPrestamo(LocalDate.now().minusDays(3))
                .fechaDevolucionEsperada(LocalDate.now().plusDays(11))
                .estado("ACTIVO")
                .observaciones("Préstamo test 2")
                .build()
        );

        prestamosVencidos = Arrays.asList(
            Prestamo.builder()
                .id(3L).usuarioId(3L).libroId(30L)
                .fechaPrestamo(LocalDate.now().minusDays(20))
                .fechaDevolucionEsperada(LocalDate.now().minusDays(5))
                .estado("ACTIVO")
                .multasAcumuladas(2500)
                .build()
        );

        prestamosConMultas = Arrays.asList(
            Prestamo.builder()
                .id(4L).usuarioId(4L).libroId(40L)
                .estado("DEVUELTO")
                .multasAcumuladas(1500)
                .build(),
            Prestamo.builder()
                .id(5L).usuarioId(5L).libroId(50L)
                .estado("ACTIVO")
                .multasAcumuladas(3000)
                .build()
        );
    }

    @Test
    void generarReporteActivos_vacio_debeRetornarMensajeApropiado() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteActivos();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("No hay préstamos activos"));
    }

    @Test
    void generarReporteActivos_conDatos_debeTenerFormatoCorrecto() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(prestamosActivos);

        Reporte reporte = reporteService.generarReporteActivos();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("PRÉSTAMOS ACTIVOS"));
        assertTrue(resultado.contains("Total: 2 préstamos activos"));
        assertTrue(resultado.contains("ID: 1"));
        assertTrue(resultado.contains("ID: 2"));
    }

    @Test
    void generarReporteVencidos_vacio_debeRetornarMensajeApropiado() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteVencidos();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("No hay préstamos vencidos"));
    }

    @Test
    void generarReporteVencidos_conDatos_debeTenerFormatoCorrecto() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(prestamosVencidos);

        Reporte reporte = reporteService.generarReporteVencidos();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("PRÉSTAMOS VENCIDOS"));
        assertTrue(resultado.contains("Total: 1 préstamos vencidos"));
        assertTrue(resultado.contains("Días de retraso"));
        assertTrue(resultado.contains("Multa acumulada"));
    }

    @Test
    void generarReporteMultas_vacio_debeRetornarMensajeApropiado() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteMultas();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("No hay multas pendientes"));
    }

    @Test
    void generarReporteMultas_conDatos_debeTenerFormatoCorrecto() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(prestamosConMultas);

        Reporte reporte = reporteService.generarReporteMultas();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("MULTAS PENDIENTES"));
        assertTrue(resultado.contains("Total: 2 préstamos con multas"));
        assertTrue(resultado.contains("Monto total acumulado"));
        assertTrue(resultado.contains("TOTAL A COBRAR"));
    }

    @Test
    void generarReporteCompleto_sinDatos_debeTenerEstructuraCorrecta() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteCompleto();

        assertTrue(reporte.isCompuesto());
        assertTrue(reporte instanceof ReporteBibliotecaCompleto);
        
        String resultado = reporte.generar();
        assertTrue(resultado.contains("REPORTE BIBLIOTECA COMPLETO"));
        assertTrue(resultado.contains("PRÉSTAMOS ACTIVOS"));
        assertTrue(resultado.contains("PRÉSTAMOS VENCIDOS"));
        assertTrue(resultado.contains("MULTAS PENDIENTES"));
    }

    @Test
    void generarReporteCompleto_conDatos_debeCombinarTodos() {
        List<Prestamo> todos = new java.util.ArrayList<>();
        todos.addAll(prestamosActivos);
        todos.addAll(prestamosVencidos);
        todos.addAll(prestamosConMultas);
        
        when(prestamoUseCase.listarPrestamos()).thenReturn(todos);

        Reporte reporte = reporteService.generarReporteCompleto();

        assertTrue(reporte instanceof ReporteCompuesto);
        
        ReporteCompuesto compuesto = (ReporteCompuesto) reporte;
        assertEquals(3, compuesto.getHijos().size());
        
        String resultado = reporte.generar();
        assertTrue(resultado.contains("PRÉSTAMOS ACTIVOS"));
        assertTrue(resultado.contains("PRÉSTAMOS VENCIDOS"));
        assertTrue(resultado.contains("MULTAS PENDIENTES"));
    }

    @Test
    void reporteCompuesto_tituloCorrecto() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteCompleto();

        assertEquals("Reporte Biblioteca Completo", reporte.getTitulo());
    }

    @Test
    void reporteCompuesto_isCompuesto_true() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteCompleto();

        assertTrue(reporte.isCompuesto());
    }

    @Test
    void reporteSimple_isCompuesto_false() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(Collections.emptyList());

        Reporte reporte = reporteService.generarReporteActivos();

        assertFalse(reporte.isCompuesto());
    }

    @Test
    void generarReporteVencidos_debeCalcularDiasCorrectamente() {
        Prestamo prestamoVencido = Prestamo.builder()
            .id(10L).usuarioId(10L).libroId(100L)
            .fechaPrestamo(LocalDate.now().minusDays(30))
            .fechaDevolucionEsperada(LocalDate.now().minusDays(10))
            .estado("ACTIVO")
            .build();
        
        when(prestamoUseCase.listarPrestamos()).thenReturn(List.of(prestamoVencido));

        Reporte reporte = reporteService.generarReporteVencidos();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("Días de retraso"));
    }

    @Test
    void generarReporteMultas_debeSumarTotalCorrectamente() {
        when(prestamoUseCase.listarPrestamos()).thenReturn(prestamosConMultas);

        Reporte reporte = reporteService.generarReporteMultas();

        String resultado = reporte.generar();
        assertTrue(resultado.contains("$4500"));
    }
}