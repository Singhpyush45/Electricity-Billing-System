package electricity.billing.system;

import javax.swing.*;
import java.awt.*;

/**
 * Program entry point (main method).
 *
 * MULTITHREADING: Splash implements the Runnable interface. A separate Thread
 * runs run(), which waits 7 seconds and then opens the Login window, while the
 * main thread is free and the splash window stays responsive.
 */
public class Splash extends JFrame implements Runnable {
    Thread t;
    Splash() {
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/elect.jpg"));
        Image i2 = i1.getImage().getScaledInstance(730, 550, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        add(image);
        
        setVisible(true);
        
        int x = 1;
        for (int i = 2; i < 600; i+=4, x+=1) {
            setSize(i + x, i);
            setLocation(700 - ((i + x)/2), 400 - (i/2));
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        t = new Thread(this, "splash-timer");   // 'this' is the Runnable
        t.start();                              // run() now executes on the new thread
        
        setVisible(true);
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(7000);
            // Swing windows must be created on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                setVisible(false);
                dispose();
                new Login();    // login frame
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void main(String[] args) {
        new Splash();
    }
}
