package electricity.billing.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import electricity.billing.system.billing.Bill;
import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.exception.EBSException;
import electricity.billing.system.model.Customer;
import electricity.billing.system.service.BillingService;
import electricity.billing.system.util.BackgroundTask;
import electricity.billing.system.util.MessageUtil;
import electricity.billing.system.util.Months;
import electricity.billing.system.util.Validator;
import java.util.List;
import java.util.Map;

public class CalculateBill extends JFrame implements ActionListener{

    JTextField tfname, tfaddress, tfstate, tfunits, tfemail, tfphone;
    JButton next, cancel;
    JLabel lblname, labeladdress;
    Choice meternumber, cmonth;
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BillingService billingService = new BillingService();

    CalculateBill() {
        setSize(700, 500);
        setLocation(400, 150);
        
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBackground(new Color(173, 216, 230));
        add(p);
        
        JLabel heading = new JLabel("Calculate Electricity Bill");
        heading.setBounds(100, 10, 400, 25);
        heading.setFont(new Font("Tahoma", Font.PLAIN, 24));
        p.add(heading);
        
        JLabel lblmeternumber = new JLabel("Meter Number");
        lblmeternumber.setBounds(100, 80, 100, 20);
        p.add(lblmeternumber);
        
        meternumber = new Choice();
        
        meternumber.setBounds(240, 80, 200, 20);
        p.add(meternumber);
        
        JLabel lblmeterno = new JLabel("Name");
        lblmeterno.setBounds(100, 120, 100, 20);
        p.add(lblmeterno);
        
        lblname = new JLabel("");
        lblname.setBounds(240, 120, 100, 20);
        p.add(lblname);
        
        JLabel lbladdress = new JLabel("Address");
        lbladdress.setBounds(100, 160, 100, 20);
        p.add(lbladdress);
        
        labeladdress = new JLabel();
        labeladdress.setBounds(240, 160, 200, 20);
        p.add(labeladdress);
        
        meternumber.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                showCustomer(meternumber.getSelectedItem());
            }
        });

        // Load the meter numbers on a background thread, then fill the dropdown
        BackgroundTask.run(this, customerDAO::getAllMeterNumbers, (List<String> meters) -> {
            for (String m : meters) {
                meternumber.add(m);
            }
            if (!meters.isEmpty()) {
                showCustomer(meters.get(0));
            }
        });
        
        JLabel lblcity = new JLabel("Units Consumed");
        lblcity.setBounds(100, 200, 100, 20);
        p.add(lblcity);
        
        tfunits = new JTextField();
        tfunits.setBounds(240, 200, 200, 20);
        p.add(tfunits);
        
        JLabel lblstate = new JLabel("Month");
        lblstate.setBounds(100, 240, 100, 20);
        p.add(lblstate);
        
        cmonth = new Choice();
        cmonth.setBounds(240, 240, 200, 20);
        Months.fill(cmonth);
        p.add(cmonth);
        
        next = new JButton("Submit");
        next.setBounds(120, 350, 100,25);
        next.setBackground(Color.BLACK);
        next.setForeground(Color.WHITE);
        next.addActionListener(this);
        p.add(next);
        
        cancel = new JButton("Cancel");
        cancel.setBounds(250, 350, 100,25);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        p.add(cancel);
        
        setLayout(new BorderLayout());
        
        add(p, "Center");
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/hicon2.jpg"));
        Image i2 = i1.getImage().getScaledInstance(150, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        add(image, "West");
        
        getContentPane().setBackground(Color.WHITE);
        
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == next) {
            String meter = meternumber.getSelectedItem();
            String month = cmonth.getSelectedItem();

            try {
                if (meter == null) {
                    throw new EBSException("No customers found. Add a customer first.");
                }
                int units = Validator.parseUnits(tfunits.getText());   // may throw InvalidUnitsException

                // Returns a NormalBill or an IndustrialBill - we only see the parent type Bill
                Bill bill = billingService.generateBill(meter, month, units);

                StringBuilder msg = new StringBuilder();
                msg.append("Customer Bill Updated Successfully\n\n");
                msg.append("Bill Type: ").append(bill.getBillType()).append("\n");
                for (Map.Entry<String, Integer> charge : bill.getBreakdown().entrySet()) {
                    msg.append(charge.getKey()).append(": Rs ").append(charge.getValue()).append("\n");
                }
                msg.append("-----------------------------\n");
                msg.append("Total: Rs ").append(bill.calculateTotal());

                MessageUtil.showInfo(this, msg.toString());
                setVisible(false);
            } catch (EBSException e) {
                // InvalidUnitsException, DuplicateBillException or DatabaseException
                MessageUtil.showError(this, e.getMessage());
            }
        } else {
            setVisible(false);
        }
    }
    
    /** Shows the name and address of the selected customer. */
    private void showCustomer(String meterNo) {
        try {
            customerDAO.findById(meterNo).ifPresent((Customer c) -> {
                lblname.setText(c.getName());
                labeladdress.setText(c.getAddress());
            });
        } catch (EBSException e) {
            MessageUtil.showError(this, e);
        }
    }

    public static void main(String[] args) {
        new CalculateBill();
    }
}
