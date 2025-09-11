package com.demo.finance_tracker_backend.serviceImpl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.enums.SenderType;
import com.demo.finance_tracker_backend.event.EmailChangeEvent;
import com.demo.finance_tracker_backend.event.listner.EmailChangeEventListner;
import com.demo.finance_tracker_backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
class EmailChangeEventListnerTest {

	    @Mock
	    private EmailService emailService;

	    private EmailChangeEventListner listener;

	    @BeforeEach
	    void setUp() {
	        listener = new EmailChangeEventListner(emailService);
	    }

	    @Test
	    void shouldSendRegistrationEmailWhenEventIsReceived() {
	        UserEntity user = UserEntity.builder()
	                .firstName("John")
	                .email("john@example.com")
	                .build();

	        EmailChangeEvent event = new EmailChangeEvent(user,
	                EmailChangeEvent.EmailType.REGISTRATION, null, null);

	        listener.handleEventChangeEvent(event);

	        verify(emailService, times(1)).sendEmail(
	                eq(SenderType.NOTIFICATIONS),
	                eq("john@example.com"),
	                contains("Welcome"),
	                anyString(),
	                eq(true)
	        );
	    }
	}
