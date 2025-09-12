package com.demo.finance_tracker_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinanceTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceTrackerBackendApplication.class, args);
	}

}
