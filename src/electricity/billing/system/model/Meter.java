package electricity.billing.system.model;

/**
 * Meter details of a customer (maps to the meter_info table).
 */
public class Meter {

    public static final int DEFAULT_DAYS = 30;

    private final String meterNo;
    private final String location;
    private final String type;
    private final String phaseCode;
    private final String billType;
    private final int days;

    public Meter(String meterNo, String location, String type,
                 String phaseCode, String billType, int days) {
        this.meterNo = meterNo;
        this.location = location;
        this.type = type;
        this.phaseCode = phaseCode;
        this.billType = billType;
        this.days = days;
    }

    public String getMeterNo()   { return meterNo; }
    public String getLocation()  { return location; }
    public String getType()      { return type; }
    public String getPhaseCode() { return phaseCode; }
    public String getBillType()  { return billType; }
    public int getDays()         { return days; }
}
