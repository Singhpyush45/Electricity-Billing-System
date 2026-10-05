package electricity.billing.system.billing;

import electricity.billing.system.model.TaxRate;

/**
 * Bill for normal (domestic) connections:
 * energy charge = units x cost per unit.
 */
public class NormalBill extends Bill {

    public NormalBill(String meterNo, String month, int units, TaxRate tax) {
        super(meterNo, month, units, tax);
    }

    @Override
    protected int calculateEnergyCharge() {
        return units * tax.getCostPerUnit();
    }

    @Override
    public String getBillType() {
        return "Normal";
    }
}
