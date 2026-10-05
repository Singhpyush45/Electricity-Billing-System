package electricity.billing.system.billing;

import electricity.billing.system.model.TaxRate;

/**
 * Creates the correct Bill object from the meter's bill type.
 * The return type is the PARENT type (Bill), but the object inside is a
 * NormalBill or an IndustrialBill - the caller never needs to know which.
 */
public final class BillFactory {

    private BillFactory() {
        // utility class - no objects needed
    }

    public static Bill createBill(String billType, String meterNo, String month,
                                  int units, TaxRate tax) {
        // startsWith("indust") also matches the old misspelt value "Industial"
        if (billType != null && billType.trim().toLowerCase().startsWith("indust")) {
            return new IndustrialBill(meterNo, month, units, tax);
        }
        return new NormalBill(meterNo, month, units, tax);
    }
}
