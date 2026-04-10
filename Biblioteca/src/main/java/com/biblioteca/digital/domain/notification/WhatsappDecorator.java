package com.biblioteca.digital.domain.notification;

import com.biblioteca.digital.domain.notification.ports.WhatsappSender;

public class WhatsappDecorator extends NotificadorDecorator {

    private final WhatsappSender whatsappSender;

    public WhatsappDecorator(Notificador notificador, WhatsappSender whatsappSender) {
        super(notificador);
        this.whatsappSender = whatsappSender;
    }

    @Override
    public void enviar(String usuario, String mensaje) {
        super.enviar(usuario, mensaje);
        whatsappSender.send(usuario, mensaje);
    }
}