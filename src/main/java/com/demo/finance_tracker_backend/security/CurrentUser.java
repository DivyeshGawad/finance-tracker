package com.demo.finance_tracker_backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    // Get the full CustomUserDetails object
    public CustomUserDetails get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    // Get only userId
    public String getUserId() {
        return get().getUserId();
    }

    // Get only username
    public String getUsername() {
        return get().getUsername();
    }
}
