package electricity.billing.system.dao;

import electricity.billing.system.db.DBConnection;
import electricity.billing.system.exception.AuthenticationException;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.exception.InvalidInputException;
import electricity.billing.system.model.Admin;
import electricity.billing.system.model.Customer;
import electricity.billing.system.model.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Login, signup and account operations on the login table.
 */
public class LoginDAO {

    private static final String AUTHENTICATE =
            "SELECT * FROM login WHERE username = ? AND password = ? AND `user` = ?";
    private static final String USERNAME_EXISTS = "SELECT COUNT(*) FROM login WHERE username = ?";
    private static final String NAME_BY_METER =
            "SELECT name FROM login WHERE meter_no = ? AND meter_no <> ''";
    private static final String INSERT_ADMIN =
            "INSERT INTO login (meter_no, username, name, password, `user`) VALUES ('', ?, ?, ?, 'Admin')";
    private static final String ACTIVATE_CUSTOMER =
            "UPDATE login SET username = ?, password = ?, `user` = 'Customer' WHERE meter_no = ? AND meter_no <> ''";

    private final DBConnection db = DBConnection.getInstance();

    /**
     * Checks the credentials and returns an Admin or a Customer object.
     * The return type is Person (the parent), so the caller can treat both the same way.
     */
    public Person authenticate(String username, String password, String role)
            throws DatabaseException, AuthenticationException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(AUTHENTICATE)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new AuthenticationException("Invalid username, password or role.");
                }
                String name = rs.getString("name");
                if ("Admin".equals(role)) {
                    return new Admin(username, name);
                }
                return new Customer(name, rs.getString("meter_no"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Login failed: " + e.getMessage(), e);
        }
    }

    public boolean usernameExists(String username) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(USERNAME_EXISTS)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not check username: " + e.getMessage(), e);
        }
    }

    /** The customer's name for a meter number, if the admin has added that customer. */
    public Optional<String> findNameByMeterNo(String meterNo) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(NAME_BY_METER)) {
            ps.setString(1, meterNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.ofNullable(rs.getString("name")) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not look up meter: " + e.getMessage(), e);
        }
    }

    public void createAdmin(Admin admin, String password) throws DatabaseException, InvalidInputException {
        if (usernameExists(admin.getUsername())) {
            throw new InvalidInputException("Username '" + admin.getUsername() + "' is already taken.");
        }
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_ADMIN)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getName());
            ps.setString(3, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not create admin account: " + e.getMessage(), e);
        }
    }

    /** Sets a username and password on the empty login row the admin created. */
    public void activateCustomer(String meterNo, String username, String password)
            throws DatabaseException, InvalidInputException {
        if (usernameExists(username)) {
            throw new InvalidInputException("Username '" + username + "' is already taken.");
        }
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(ACTIVATE_CUSTOMER)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, meterNo);
            if (ps.executeUpdate() == 0) {
                throw new InvalidInputException(
                        "Meter number " + meterNo + " was not found. Ask the admin to add you first.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not create customer account: " + e.getMessage(), e);
        }
    }
}
