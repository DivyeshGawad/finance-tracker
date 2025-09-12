package com.demo.finance_tracker_backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {

    private CurrentUser() {} // Prevent instantiation

    // Get the full CustomUserDetails object
    public static CustomUserDetails get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    // Get only userId
    public static String getUserId() {
        return get().getUserId();
    }

    // Get only username
    public static String getUsername() {
        return get().getUsername();
    }
}
