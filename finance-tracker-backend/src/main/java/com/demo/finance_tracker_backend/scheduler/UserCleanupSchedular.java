package com.demo.finance_tracker_backend.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.config.CleanupProperties;
import com.demo.finance_tracker_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserCleanupSchedular {

	private final UserRepository userRepository;
	private final CleanupProperties cleanupProperties;
	
	@Scheduled(cron = "0 0 2 * * ?")
	public void cleanupUnverifiedUsers() {
		int graceDays = cleanupProperties.getDays();
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(graceDays);
		
		userRepository.deleteByIsVerifiedFalseAndCreatedAtBefore(cutoffDate);
		log.info("CLeanup job executed: removed unverified account older than {} days",graceDays);
	}
}
