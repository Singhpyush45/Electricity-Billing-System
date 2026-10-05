package electricity.billing.system.dao;

import electricity.billing.system.db.DBConnection;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.model.Meter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All database operations on the meter_info table.
 */
public class MeterDAO implements GenericDAO<Meter, String> {

    private static final String INSERT =
            "INSERT INTO meter_info (meter_no, meter_location, meter_type, phase_code, bill_type, days) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_METER = "SELECT * FROM meter_info WHERE meter_no = ?";
    private static final String SELECT_ALL = "SELECT * FROM meter_info";
    private static final String UPDATE =
            "UPDATE meter_info SET meter_location = ?, meter_type = ?, phase_code = ?, bill_type = ?, days = ? WHERE meter_no = ?";

    private final DBConnection db = DBConnection.getInstance();

    @Override
    public void add(Meter m) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT)) {
            ps.setString(1, m.getMeterNo());
            ps.setString(2, m.getLocation());
            ps.setString(3, m.getType());
            ps.setString(4, m.getPhaseCode());
            ps.setString(5, m.getBillType());
            ps.setString(6, String.valueOf(m.getDays()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save meter information: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Meter> findById(String meterNo) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_METER)) {
            ps.setString(1, meterNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load meter information: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Meter> findAll() throws DatabaseException {
        List<Meter> meters = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                meters.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load meters: " + e.getMessage(), e);
        }
        return meters;
    }

    @Override
    public boolean update(Meter m) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, m.getLocation());
            ps.setString(2, m.getType());
            ps.setString(3, m.getPhaseCode());
            ps.setString(4, m.getBillType());
            ps.setString(5, String.valueOf(m.getDays()));
            ps.setString(6, m.getMeterNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Could not update meter information: " + e.getMessage(), e);
        }
    }

    private Meter mapRow(ResultSet rs) throws SQLException {
        int days;
        try {
            days = Integer.parseInt(rs.getString("days").trim());
        } catch (NumberFormatException | NullPointerException e) {
            days = Meter.DEFAULT_DAYS;
        }
        return new Meter(
                rs.getString("meter_no"),
                rs.getString("meter_location"),
                rs.getString("meter_type"),
                rs.getString("phase_code"),
                rs.getString("bill_type"),
                days);
    }
}
