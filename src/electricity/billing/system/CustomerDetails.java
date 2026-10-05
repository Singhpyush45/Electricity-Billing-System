package electricity.billing.system;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import electricity.billing.system.dao.CustomerDAO;
import electricity.billing.system.model.Customer;
import electricity.billing.system.util.BackgroundTask;
import electricity.billing.system.util.MessageUtil;
import electricity.billing.system.util.TableModelBuilder;
import java.awt.print.PrinterException;

public class CustomerDetails extends JFrame implements ActionListener{

    static final String[] COLUMNS = {"Name", "Meter No", "Address", "City", "State", "Email", "Phone"};

    JTable table;
    JButton print;
    
    CustomerDetails(){
        super("Customer Details");
        
        setSize(1200, 650);
        setLocation(200, 150);
        
        table = new JTable();
        
        // Load List<Customer> on a background thread, then show it in the table
        BackgroundTask.run(this, new CustomerDAO()::findAll, customers ->
                table.setModel(TableModelBuilder.build(COLUMNS, customers, (Customer c) -> new Object[] {
                        c.getName(), c.getMeterNo(), c.getAddress(), c.getCity(),
                        c.getState(), c.getEmail(), c.getPhone()
                })));
        
        JScrollPane sp = new JScrollPane(table);
        add(sp);
        
        print = new JButton("Print");
        print.addActionListener(this);
        add(print, "South");
        
        
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        try {
            table.print();
        } catch (PrinterException e) {
            MessageUtil.showError(this, "Could not print: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new CustomerDetails();
    }
}
