package com.biblioteca.digital.domain.subscription;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.SubscriptionChecker;

@Component
public class SubscriptionManagerAdapter implements SubscriptionChecker {

    @Override
    public boolean isPremium(User user) {
    	System.out.println("Adapter: traduciendo llamada del cliente hacia SubscriptionManager...");
        return SubscriptionManager.INSTANCE.isPremium(user);
    }
}