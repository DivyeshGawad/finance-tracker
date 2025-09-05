package com.demo.finance_tracker_backend.event;

import com.demo.finance_tracker_backend.entity.UserEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailChangeEvent {

	private final UserEntity user;
	private final EmailType type;
	private final String oldEmail; // for email update, null for registration or reset
    private final String newEmail; // for email update, null for registration or reset
    
    public enum EmailType{
    	REGISTRATION,
    	EMAIL_UPDATE,
    	PASSSWORD_RESET,
    	USERNAME_RECOVERY
    }
}
