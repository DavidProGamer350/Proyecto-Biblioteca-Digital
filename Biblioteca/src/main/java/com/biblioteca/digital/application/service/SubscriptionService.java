package com.biblioteca.digital.application.service;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.SubscriptionChecker;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionChecker subscriptionChecker;

    public SubscriptionService(SubscriptionChecker subscriptionChecker) {
        this.subscriptionChecker = subscriptionChecker;
    }

    public boolean verificarSuscripcionPremium(User user) {
    	 System.out.println("Client: usando la interfaz SubscriptionChecker...");
    	return subscriptionChecker.isPremium(user);
    }
}