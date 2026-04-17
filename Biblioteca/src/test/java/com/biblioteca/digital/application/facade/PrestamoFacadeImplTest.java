package com.biblioteca.digital.application.facade;

import com.biblioteca.digital.application.service.PrestamoService;
import com.biblioteca.digital.application.service.SubscriptionService;
import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoFacadeImplTest {

    @Mock
    private PrestamoService prestamoService;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private PrestamoFacadeImpl prestamoFacade;

    private Map<String, Object> request;

    @BeforeEach
    void setUp() {
        request = new HashMap<>();
        request.put("usuarioId", 1L);
        request.put("libroId", 10L);
        request.put("observaciones", "Test prestamo");
    }

    @Test
    void crearPrestamoCompleto_exito_debeCrearPrestamoYNotificar() {
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(false);
        when(prestamoService.crearPrestamo(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo p = invocation.getArgument(0);
            return Prestamo.builder()
                .id(1L)
                .usuarioId(p.getUsuarioId())
                .libroId(p.getLibroId())
                .fechaPrestamo(p.getFechaPrestamo())
                .fechaDevolucionEsperada(p.getFechaDevolucionEsperada())
                .estado(p.getEstado())
                .build();
        });

        Prestamo result = prestamoFacade.crearPrestamoCompleto(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVO", result.getEstado());
        verify(prestamoService).crearPrestamo(any(Prestamo.class));
        verify(notificacionService).notificarUsuario(eq("1"), anyString(), eq(false));
    }

    @Test
    void crearPrestamoCompleto_usuarioPremium_debeNotificarPorMultiplesCanales() {
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(true);
        when(prestamoService.crearPrestamo(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo p = invocation.getArgument(0);
            return Prestamo.builder()
                .id(2L)
                .usuarioId(p.getUsuarioId())
                .libroId(p.getLibroId())
                .build();
        });

        prestamoFacade.crearPrestamoCompleto(request);

        verify(notificacionService).notificarUsuario(eq("1"), anyString(), eq(true));
    }

    @Test
    void devolverPrestamo_exitoSinMulta_debeRetornarEstadoDEVUELTO() {
        Prestamo prestamo = Prestamo.builder()
            .id(1L)
            .usuarioId(1L)
            .libroId(10L)
            .fechaPrestamo(LocalDate.now().minusDays(10))
            .fechaDevolucionEsperada(LocalDate.now().plusDays(4))
            .estado("ACTIVO")
            .build();

        when(prestamoService.buscarPrestamo(1L)).thenReturn(prestamo);
        when(prestamoService.actualizarPrestamo(eq(1L), any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(1));
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(false);

        Prestamo result = prestamoFacade.devolverPrestamo(1L);

        assertNotNull(result);
        assertEquals("DEVUELTO", result.getEstado());
        assertNotNull(result.getFechaDevolucionReal());
        assertEquals(0, result.getMultasAcumuladas());
    }

    @Test
    void devolverPrestamo_exitoConMulta_debeCalcularMultaCorrecta() {
        Prestamo prestamo = Prestamo.builder()
            .id(1L)
            .usuarioId(1L)
            .libroId(10L)
            .fechaPrestamo(LocalDate.now().minusDays(20))
            .fechaDevolucionEsperada(LocalDate.now().minusDays(5))
            .estado("ACTIVO")
            .build();

        when(prestamoService.buscarPrestamo(1L)).thenReturn(prestamo);
        when(prestamoService.actualizarPrestamo(eq(1L), any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(1));
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(false);

        Prestamo result = prestamoFacade.devolverPrestamo(1L);

        assertNotNull(result);
        assertEquals("DEVUELTO", result.getEstado());
        assertTrue(result.getMultasAcumuladas() > 0);
    }

    @Test
    void devolverPrestamo_noEncontrado_debeLanzarExcepcion() {
        when(prestamoService.buscarPrestamo(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> prestamoFacade.devolverPrestamo(999L)
        );

        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    @Test
    void renovarPrestamo_exito_debeIncrementarFecha() {
        LocalDate fechaOriginal = LocalDate.now().plusDays(14);
        Prestamo prestamo = Prestamo.builder()
            .id(1L)
            .usuarioId(1L)
            .libroId(10L)
            .fechaPrestamo(LocalDate.now())
            .fechaDevolucionEsperada(fechaOriginal)
            .estado("ACTIVO")
            .vecesRenovado(0)
            .build();

        when(prestamoService.buscarPrestamo(1L)).thenReturn(prestamo);
        when(prestamoService.actualizarPrestamo(eq(1L), any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(1));
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(false);

        Prestamo result = prestamoFacade.renovarPrestamo(1L, 7);

        assertNotNull(result);
        assertEquals(fechaOriginal.plusDays(7), result.getFechaDevolucionEsperada());
        assertEquals(1, result.getVecesRenovado());
    }

    @Test
    void renovarPrestamo_limiteExcedido_debeLanzarExcepcion() {
        Prestamo prestamo = Prestamo.builder()
            .id(1L)
            .usuarioId(1L)
            .libroId(10L)
            .fechaDevolucionEsperada(LocalDate.now().plusDays(14))
            .estado("ACTIVO")
            .vecesRenovado(2)
            .build();

        when(prestamoService.buscarPrestamo(1L)).thenReturn(prestamo);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> prestamoFacade.renovarPrestamo(1L, 7)
        );

        assertTrue(exception.getMessage().contains("Límite"));
    }

    @Test
    void renovarPrestamo_primeraRenovacion_debeEstablecerVecesRenovadoEn1() {
        Prestamo prestamo = Prestamo.builder()
            .id(1L)
            .usuarioId(1L)
            .libroId(10L)
            .fechaDevolucionEsperada(LocalDate.now().plusDays(14))
            .estado("ACTIVO")
            .vecesRenovado(null)
            .build();

        when(prestamoService.buscarPrestamo(1L)).thenReturn(prestamo);
        when(prestamoService.actualizarPrestamo(eq(1L), any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(1));
        when(subscriptionService.verificarSuscripcionPremium(any(User.class))).thenReturn(false);

        Prestamo result = prestamoFacade.renovarPrestamo(1L, 7);

        assertEquals(1, result.getVecesRenovado());
    }
}