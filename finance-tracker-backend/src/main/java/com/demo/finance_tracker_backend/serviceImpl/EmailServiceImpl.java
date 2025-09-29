package com.demo.finance_tracker_backend.serviceImpl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.demo.finance_tracker_backend.config.SenderProperties;
import com.demo.finance_tracker_backend.enums.SenderType;
import com.demo.finance_tracker_backend.service.EmailService;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SenderProperties senderProperties;
    /**
     * Send email asynchronously. Can be triggered from an event listener.
     */
//    @Override
//    @Async
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(body);
//
//            mailSender.send(message);
//
//            log.info("Email sent successfully to {}", to);
//        } catch (Exception ex) {
//            log.error("Failed to send email to {}: {}", to, ex.getMessage());
//            // Optional: Implement retry mechanism or send to dead-letter queue
//        }
//    }
    
    @Override
    @Async
    public void sendEmail(SenderType senderType, String to, String subject, String body, boolean isHtml) {
    	try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message,true);
			
			// Recipient and content
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, isHtml);
			
			// Pick sender identity from config
			SenderProperties.Identity identity = switch(senderType) {
			case SUPPORT -> senderProperties.getSupport();
			case NOTIFICATIONS -> senderProperties.getNotifications();
			case REPORTS -> senderProperties.getReports();
			};
			
			helper.setFrom(new InternetAddress(identity.getEmail(), identity.getName()));
			
			mailSender.send(message);
			
			log.info("Mail send successfully to email {} from {}", to, identity.getEmail());
		} catch (Exception e) {
			
			log.error("Fail to send email to {}:{}", to, e.getMessage());
		}
    }
}
