package com.biblioteca.digital.domain.port.in;

import com.biblioteca.digital.domain.model.User;

public interface SubscriptionChecker {
	boolean isPremium(User user);
}
