package com.biblioteca.digital.domain.notification.ports;

public interface EmailSender {
    void send(String usuario, String mensaje);
}