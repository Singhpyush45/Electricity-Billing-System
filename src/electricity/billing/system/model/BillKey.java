package electricity.billing.system.model;

/**
 * A bill is identified by TWO values together: meter number + month.
 * This small class holds that pair so it can be used as the key type
 * in GenericDAO<BillRecord, BillKey>.
 */
public class BillKey {

    private final String meterNo;
    private final String month;

    public BillKey(String meterNo, String month) {
        this.meterNo = meterNo;
        this.month = month;
    }

    public String getMeterNo() { return meterNo; }
    public String getMonth()   { return month; }
}
