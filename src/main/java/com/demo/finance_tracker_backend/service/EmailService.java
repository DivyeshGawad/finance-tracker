package com.demo.finance_tracker_backend.service;

import com.demo.finance_tracker_backend.enums.SenderType;

public interface EmailService {

//	Basic Email Notification
//	void sendEmail(String to, String subject, String body);
	
	// For custom email notification like name as SUPPORT etc. and also to use HTML in email
	void sendEmail(SenderType senderType, String to, String subject, String body, boolean isHtml);
}