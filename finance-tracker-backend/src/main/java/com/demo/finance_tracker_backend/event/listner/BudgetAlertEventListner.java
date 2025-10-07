package com.demo.finance_tracker_backend.event.listner;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.demo.finance_tracker_backend.enums.SenderType;
import com.demo.finance_tracker_backend.event.BudgetAlertEvent;
import com.demo.finance_tracker_backend.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertEventListner {

	private final EmailService emailService;

	@Async
	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBudgetAlert(BudgetAlertEvent event) {

		String subject = "";
		String body = "";

		switch (event.getType()) {
		case LOW_BUDGET -> {
			subject = "Low Budget Alert - " + event.getBudget().getName();
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + ",</p>"
					+ "<p>You have already spent <b>" + event.getCurrentSpent() + "</b> out of your budget limit <b>"
					+ event.getBudgetLimit() + "</b> for <b>" + event.getBudget().getName() + "</b>.</p>"
					+ "<p>This means your budget usage has crossed 80%.</p>" + "<p>Please review your expenses.</p>";
			}
		case OVERSPENDING -> {
			subject = "Overspending Alert - " + event.getBudget().getName();
			body = "<p>Hi " + event.getUser().getFirstName() + " " + event.getUser().getLastName() + ",</p>"
					+ "<p>You have exceeded your budget limit <b>" + event.getBudgetLimit() + "</b> for <b>"
					+ event.getBudget().getName() + "</b>.</p>" + "<p>Current Spending: <b>" + event.getCurrentSpent()
					+ "</b>.</p>" + "<p>Please adjust your expenses accordingly.</p>";
			}
		}
		
		emailService.sendEmail(
				SenderType.NOTIFICATIONS,
				event.getUser().getEmail(),
				subject,
				body,
				true
				);
		
		log.info("Budget Alert Email sent to {} for Budget {} [{}]",
                event.getUser().getEmail(),
                event.getBudget().getName(),
                event.getType());

	}
}
