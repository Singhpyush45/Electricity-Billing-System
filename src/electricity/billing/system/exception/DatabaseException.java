package electricity.billing.system.exception;

/**
 * Thrown when a database operation fails (connection problem, bad SQL, etc.).
 * It wraps the original SQLException so the real cause is not lost.
 */
public class DatabaseException extends EBSException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
