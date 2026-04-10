package com.biblioteca.digital.domain.notification.ports;

public interface SmsSender {
    void send(String usuario, String mensaje);
}