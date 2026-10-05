package electricity.billing.system.model;

/**
 * ABSTRACT parent class for every user of the system.
 *
 * - It cannot be created directly (new Person() is not allowed).
 * - Admin and Customer EXTEND this class (inheritance).
 * - getRole() is abstract, so every child MUST give its own version.
 * - getWelcomeMessage() and getMeterNo() are overridden by children,
 *   so the same method call gives different results (runtime polymorphism).
 */
public abstract class Person {

    private String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** "Admin" or "Customer" - decided by the child class. */
    public abstract String getRole();

    /** Admins have no meter, so the default is empty. Customer overrides this. */
    public String getMeterNo() {
        return "";
    }

    public String getWelcomeMessage() {
        return "Welcome, " + name;
    }

    @Override
    public String toString() {
        return getRole() + ": " + name;
    }
}
