package com.biblioteca.digital.domain.service;

import org.springframework.stereotype.Service;
import com.biblioteca.digital.domain.notification.*;
import com.biblioteca.digital.domain.notification.ports.*;

@Service
public class NotificacionService {

    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final WhatsappSender whatsappSender;

    public NotificacionService(
            EmailSender emailSender,
            SmsSender smsSender,
            WhatsappSender whatsappSender
    ) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.whatsappSender = whatsappSender;
    }

    public void notificarUsuario(String usuario, String mensaje, boolean esPremium) {

        Notificador notificador = new NotificadorBasico();

        // 🔥 Decoración dinámica
        notificador = new EmailDecorator(notificador, emailSender);

        if (esPremium) {
            notificador = new SmsDecorator(notificador, smsSender);
            notificador = new WhatsappDecorator(notificador, whatsappSender);
        }

        notificador.enviar(usuario, mensaje);
    }
}