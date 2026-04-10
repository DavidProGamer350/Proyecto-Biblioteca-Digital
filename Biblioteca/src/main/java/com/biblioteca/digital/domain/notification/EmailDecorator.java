package com.biblioteca.digital.domain.notification;

import com.biblioteca.digital.domain.notification.ports.EmailSender;

public class EmailDecorator extends NotificadorDecorator {

    private final EmailSender emailSender;

    public EmailDecorator(Notificador notificador, EmailSender emailSender) {
        super(notificador);
        this.emailSender = emailSender;
    }

    @Override
    public void enviar(String usuario, String mensaje) {
        super.enviar(usuario, mensaje);
        emailSender.send(usuario, mensaje);
    }
}