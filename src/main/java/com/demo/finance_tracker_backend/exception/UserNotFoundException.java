package com.demo.finance_tracker_backend.exception;

public class UserNotFoundException extends RuntimeException {
    
	public UserNotFoundException(String message) {
        super(message);
    }
}
