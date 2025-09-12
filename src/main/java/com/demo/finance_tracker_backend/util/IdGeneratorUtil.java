package com.demo.finance_tracker_backend.util;

import java.security.SecureRandom;
import java.util.UUID;

public class IdGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a safer unique medium numeric ID (~16 digits).
     * Combines current timestamp in milliseconds (13 digits) + 3 random digits.
     * Example: 1693569123456123
     */
    public static String generateMediumNumericId() {
        long timestampMillis = System.currentTimeMillis(); // 13-digit timestamp
        int randomDigits = random.nextInt(900) + 100; // 3 random digits (100-999)
        return String.valueOf(timestampMillis) + randomDigits;
    }

    /**
     * Generates an ID with a given prefix and numeric uniqueness.
     * Example: CAT-1693569123456123
     */
    public static String generatePrefixedId(String prefix) {
        return prefix + "-" + generateMediumNumericId();
    }

    /**
     * Generates a short random alphanumeric ID (8 characters).
     * Example: A1B2C3D4
     */
    public static String generateShortAlphaNumericId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
