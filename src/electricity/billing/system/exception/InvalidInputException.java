package electricity.billing.system.exception;

/**
 * Thrown when the user enters invalid data in a form
 * (empty name, wrong email format, non-numeric phone, etc.).
 */
public class InvalidInputException extends EBSException {

    public InvalidInputException(String message) {
        super(message);
    }
}
