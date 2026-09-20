package utils;

import java.util.regex.Pattern;

/**
 * ValidationUtils - Input validation utility class
 * Centralized validation logic for forms
 * Demonstrates OOP: Utility class, Static methods
 */
public class ValidationUtils {

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate password (min 6 chars)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validate name (non-empty, letters and spaces only)
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    /**
     * Validate amount (positive number)
     */
    public static boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr.trim());
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a string is non-null and non-empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validate password confirmation match
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Sanitize text input (trim whitespace)
     */
    public static String sanitize(String input) {
        return input == null ? "" : input.trim();
    }
}
