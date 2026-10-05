package electricity.billing.system.util;

import java.util.List;
import java.util.function.Function;
import javax.swing.table.DefaultTableModel;

/**
 * Turns a List of ANY type into a JTable model (replaces the old rs2xml library).
 *
 * <T> makes this a GENERIC METHOD: it works for List<Customer>, List<BillRecord>, etc.
 * The "mapper" function says how one object becomes one table row.
 */
public final class TableModelBuilder {

    private TableModelBuilder() {
    }

    public static <T> DefaultTableModel build(String[] columns, List<T> rows,
                                              Function<T, Object[]> mapper) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;       // read-only table
            }
        };
        for (T item : rows) {
            model.addRow(mapper.apply(item));
        }
        return model;
    }
}
