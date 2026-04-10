package com.biblioteca.digital.domain.notification.ports;

public interface WhatsappSender {
    void send(String usuario, String mensaje);
}