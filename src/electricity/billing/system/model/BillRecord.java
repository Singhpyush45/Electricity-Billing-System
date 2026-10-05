package electricity.billing.system.model;

/**
 * One row of the bill table: the bill of one meter for one month.
 */
public class BillRecord {

    public static final String PAID = "Paid";
    public static final String NOT_PAID = "Not Paid";

    private final String meterNo;
    private final String month;
    private final int units;
    private final int totalBill;
    private String status;

    public BillRecord(String meterNo, String month, int units, int totalBill, String status) {
        this.meterNo = meterNo;
        this.month = month;
        this.units = units;
        this.totalBill = totalBill;
        this.status = status;
    }

    public String getMeterNo() { return meterNo; }
    public String getMonth()   { return month; }
    public int getUnits()      { return units; }
    public int getTotalBill()  { return totalBill; }
    public String getStatus()  { return status; }

    public void setStatus(String status) { this.status = status; }

    public boolean isPaid() {
        return PAID.equalsIgnoreCase(status);
    }
}
