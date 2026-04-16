package com.biblioteca.digital.application.facade;

import com.biblioteca.digital.application.service.PrestamoService;
import com.biblioteca.digital.domain.service.NotificacionService;
import com.biblioteca.digital.application.service.SubscriptionService;
import com.biblioteca.digital.domain.model.Prestamo;
import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.PrestamoFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class PrestamoFacadeImpl implements PrestamoFacade {

    private final PrestamoService prestamoService;
    private final NotificacionService notificacionService;
    private final SubscriptionService subscriptionService;

    public PrestamoFacadeImpl(
            PrestamoService prestamoService,
            NotificacionService notificacionService,
            SubscriptionService subscriptionService
    ) {
        this.prestamoService = prestamoService;
        this.notificacionService = notificacionService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Prestamo crearPrestamoCompleto(Map<String, Object> request) {
        Long usuarioId = ((Number) request.get("usuarioId")).longValue();
        Long libroId = ((Number) request.get("libroId")).longValue();
        String observaciones = (String) request.get("observaciones");

        Prestamo prestamo = Prestamo.builder()
                .usuarioId(usuarioId)
                .libroId(libroId)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucionEsperada(LocalDate.now().plusDays(14))
                .estado("ACTIVO")
                .observaciones(observaciones)
                .requiereAprobacion(false)
                .build();

        Prestamo saved = prestamoService.crearPrestamo(prestamo);

        User usuario = new User();
        usuario.setId(usuarioId);
        boolean esPremium = subscriptionService.verificarSuscripcionPremium(usuario);

        String mensaje = "Su préstamo #" + saved.getId() + " ha sido creado";
        notificacionService.notificarUsuario(usuarioId.toString(), mensaje, esPremium);

        return saved;
    }

    @Override
    public Prestamo devolverPrestamo(Long prestamoId) {
        Prestamo prestamo = prestamoService.buscarPrestamo(prestamoId);
        if (prestamo == null) {
            throw new RuntimeException("Préstamo no encontrado: " + prestamoId);
        }

        LocalDate hoy = LocalDate.now();
        int multa = 0;

        if (prestamo.getFechaDevolucionEsperada() != null &&
            hoy.isAfter(prestamo.getFechaDevolucionEsperada())) {
            long diasRetraso = ChronoUnit.DAYS.between(
                prestamo.getFechaDevolucionEsperada(),
                hoy
            );
            multa = (int) (diasRetraso * 500);
        }

        Prestamo actualizado = Prestamo.builder()
                .id(prestamo.getId())
                .usuarioId(prestamo.getUsuarioId())
                .libroId(prestamo.getLibroId())
                .fechaPrestamo(prestamo.getFechaPrestamo())
                .fechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada())
                .fechaDevolucionReal(hoy)
                .estado("DEVUELTO")
                .observaciones(prestamo.getObservaciones())
                .multasAcumuladas(multa)
                .build();

        Prestamo result = prestamoService.actualizarPrestamo(prestamoId, actualizado);

        User usuario = new User();
        usuario.setId(prestamo.getUsuarioId());
        boolean esPremium = subscriptionService.verificarSuscripcionPremium(usuario);

        String mensaje = "Préstamo devuelto. Multa: $" + multa;
        notificacionService.notificarUsuario(
            prestamo.getUsuarioId().toString(),
            mensaje,
            esPremium
        );

        return result;
    }

    @Override
    public Prestamo renovarPrestamo(Long prestamoId, int dias) {
        Prestamo prestamo = prestamoService.buscarPrestamo(prestamoId);
        if (prestamo == null) {
            throw new RuntimeException("Préstamo no encontrado: " + prestamoId);
        }

        int vecesRenovado = prestamo.getVecesRenovado() != null ? prestamo.getVecesRenovado() : 0;
        if (vecesRenovado >= 2) {
            throw new RuntimeException("Límite de renovaciones excedido (máximo 2)");
        }

        LocalDate nuevaFecha = prestamo.getFechaDevolucionEsperada().plusDays(dias);

        Prestamo actualizado = Prestamo.builder()
                .id(prestamo.getId())
                .usuarioId(prestamo.getUsuarioId())
                .libroId(prestamo.getLibroId())
                .fechaPrestamo(prestamo.getFechaPrestamo())
                .fechaDevolucionEsperada(nuevaFecha)
                .estado("ACTIVO")
                .observaciones(prestamo.getObservaciones())
                .vecesRenovado(vecesRenovado + 1)
                .build();

        Prestamo result = prestamoService.actualizarPrestamo(prestamoId, actualizado);

        User usuario = new User();
        usuario.setId(prestamo.getUsuarioId());
        boolean esPremium = subscriptionService.verificarSuscripcionPremium(usuario);

        String mensaje = "Préstamo renovado hasta " + nuevaFecha;
        notificacionService.notificarUsuario(
            prestamo.getUsuarioId().toString(),
            mensaje,
            esPremium
        );

        return result;
    }
}