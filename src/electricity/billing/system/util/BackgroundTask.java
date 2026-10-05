package electricity.billing.system.util;

import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/**
 * Runs slow work (database queries) on a BACKGROUND THREAD so the window never freezes.
 *
 * Swing rule: the screen may only be updated from the Event Dispatch Thread (EDT).
 * SwingWorker handles this for us:
 *   - doInBackground() runs on a separate worker thread  -> the database query
 *   - done()           runs back on the EDT              -> updating the table/labels
 *
 * <T> is the result type, so the same helper works for List<Customer>, List<String>, etc.
 */
public final class BackgroundTask {

    private BackgroundTask() {
    }

    public static <T> void run(Component parent, Callable<T> task, Consumer<T> onSuccess) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    MessageUtil.showError(parent, e.getCause());
                }
            }
        }.execute();
    }
}
