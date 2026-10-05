package electricity.billing.system.service;

import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.exception.DatabaseException;
import java.util.Random;
import java.util.Set;

/**
 * Generates a UNIQUE 6-digit meter number.
 *
 * All existing numbers are loaded into a HashSet, and a new random number is
 * picked until it is not in the set. The method is "static synchronized",
 * which locks on the class itself, so two threads can never generate at the same time.
 */
public final class MeterNumberGenerator {

    private static final Random RANDOM = new Random();

    private MeterNumberGenerator() {
        // utility class - no objects needed
    }

    public static synchronized String generate() throws DatabaseException {
        Set<String> used = new CustomerDAO().getMeterNumberSet();
        String candidate;
        do {
            candidate = String.valueOf(100000 + RANDOM.nextInt(900000));   // 100000 - 999999
        } while (used.contains(candidate));
        return candidate;
    }
}
