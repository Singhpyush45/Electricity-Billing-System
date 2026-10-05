package electricity.billing.system.exception;

/**
 * Thrown when a bill already exists for the same meter number and month.
 */
public class DuplicateBillException extends EBSException {

    public DuplicateBillException(String meterNo, String month) {
        super("A bill for meter " + meterNo + " for " + month + " already exists.");
    }
}
