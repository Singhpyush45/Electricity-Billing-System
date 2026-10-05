package electricity.billing.system.billing;

import java.util.Map;

/**
 * INTERFACE: a contract that says "anything that is Billable must be able to
 * calculate a total, give a breakdown of charges, and tell its bill type".
 * The Bill class implements this interface.
 */
public interface Billable {

    int calculateTotal();

    Map<String, Integer> getBreakdown();

    String getBillType();
}
