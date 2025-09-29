package com.demo.finance_tracker_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "finance.mail")
public class SenderProperties {

	private Identity support = new Identity();
	private Identity notifications = new Identity();
	private Identity reports = new Identity();
	
	
	@Data
	public static class Identity{
		private String email;
		private String name;
	}
}
