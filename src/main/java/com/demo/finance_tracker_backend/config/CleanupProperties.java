package com.demo.finance_tracker_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "cleanup.unverified")
public class CleanupProperties {
	
	private int days;
    private boolean dryRun = false; // default false

}
