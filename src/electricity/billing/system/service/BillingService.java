package electricity.billing.system.service;

import electricity.billing.system.billing.Bill;
import electricity.billing.system.billing.BillFactory;
import electricity.billing.system.dao.BillDAO;
import electricity.billing.system.dao.MeterDAO;
import electricity.billing.system.dao.TaxDAO;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.exception.DuplicateBillException;
import electricity.billing.system.model.BillRecord;
import electricity.billing.system.model.Meter;
import electricity.billing.system.model.TaxRate;

/**
 * Business logic for bills: combines the meter, tax and bill DAOs.
 */
public class BillingService {

    /**
     * One shared lock object for the whole program (static).
     * "check if the bill exists" and "insert the bill" must happen together;
     * if two threads did the check at the same time, both would see "no bill"
     * and both would insert - a duplicate. The synchronized block lets only
     * ONE thread run that section at a time.
     */
    private static final Object BILL_LOCK = new Object();

    private final BillDAO billDAO = new BillDAO();
    private final MeterDAO meterDAO = new MeterDAO();
    private final TaxDAO taxDAO = new TaxDAO();

    public Bill generateBill(String meterNo, String month, int units)
            throws DatabaseException, DuplicateBillException {
        synchronized (BILL_LOCK) {
            if (billDAO.exists(meterNo, month)) {
                throw new DuplicateBillException(meterNo, month);
            }
            Bill bill = buildBill(meterNo, month, units);
            billDAO.add(bill.toRecord());
            return bill;
        }
    }

    /** Rebuilds a Bill object from a saved record, so its charge breakdown can be shown. */
    public Bill rebuildBill(BillRecord record) throws DatabaseException {
        return buildBill(record.getMeterNo(), record.getMonth(), record.getUnits());
    }

    private Bill buildBill(String meterNo, String month, int units) throws DatabaseException {
        String billType = meterDAO.findById(meterNo)
                .map(Meter::getBillType)
                .orElse("Normal");
        TaxRate tax = taxDAO.getTaxRate();
        return BillFactory.createBill(billType, meterNo, month, units, tax);
    }
}
