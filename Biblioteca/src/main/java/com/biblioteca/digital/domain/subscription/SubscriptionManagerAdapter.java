package com.biblioteca.digital.domain.subscription;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.User;
import com.biblioteca.digital.domain.port.in.SubscriptionChecker;

@Component
public class SubscriptionManagerAdapter implements SubscriptionChecker {

    @Override
    public boolean isPremium(User user) {
        return SubscriptionManager.INSTANCE.isPremium(user);
    }
}