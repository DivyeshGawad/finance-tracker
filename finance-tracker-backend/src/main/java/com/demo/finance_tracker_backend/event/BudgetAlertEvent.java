package com.demo.finance_tracker_backend.event;

import com.demo.finance_tracker_backend.entity.BudgetEntity;
import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.enums.BudgetAlertType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BudgetAlertEvent {
	
	private final UserEntity user;        // The user who will receive the alert
    private final BudgetEntity budget;    // The budget being monitored
    private final BudgetAlertType type;         // Type of alert (LOW_BUDGET / OVERSPENDING)
    private final double currentSpent;    // Amount already spent
    private final double budgetLimit;     // Limit set in budget
}
