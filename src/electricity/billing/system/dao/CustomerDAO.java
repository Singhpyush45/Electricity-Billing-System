package electricity.billing.system.dao;

import electricity.billing.system.db.DBConnection;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * All database operations on the customer table.
 * Uses PreparedStatement (with ? placeholders) everywhere, which prevents SQL injection.
 */
public class CustomerDAO implements GenericDAO<Customer, String> {

    private static final String INSERT_CUSTOMER =
            "INSERT INTO customer (name, meter_no, address, city, state, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_EMPTY_LOGIN =
            "INSERT INTO login (meter_no, username, name, password, `user`) VALUES (?, '', ?, '', '')";
    private static final String SELECT_BY_METER = "SELECT * FROM customer WHERE meter_no = ?";
    private static final String SELECT_ALL = "SELECT * FROM customer";
    private static final String SELECT_METERS = "SELECT meter_no FROM customer";
    private static final String UPDATE =
            "UPDATE customer SET address = ?, city = ?, state = ?, email = ?, phone = ? WHERE meter_no = ?";

    private final DBConnection db = DBConnection.getInstance();

    @Override
    public void add(Customer c) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_CUSTOMER)) {
            fillInsert(ps, c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not add customer: " + e.getMessage(), e);
        }
    }

    /**
     * Adds the customer AND an empty login row in ONE TRANSACTION.
     * Either both rows are saved (commit) or neither is (rollback),
     * so the database can never be left half-updated.
     */
    public void addWithLoginAccount(Customer c) throws DatabaseException {
        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false);                       // start transaction

            try (PreparedStatement ps1 = con.prepareStatement(INSERT_CUSTOMER);
                 PreparedStatement ps2 = con.prepareStatement(INSERT_EMPTY_LOGIN)) {
                fillInsert(ps1, c);
                ps1.executeUpdate();

                ps2.setString(1, c.getMeterNo());
                ps2.setString(2, c.getName());
                ps2.executeUpdate();
            }
            con.commit();                                   // save both
        } catch (SQLException e) {
            rollbackQuietly(con);                           // undo both
            throw new DatabaseException("Could not add customer: " + e.getMessage(), e);
        } finally {
            closeQuietly(con);                              // always runs
        }
    }

    @Override
    public Optional<Customer> findById(String meterNo) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_METER)) {
            ps.setString(1, meterNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load customer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Customer> findAll() throws DatabaseException {
        List<Customer> customers = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load customers: " + e.getMessage(), e);
        }
        return customers;
    }

    @Override
    public boolean update(Customer c) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, c.getAddress());
            ps.setString(2, c.getCity());
            ps.setString(3, c.getState());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getPhone());
            ps.setString(6, c.getMeterNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Could not update customer: " + e.getMessage(), e);
        }
    }

    /** All meter numbers, in the order stored - used to fill dropdowns. */
    public List<String> getAllMeterNumbers() throws DatabaseException {
        List<String> meters = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_METERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                meters.add(rs.getString("meter_no"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not load meter numbers: " + e.getMessage(), e);
        }
        return meters;
    }

    /** Same data as a HashSet - checking contains() in a Set is very fast. */
    public Set<String> getMeterNumberSet() throws DatabaseException {
        return new HashSet<>(getAllMeterNumbers());
    }

    private void fillInsert(PreparedStatement ps, Customer c) throws SQLException {
        ps.setString(1, c.getName());
        ps.setString(2, c.getMeterNo());
        ps.setString(3, c.getAddress());
        ps.setString(4, c.getCity());
        ps.setString(5, c.getState());
        ps.setString(6, c.getEmail());
        ps.setString(7, c.getPhone());
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("name"),
                rs.getString("meter_no"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("email"),
                rs.getString("phone"));
    }

    private static void rollbackQuietly(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
                // nothing more we can do
            }
        }
    }

    private static void closeQuietly(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ignored) {
                // nothing more we can do
            }
        }
    }
}
