package electricity.billing.system.dao;

import electricity.billing.system.db.DBConnection;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.model.TaxRate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reads the current rates from the tax table (it holds a single row).
 */
public class TaxDAO {

    private static final String SELECT = "SELECT * FROM tax LIMIT 1";

    private final DBConnection db = DBConnection.getInstance();

    public TaxRate getTaxRate() throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new DatabaseException("No tax rates found. Insert one row into the tax table.");
            }
            return new TaxRate(
                    toInt(rs, "cost_per_unit"),
                    toInt(rs, "meter_rent"),
                    toInt(rs, "service_charge"),
                    toInt(rs, "service_tax"),
                    toInt(rs, "swacch_bharat_cess"),
                    toInt(rs, "fixed_tax"));
        } catch (SQLException e) {
            throw new DatabaseException("Could not load tax rates: " + e.getMessage(), e);
        }
    }

    private static int toInt(ResultSet rs, String column) throws SQLException, DatabaseException {
        String value = rs.getString(column);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatabaseException("Tax value '" + column + "' must be a whole number, found: " + value);
        }
    }
}
