package electricity.billing.system.model;

/**
 * An administrator of the billing system. Inherits from Person.
 */
public class Admin extends Person {

    private String username;

    public Admin(String username, String name) {
        super(name);                 // calls the Person constructor
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String getWelcomeMessage() {
        return "Admin Dashboard - Welcome, " + getName();
    }
}
