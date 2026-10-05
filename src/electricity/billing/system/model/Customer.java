package electricity.billing.system.model;

/**
 * An electricity customer. Inherits from Person and adds address/contact details.
 * The meter number uniquely identifies a customer.
 */
public class Customer extends Person {

    private String meterNo;
    private String address;
    private String city;
    private String state;
    private String email;
    private String phone;

    /** Full constructor - used when reading/writing the customer table. */
    public Customer(String name, String meterNo, String address, String city,
                    String state, String email, String phone) {
        super(name);
        this.meterNo = meterNo;
        this.address = address;
        this.city = city;
        this.state = state;
        this.email = email;
        this.phone = phone;
    }

    /** Short constructor - used after login, when only name and meter are known. */
    public Customer(String name, String meterNo) {
        this(name, meterNo, "", "", "", "", "");
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String getMeterNo() {
        return meterNo;
    }

    @Override
    public String getWelcomeMessage() {
        return "Customer Portal - Welcome, " + getName() + " (Meter: " + meterNo + ")";
    }

    public String getAddress() { return address; }
    public String getCity()    { return city; }
    public String getState()   { return state; }
    public String getEmail()   { return email; }
    public String getPhone()   { return phone; }

    public void setAddress(String address) { this.address = address; }
    public void setCity(String city)       { this.city = city; }
    public void setState(String state)     { this.state = state; }
    public void setEmail(String email)     { this.email = email; }
    public void setPhone(String phone)     { this.phone = phone; }
}
