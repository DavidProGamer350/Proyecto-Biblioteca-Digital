package com.biblioteca.digital.domain.notification;

import com.biblioteca.digital.domain.notification.ports.SmsSender;

public class SmsDecorator extends NotificadorDecorator {

    private final SmsSender smsSender;

    public SmsDecorator(Notificador notificador, SmsSender smsSender) {
        super(notificador);
        this.smsSender = smsSender;
    }

    @Override
    public void enviar(String usuario, String mensaje) {
        super.enviar(usuario, mensaje);
        smsSender.send(usuario, mensaje);
    }
}