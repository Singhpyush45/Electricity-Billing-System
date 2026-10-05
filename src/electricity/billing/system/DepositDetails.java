package electricity.billing.system;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import electricity.billing.system.dao.BillDAO;
import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.model.BillKey;
import electricity.billing.system.model.BillRecord;
import electricity.billing.system.util.BackgroundTask;
import electricity.billing.system.util.MessageUtil;
import electricity.billing.system.util.Months;
import electricity.billing.system.util.TableModelBuilder;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class DepositDetails extends JFrame implements ActionListener{

    static final String[] COLUMNS = {"Meter No", "Month", "Units", "Total Bill (Rs)", "Status"};

    Choice meternumber, cmonth;
    JTable table;
    JButton search, print, showAll;
    private final BillDAO billDAO = new BillDAO();
    
    DepositDetails(){
        super("Deposit Details");
        
        setSize(700, 700);
        setLocation(400, 100);
        
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        JLabel lblmeternumber = new JLabel("Search By Meter Number");
        lblmeternumber.setBounds(20, 20, 150, 20);
        add(lblmeternumber);
        
        meternumber = new Choice();
        meternumber.setBounds(180, 20, 150, 20);
        add(meternumber);
        
        BackgroundTask.run(this, new CustomerDAO()::getAllMeterNumbers, meters -> {
            for (String m : meters) {
                meternumber.add(m);
            }
        });
        
        JLabel lblmonth = new JLabel("Search By Month");
        lblmonth.setBounds(400, 20, 100, 20);
        add(lblmonth);
        
        cmonth = new Choice();
        cmonth.setBounds(520, 20, 150, 20);
        Months.fill(cmonth);
        add(cmonth);
        
        table = new JTable();
        loadAllBills();
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(0, 100, 700, 600);
        add(sp);
        
        search = new JButton("Search");
        search.setBounds(20, 70, 80, 20);
        search.addActionListener(this);
        add(search);
        
        print = new JButton("Print");
        print.setBounds(120, 70, 80, 20);
        print.addActionListener(this);
        add(print);
        
        showAll = new JButton("Show All");
        showAll.setBounds(220, 70, 100, 20);
        showAll.addActionListener(this);
        add(showAll);
        
        setVisible(true);
    }

    private void loadAllBills() {
        BackgroundTask.run(this, billDAO::findAll, this::showBills);
    }

    private void showBills(List<BillRecord> bills) {
        table.setModel(TableModelBuilder.build(COLUMNS, bills, (BillRecord b) -> new Object[] {
                b.getMeterNo(), b.getMonth(), b.getUnits(), b.getTotalBill(), b.getStatus()
        }));
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == search) {
            String meter = meternumber.getSelectedItem();
            String month = cmonth.getSelectedItem();
            if (meter == null) {
                MessageUtil.showError(this, "No customers found.");
                return;
            }
            BackgroundTask.run(this, () -> billDAO.findById(new BillKey(meter, month)), result -> {
                List<BillRecord> rows = new ArrayList<>();
                result.ifPresent(rows::add);         // Optional -> 0 or 1 rows
                showBills(rows);
                if (rows.isEmpty()) {
                    MessageUtil.showInfo(this, "No bill found for meter " + meter + " in " + month + ".");
                }
            });
        } else if (ae.getSource() == showAll) {
            loadAllBills();
        } else {
            try {
                table.print();
            } catch (PrinterException e) {
                MessageUtil.showError(this, "Could not print: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new DepositDetails();
    }
}
