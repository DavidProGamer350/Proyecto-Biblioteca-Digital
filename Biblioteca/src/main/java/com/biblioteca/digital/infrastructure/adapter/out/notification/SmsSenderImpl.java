package com.biblioteca.digital.infrastructure.adapter.out.notification;

import com.biblioteca.digital.domain.notification.ports.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsSenderImpl implements SmsSender {

    private static final Logger log = LoggerFactory.getLogger(SmsSenderImpl.class);

    @Override
    public void send(String usuario, String mensaje) {
        log.info("📱 Enviando SMS a {} con mensaje: {}", usuario, mensaje);
    }
}