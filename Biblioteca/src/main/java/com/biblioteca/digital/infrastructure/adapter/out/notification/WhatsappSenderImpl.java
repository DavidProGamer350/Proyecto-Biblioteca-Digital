package com.biblioteca.digital.infrastructure.adapter.out.notification;

import com.biblioteca.digital.domain.notification.ports.WhatsappSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WhatsappSenderImpl implements WhatsappSender {

    private static final Logger log = LoggerFactory.getLogger(WhatsappSenderImpl.class);

    @Override
    public void send(String usuario, String mensaje) {
        log.info("💬 Enviando WhatsApp a {} con mensaje: {}", usuario, mensaje);
    }
}