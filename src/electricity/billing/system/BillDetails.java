package electricity.billing.system;

import javax.swing.*;
import java.awt.*;
import electricity.billing.system.dao.BillDAO;
import electricity.billing.system.model.BillRecord;
import electricity.billing.system.util.BackgroundTask;
import electricity.billing.system.util.TableModelBuilder;

public class BillDetails extends JFrame{

    static final String[] COLUMNS = {"Meter No", "Month", "Units", "Total Bill (Rs)", "Status"};

    BillDetails(String meter) {
        
        setSize(700, 650);
        setLocation(400, 150);
        
        getContentPane().setBackground(Color.WHITE);
        
        JTable table = new JTable();
        
        // Query runs on a background thread; the table is filled when it finishes
        BackgroundTask.run(this, () -> new BillDAO().findByMeter(meter), bills ->
                table.setModel(TableModelBuilder.build(COLUMNS, bills, (BillRecord b) -> new Object[] {
                        b.getMeterNo(), b.getMonth(), b.getUnits(), b.getTotalBill(), b.getStatus()
                })));
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(0, 0, 700, 650);
        add(sp);
        
        setVisible(true);
    }

    public static void main(String[] args) {
        new BillDetails("");
    }
}
