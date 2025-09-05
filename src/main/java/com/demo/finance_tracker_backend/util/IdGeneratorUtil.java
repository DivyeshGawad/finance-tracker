package com.demo.finance_tracker_backend.util;

import java.security.SecureRandom;

public class IdGeneratorUtil {

	    private static final SecureRandom random = new SecureRandom();

	    /**
	     * Generates a unique medium numeric ID (~14 digits)
	     * Combines current timestamp in seconds (10 digits) + 4 random digits
	     * Example output: 16935691234567
	     */
	    public static String generateMediumNumericId() {
	        long timestampSeconds = System.currentTimeMillis() / 1000; // 10-digit timestamp
	        int randomDigits = random.nextInt(9000) + 1000;           // 4 random digits (1000-9999)
	        return String.valueOf(timestampSeconds) + randomDigits;   // total 14 digits
	    }
	}
