package electricity.billing.system.db;

import electricity.billing.system.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gives database connections to the rest of the application (JDBC).
 *
 * SINGLETON pattern: only ONE DBConnection object exists in the whole program.
 * The settings file is read once and the MySQL driver is loaded once.
 *
 * getInstance() is SYNCHRONIZED: if two threads call it at the same moment,
 * only one can enter at a time, so two objects can never be created by accident.
 *
 * Each DAO method asks for a fresh Connection and closes it with
 * try-with-resources, so connections never leak.
 */
public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/ebs?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "ebsuser";
    private static final String DEFAULT_PASSWORD = "ebs123";

    private static DBConnection instance;

    private final String url;
    private final String user;
    private final String password;

    /** Private constructor: nobody outside can write new DBConnection(). */
    private DBConnection() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Could not read db.properties, using default settings: " + e.getMessage());
        }
        url = props.getProperty("db.url", DEFAULT_URL);
        user = props.getProperty("db.user", DEFAULT_USER);
        password = props.getProperty("db.password", DEFAULT_PASSWORD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Connector/J jar is not on the classpath.");
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /** Opens a new connection. The caller must close it (use try-with-resources). */
    public Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Could not connect to the database.\n"
                    + "Check that MySQL is running and the settings in db.properties are correct.\n\n"
                    + e.getMessage(), e);
        }
    }
}
