package electricity.billing.system.util;

import electricity.billing.system.exception.InvalidInputException;
import electricity.billing.system.exception.InvalidUnitsException;

/**
 * Checks form input before it goes to the database.
 * Each method THROWS a custom exception if the input is wrong.
 */
public final class Validator {

    private Validator() {
    }

    public static String requireText(String fieldName, String value) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public static int parseUnits(String value) throws InvalidUnitsException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUnitsException("Please enter the units consumed.");
        }
        int units;
        try {
            units = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidUnitsException("Units must be a whole number, e.g. 150.");
        }
        if (units < 0) {
            throw new InvalidUnitsException("Units cannot be negative.");
        }
        return units;
    }

    public static String validateEmail(String email) throws InvalidInputException {
        String value = requireText("Email", email);
        if (!value.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new InvalidInputException("Please enter a valid email address.");
        }
        return value;
    }

    public static String validatePhone(String phone) throws InvalidInputException {
        String value = requireText("Phone number", phone);
        if (!value.matches("\\d{10}")) {
            throw new InvalidInputException("Phone number must be exactly 10 digits.");
        }
        return value;
    }
}
