package com.biblioteca.digital.domain.notification;

import com.biblioteca.digital.domain.notification.ports.EmailSender;
import com.biblioteca.digital.domain.notification.ports.SmsSender;
import com.biblioteca.digital.domain.notification.ports.WhatsappSender;
import com.biblioteca.digital.domain.service.NotificacionService;

public class PruebaDecorator {

	    public static void main(String[] args) {

	        EmailSender email = (u, m) -> System.out.println("[EMAIL] " + m);
	        SmsSender sms = (u, m) -> System.out.println("[SMS] " + m);
	        WhatsappSender wa = (u, m) -> System.out.println("[WA] " + m);

	        NotificacionService service = new NotificacionService(email, sms, wa);

	        System.out.println("Usuario GRATIS:");
	        service.notificarUsuario("user1", "Esto es una notificacion de prueba", false);

	        System.out.println("\nUsuario PREMIUM:");
	        service.notificarUsuario("user2", "Esto es una notificacion de prueba", true);
	    }
	}
