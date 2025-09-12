package com.demo.finance_tracker_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.finance_tracker_backend.scheduler.UserCleanupSchedular;

import lombok.RequiredArgsConstructor;

// Temporary testing endpoint
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
	
    private final UserCleanupSchedular userCleanupScheduler;

    @GetMapping("/cleanup")
    public String triggerCleanup() {
        userCleanupScheduler.cleanupUnverifiedUsers();
        return "Cleanup triggered!";
    }
}
