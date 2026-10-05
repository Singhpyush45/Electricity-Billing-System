package electricity.billing.system.dao;

import electricity.billing.system.db.DBConnection;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.model.BillKey;
import electricity.billing.system.model.BillRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All database operations on the bill table.
 * A bill is identified by meter number + month, so the key type is BillKey.
 */
public class BillDAO implements GenericDAO<BillRecord, BillKey> {

    private static final String INSERT =
            "INSERT INTO bill (meter_no, month, units, totalbill, status) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ONE = "SELECT * FROM bill WHERE meter_no = ? AND month = ?";
    private static final String SELECT_ALL = "SELECT * FROM bill";
    private static final String SELECT_BY_METER = "SELECT * FROM bill WHERE meter_no = ?";
    private static final String COUNT_ONE = "SELECT COUNT(*) FROM bill WHERE meter_no = ? AND month = ?";
    private static final String UPDATE =
            "UPDATE bill SET units = ?, totalbill = ?, status = ? WHERE meter_no = ? AND month = ?";
    private static final String MARK_PAID =
            "UPDATE bill SET status = ? WHERE meter_no = ? AND month = ?";

    private final DBConnection db = DBConnection.getInstance();

    @Override
    public void add(BillRecord b) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT)) {
            ps.setString(1, b.getMeterNo());
            ps.setString(2, b.getMonth());
            ps.setString(3, String.valueOf(b.getUnits()));
            ps.setString(4, String.valueOf(b.getTotalBill()));
            ps.setString(5, b.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save bill: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<BillRecord> findById(BillKey key) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ONE)) {
            ps.setString(1, key.getMeterNo());
            ps.setString(2, key.getMonth());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load bill: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BillRecord> findAll() throws DatabaseException {
        return query(SELECT_ALL, null);
    }

    public List<BillRecord> findByMeter(String meterNo) throws DatabaseException {
        return query(SELECT_BY_METER, meterNo);
    }

    @Override
    public boolean update(BillRecord b) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, String.valueOf(b.getUnits()));
            ps.setString(2, String.valueOf(b.getTotalBill()));
            ps.setString(3, b.getStatus());
            ps.setString(4, b.getMeterNo());
            ps.setString(5, b.getMonth());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Could not update bill: " + e.getMessage(), e);
        }
    }

    public boolean exists(String meterNo, String month) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(COUNT_ONE)) {
            ps.setString(1, meterNo);
            ps.setString(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not check bill: " + e.getMessage(), e);
        }
    }

    public boolean markAsPaid(String meterNo, String month) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(MARK_PAID)) {
            ps.setString(1, BillRecord.PAID);
            ps.setString(2, meterNo);
            ps.setString(3, month);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Could not update payment status: " + e.getMessage(), e);
        }
    }

    /** Runs a SELECT with an optional single parameter and returns every row as a list. */
    private List<BillRecord> query(String sql, String param) throws DatabaseException {
        List<BillRecord> bills = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load bills: " + e.getMessage(), e);
        }
        return bills;
    }

    private BillRecord mapRow(ResultSet rs) throws SQLException {
        return new BillRecord(
                rs.getString("meter_no"),
                rs.getString("month"),
                toInt(rs.getString("units")),
                toInt(rs.getString("totalbill")),
                rs.getString("status"));
    }

    /** Values are stored as text in the table, so convert them safely. */
    private static int toInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
