package electricity.billing.system.billing;

import electricity.billing.system.model.BillRecord;
import electricity.billing.system.model.TaxRate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ABSTRACT parent class for all bill types.
 *
 * The fixed charges (meter rent, taxes...) are the same for every bill,
 * so they are written ONCE here. Only the energy charge differs between
 * a Normal and an Industrial bill, so calculateEnergyCharge() is abstract
 * and each child class writes its own version.
 *
 * When code calls bill.calculateTotal() on a Bill variable, Java decides
 * AT RUNTIME which child's calculateEnergyCharge() to run. That is
 * runtime polymorphism.
 */
public abstract class Bill implements Billable {

    protected final String meterNo;
    protected final String month;
    protected final int units;
    protected final TaxRate tax;

    protected Bill(String meterNo, String month, int units, TaxRate tax) {
        this.meterNo = meterNo;
        this.month = month;
        this.units = units;
        this.tax = tax;
    }

    /** Each bill type calculates the energy (units) charge differently. */
    protected abstract int calculateEnergyCharge();

    /**
     * Charge name -> amount. A LinkedHashMap is used (Collections + Generics)
     * because it remembers the insertion order, so the bill prints in a fixed order.
     */
    @Override
    public Map<String, Integer> getBreakdown() {
        Map<String, Integer> charges = new LinkedHashMap<>();
        charges.put("Energy Charge", calculateEnergyCharge());
        charges.put("Meter Rent", tax.getMeterRent());
        charges.put("Service Charge", tax.getServiceCharge());
        charges.put("Service Tax", tax.getServiceTax());
        charges.put("Swacch Bharat Cess", tax.getSwacchBharatCess());
        charges.put("Fixed Tax", tax.getFixedTax());
        return charges;
    }

    /** Total = sum of every value in the breakdown map. */
    @Override
    public final int calculateTotal() {
        int total = 0;
        for (int amount : getBreakdown().values()) {
            total += amount;
        }
        return total;
    }

    /** Converts this bill into a row that can be saved in the bill table. */
    public BillRecord toRecord() {
        return new BillRecord(meterNo, month, units, calculateTotal(), BillRecord.NOT_PAID);
    }

    public String getMeterNo() { return meterNo; }
    public String getMonth()   { return month; }
    public int getUnits()      { return units; }
}
