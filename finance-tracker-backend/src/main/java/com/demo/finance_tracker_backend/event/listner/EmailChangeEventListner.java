package com.demo.finance_tracker_backend.event.listner;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.demo.finance_tracker_backend.enums.SenderType;
import com.demo.finance_tracker_backend.event.EmailChangeEvent;
import com.demo.finance_tracker_backend.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailChangeEventListner {

	private final EmailService emailService;

	@Async
	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEventChangeEvent(EmailChangeEvent event) {

		String subject = "";
		String body = "";

//		switch(event.getType()) {
//		case REGISTRATION:
//			subject = "Verify Your Email";
//			body="Hi "+event.getUser().getFirstName()+" "+event.getUser().getLastName()+", \n\n"
//					+"Please Verify your email by clicking the link below:\n"
//					+"http://localhost:8080/api/auth/verify?token=" +event.getUser().getVerificationToken()+"\n\n"
//					+"This link will expires in 24 hours.";
//			break;
//			
//		case EMAIL_UPDATE:
//			subject="Verify Your Updated Email";
//			body="Hi "+event.getUser().getFirstName()+" "+event.getUser().getLastName()+"\n\n"
//					+"Your email has been updated from "+event.getOldEmail()+" to "+event.getNewEmail()+"\n"
//					+"Please verify your new email by clicking the link below:\n"
//					+"http://localhost:8080/api/auth/verify?token=" +event.getUser().getVerificationToken()+"\n\n"
//					+"This link will expires in 24 hours.";
//			break;
//			
//		case PASSSWORD_RESET:
//			subject="Password Reset Request";
//			body="Hi "+event.getUser().getFirstName()+" "+event.getUser().getLastName()+"\n\n"
//					+"You requested a password reset. Please click the link below to reset your password:\n"
//					+"http://localhost:8080/api/auth/reset-password?token=" +event.getUser().getPasswordResetToken()+"\n\n"
//					+"This link will expires in 15 minutes";
//			break;
//			
//		case USERNAME_RECOVERY:
//			subject="Username Recovery";
//			body="Hi "+event.getUser().getFirstName()+" "+event.getUser().getLastName()+"\n\n"
//					+"As requested, here is your username: "+event.getUser().getUsername()+"\n\n"
//					+"Regards,\nFinance Tracker Team";
//			break;
//		}

		switch (event.getType()) {
		case REGISTRATION -> {

			subject = "Verify Your Email";
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + ", </p>"
					+ "<p>Please Verify your email by clicking the link below:</p>"
					+ "<a href='http://localhost:8080/api/auth/verify?token=" + event.getUser().getVerificationToken()
					+ "'>Verify Email</a>" + "<p>This link will expires in 24 hours.</p>";
		}

		case EMAIL_UPDATE -> {

			subject = "Verify Your Updated Email";
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + "</p>"
					+ "<p>Your email has been updated from " + event.getOldEmail() + " to " + event.getNewEmail()
					+ "</p>" + "<p>Please verify your new email by clicking the link below:</p>"
					+ "<a href='http://localhost:8080/api/auth/verify?token=" + event.getUser().getVerificationToken()
					+ "'>Verify Updated Email</a>" + "<p>This link will expires in 24 hours.</p>";
		}

		case PASSSWORD_RESET -> {

			subject = "Password Reset Request";
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + "<p>"
					+ "<p>You requested a password reset. Please click the link below to reset your password:</p>"
					+ "<a href='http://localhost:8080/api/auth/reset-password?token="
					+ event.getUser().getPasswordResetToken() + "'>Reset Password</a>" + "\n\n"
					+ "<p>This link will expires in 15 minutes.</p>";
		}

		case USERNAME_RECOVERY -> {

			subject = "Username Recovery";
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + "</p>"
					+ "<p>As requested, here is your username: <strong>" + event.getUser().getUsername()
					+ "</strong></p>" + "<p>Regards,\nFinance Tracker Team.</p>";
		}
		}

		SenderType senderType = switch (event.getType()) {
		case REGISTRATION, EMAIL_UPDATE -> SenderType.NOTIFICATIONS;
		case PASSSWORD_RESET, USERNAME_RECOVERY -> SenderType.SUPPORT;
		};

		emailService.sendEmail(senderType, event.getUser().getEmail(), subject, body, true); // HTML = true
		 log.info("AUDIT: {} email sent to {} via {}", event.getType(), event.getUser().getEmail(), senderType);
	}
}
