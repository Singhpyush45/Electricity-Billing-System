package electricity.billing.system.exception;

/**
 * Thrown when the username, password or role entered on the login screen is wrong.
 */
public class AuthenticationException extends EBSException {

    public AuthenticationException(String message) {
        super(message);
    }
}
