package electricity.billing.system.billing;

import electricity.billing.system.model.TaxRate;

/**
 * Bill for industrial connections:
 * energy charge = units x cost per unit, plus a 20% industrial surcharge.
 */
public class IndustrialBill extends Bill {

    public static final int SURCHARGE_PERCENT = 20;

    public IndustrialBill(String meterNo, String month, int units, TaxRate tax) {
        super(meterNo, month, units, tax);
    }

    @Override
    protected int calculateEnergyCharge() {
        int base = units * tax.getCostPerUnit();
        return base + (base * SURCHARGE_PERCENT) / 100;
    }

    @Override
    public String getBillType() {
        return "Industrial";
    }
}
