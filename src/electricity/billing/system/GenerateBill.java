package electricity.billing.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import electricity.billing.system.billing.Bill;
import electricity.billing.system.dao.BillDAO;
import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.dao.MeterDAO;
import electricity.billing.system.exception.DatabaseException;
import electricity.billing.system.model.BillKey;
import electricity.billing.system.model.BillRecord;
import electricity.billing.system.model.Customer;
import electricity.billing.system.model.Meter;
import electricity.billing.system.service.BillingService;
import electricity.billing.system.util.BackgroundTask;
import electricity.billing.system.util.Months;
import java.time.Year;
import java.util.Map;
import java.util.Optional;

public class GenerateBill extends JFrame implements ActionListener{

    String meter;
    JButton bill;
    Choice cmonth;
    JTextArea area;
    GenerateBill(String meter) {
        this.meter = meter;
        
        setSize(500, 800);
        setLocation(550, 30);
        
        setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        
        JLabel heading = new JLabel("Generate Bill");
        JLabel meternumber = new JLabel(meter);
        
        cmonth = new Choice();
        
        Months.fill(cmonth);
        
        area = new JTextArea(50, 15);
        area.setText("\n\n\t--------Click on the---------\n\t Generate Bill Button to get\n\tthe bill of the Selected Month");
        area.setFont(new Font("Senserif", Font.ITALIC, 18));
        
        JScrollPane pane = new JScrollPane(area);
        
        bill = new JButton("Generate Bill");
        bill.addActionListener(this);
        
        panel.add(heading);
        panel.add(meternumber);
        panel.add(cmonth);
        add(panel, "North");
        
        add(pane, "Center");
        add(bill, "South");
        
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        String month = cmonth.getSelectedItem();
        area.setText("\n\n\tLoading bill...");
        bill.setEnabled(false);

        // Build the bill text on a background thread, then show it on the screen
        BackgroundTask.run(this, () -> buildBillText(month), text -> {
            area.setText(text);
            area.setCaretPosition(0);
            bill.setEnabled(true);
        });
    }

    /** Collects data from 3 DAOs and formats the bill. Runs on the background thread. */
    private String buildBillText(String month) throws DatabaseException {
        StringBuilder sb = new StringBuilder();
        sb.append("\tReliance Power Limited\nELECTRICITY BILL GENERATED FOR THE MONTH\n\tOF ")
          .append(month).append(", ").append(Year.now()).append("\n\n\n");

        Optional<BillRecord> record = new BillDAO().findById(new BillKey(meter, month));
        if (record.isEmpty()) {
            sb.append("\n    No bill has been generated for ").append(month).append(" yet.");
            return sb.toString();
        }

        Optional<Customer> customer = new CustomerDAO().findById(meter);
        if (customer.isPresent()) {
            Customer c = customer.get();
            sb.append("\n    Customer Name: ").append(c.getName());
            sb.append("\n    Meter Number   : ").append(c.getMeterNo());
            sb.append("\n    Address             : ").append(c.getAddress());
            sb.append("\n    City                 : ").append(c.getCity());
            sb.append("\n    State                : ").append(c.getState());
            sb.append("\n    Email                : ").append(c.getEmail());
            sb.append("\n    Phone                 : ").append(c.getPhone());
            sb.append("\n---------------------------------------------------\n");
        }

        Optional<Meter> meterInfo = new MeterDAO().findById(meter);
        if (meterInfo.isPresent()) {
            Meter m = meterInfo.get();
            sb.append("\n    Meter Location: ").append(m.getLocation());
            sb.append("\n    Meter Type:     ").append(m.getType());
            sb.append("\n    Phase Code:        ").append(m.getPhaseCode());
            sb.append("\n    Bill Type:          ").append(m.getBillType());
            sb.append("\n    Days:                ").append(m.getDays());
            sb.append("\n---------------------------------------------------\n");
        }

        BillRecord r = record.get();
        Bill details = new BillingService().rebuildBill(r);   // NormalBill or IndustrialBill
        sb.append("\n    Current Month: ").append(r.getMonth());
        sb.append("\n    Units Consumed:     ").append(r.getUnits());
        sb.append("\n");
        for (Map.Entry<String, Integer> charge : details.getBreakdown().entrySet()) {
            sb.append("\n    ").append(charge.getKey()).append(": Rs ").append(charge.getValue());
        }
        sb.append("\n-------------------------------------------------------");
        sb.append("\n    Total Payable: Rs ").append(r.getTotalBill());
        sb.append("\n    Status: ").append(r.getStatus());
        sb.append("\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        new GenerateBill("");
    }
}
