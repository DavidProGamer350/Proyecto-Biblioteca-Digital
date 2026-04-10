package com.biblioteca.digital.infrastructure.adapter.out.notification;

import com.biblioteca.digital.domain.notification.ports.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderImpl implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderImpl.class);

    @Override
    public void send(String usuario, String mensaje) {
        log.info("📧 Enviando EMAIL a {} con mensaje: {}", usuario, mensaje);
    }
}