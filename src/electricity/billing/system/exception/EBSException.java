package electricity.billing.system.exception;

/**
 * Base (parent) exception for the whole application.
 * All custom exceptions extend this class, so the UI can catch
 * every expected problem with a single catch (EBSException e) block.
 *
 * It is a CHECKED exception (extends Exception), which means the compiler
 * forces the caller to handle it with try-catch or declare it with throws.
 */
public class EBSException extends Exception {

    public EBSException(String message) {
        super(message);
    }

    public EBSException(String message, Throwable cause) {
        super(message, cause);
    }
}
