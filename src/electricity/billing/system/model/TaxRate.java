package electricity.billing.system.model;

/**
 * Current rates and taxes (maps to the single row of the tax table).
 */
public class TaxRate {

    private final int costPerUnit;
    private final int meterRent;
    private final int serviceCharge;
    private final int serviceTax;
    private final int swacchBharatCess;
    private final int fixedTax;

    public TaxRate(int costPerUnit, int meterRent, int serviceCharge,
                   int serviceTax, int swacchBharatCess, int fixedTax) {
        this.costPerUnit = costPerUnit;
        this.meterRent = meterRent;
        this.serviceCharge = serviceCharge;
        this.serviceTax = serviceTax;
        this.swacchBharatCess = swacchBharatCess;
        this.fixedTax = fixedTax;
    }

    public int getCostPerUnit()      { return costPerUnit; }
    public int getMeterRent()        { return meterRent; }
    public int getServiceCharge()    { return serviceCharge; }
    public int getServiceTax()       { return serviceTax; }
    public int getSwacchBharatCess() { return swacchBharatCess; }
    public int getFixedTax()         { return fixedTax; }
}
