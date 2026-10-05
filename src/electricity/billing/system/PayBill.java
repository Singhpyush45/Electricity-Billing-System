package electricity.billing.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import electricity.billing.system.dao.BillDAO;
import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.exception.EBSException;
import electricity.billing.system.model.BillKey;
import electricity.billing.system.model.BillRecord;
import electricity.billing.system.model.Customer;
import electricity.billing.system.util.MessageUtil;
import electricity.billing.system.util.Months;
import java.util.Optional;

public class PayBill extends JFrame implements ActionListener{

    Choice cmonth;
    JButton pay, back;
    String meter;
    JLabel labelunits, labeltotalbill, labelstatus;
    private final BillDAO billDAO = new BillDAO();
    private BillRecord currentBill;      // bill of the selected month (null if none)

    PayBill(String meter) {
        this.meter = meter;
        setLayout(null);
        setBounds(300, 150, 900, 600);
        
        JLabel heading = new JLabel("Electricity Bill");
        heading.setFont(new Font("Tahoma", Font.BOLD, 24));
        heading.setBounds(120, 5, 400, 30);
        add(heading);
        
        JLabel lblmeternumber = new JLabel("Meter Number");
        lblmeternumber.setBounds(35, 80, 200, 20);
        add(lblmeternumber);
        
        JLabel meternumber = new JLabel("");
        meternumber.setBounds(300, 80, 200, 20);
        add(meternumber);
        
        JLabel lblname = new JLabel("Name");
        lblname.setBounds(35, 140, 200, 20);
        add(lblname);
        
        JLabel labelname = new JLabel("");
        labelname.setBounds(300, 140, 200, 20);
        add(labelname);
        
        JLabel lblmonth = new JLabel("Month");
        lblmonth.setBounds(35, 200, 200, 20);
        add(lblmonth);
        
        cmonth = new Choice();
        cmonth.setBounds(300, 200, 200, 20);
        Months.fill(cmonth);
        add(cmonth);
        
        JLabel lblunits = new JLabel("Units");
        lblunits.setBounds(35, 260, 200, 20);
        add(lblunits);
        
        labelunits = new JLabel("");
        labelunits.setBounds(300, 260, 200, 20);
        add(labelunits);
        
        JLabel lbltotalbill = new JLabel("Total Bill");
        lbltotalbill.setBounds(35, 320, 200, 20);
        add(lbltotalbill);
        
        labeltotalbill = new JLabel("");
        labeltotalbill.setBounds(300, 320, 200, 20);
        add(labeltotalbill);
        
        JLabel lblstatus = new JLabel("Status");
        lblstatus.setBounds(35, 380, 200, 20);
        add(lblstatus);
        
        labelstatus = new JLabel("");
        labelstatus.setBounds(300, 380, 200, 20);
        labelstatus.setForeground(Color.RED);
        add(labelstatus);
        
        meternumber.setText(meter);
        try {
            Optional<Customer> customer = new CustomerDAO().findById(meter);
            customer.ifPresent(c -> labelname.setText(c.getName()));
        } catch (EBSException e) {
            MessageUtil.showError(this, e);
        }
        loadBill(cmonth.getSelectedItem());
        
        cmonth.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ae) {
                loadBill(cmonth.getSelectedItem());
            }
        });
        
        
        pay = new JButton("Pay");
        pay.setBackground(Color.BLACK);
        pay.setForeground(Color.WHITE);
        pay.setBounds(100, 460, 100, 25);
        pay.addActionListener(this);
        add(pay);
        
        back = new JButton("Back");
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setBounds(230, 460, 100, 25);
        back.addActionListener(this);
        add(back);
        
        getContentPane().setBackground(Color.WHITE);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bill.png"));
        Image i2 = i1.getImage().getScaledInstance(600, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(400, 120, 600, 300);
        add(image);
        
        setVisible(true);
    }
    
    /** Shows the bill of the selected month, or "No Bill" if it has not been generated. */
    private void loadBill(String month) {
        try {
            currentBill = billDAO.findById(new BillKey(meter, month)).orElse(null);
        } catch (EBSException e) {
            currentBill = null;
            MessageUtil.showError(this, e);
        }
        if (currentBill == null) {
            labelunits.setText("-");
            labeltotalbill.setText("-");
            labelstatus.setText("No Bill");
        } else {
            labelunits.setText(String.valueOf(currentBill.getUnits()));
            labeltotalbill.setText(String.valueOf(currentBill.getTotalBill()));
            labelstatus.setText(currentBill.getStatus());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == pay) {
            if (currentBill == null) {
                MessageUtil.showError(this, "There is no bill for " + cmonth.getSelectedItem() + ".");
                return;
            }
            if (currentBill.isPaid()) {
                MessageUtil.showInfo(this, "This bill is already paid.");
                return;
            }
            try {
                billDAO.markAsPaid(meter, currentBill.getMonth());
                setVisible(false);
                new Paytm(meter);
            } catch (EBSException e) {
                MessageUtil.showError(this, e.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args){
        new PayBill("");
    }
}
