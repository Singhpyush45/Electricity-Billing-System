package electricity.billing.system.util;

import java.awt.Choice;
import java.util.List;

/**
 * The twelve month names in one place, instead of repeating 12 add() calls in 4 screens.
 */
public final class Months {

    /** An unmodifiable List<String> - nobody can accidentally add or remove a month. */
    public static final List<String> ALL = List.of(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December");

    private Months() {
    }

    /** Adds all months to an AWT dropdown. */
    public static void fill(Choice choice) {
        for (String month : ALL) {
            choice.add(month);
        }
    }
}
