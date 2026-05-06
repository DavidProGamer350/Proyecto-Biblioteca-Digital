package com.biblioteca.digital.domain.subscription;

import java.time.LocalDate;
import com.biblioteca.digital.domain.model.User;

public enum SubscriptionManager {

	INSTANCE; // única instancia

	public boolean isPremium(User user) {
		System.out.println("Adaptee/Singleton: ejecutando lógica real de suscripción...");

		System.out.println("HashCode instancia: " + this.hashCode());

		if (!user.isSuscripcionActiva()) {
			return false;
		}

		if (user.getFechaExpiracionSuscripcion() == null) {
			return false;
		}

		return user.getFechaExpiracionSuscripcion().isAfter(LocalDate.now());
	}
}