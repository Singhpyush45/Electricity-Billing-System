package electricity.billing.system.util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Shows popups, so errors are visible to the user instead of only in the console.
 */
public final class MessageUtil {

    private MessageUtil() {
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, Throwable error) {
        error.printStackTrace();   // full details still go to the NetBeans Output panel
        String message = error.getMessage() != null ? error.getMessage() : error.toString();
        showError(parent, message);
    }
}
