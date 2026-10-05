package electricity.billing.system.exception;

/**
 * Thrown when the "units consumed" value is empty, not a number, or negative.
 * It extends InvalidInputException, so it is also an input error
 * (exception hierarchy = inheritance).
 */
public class InvalidUnitsException extends InvalidInputException {

    public InvalidUnitsException(String message) {
        super(message);
    }
}
