package com.biblioteca.digital.domain.notification;

public interface Notificador {

    void enviar(String usuario, String mensaje);
}