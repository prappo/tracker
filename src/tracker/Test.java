package tracker;
import java.awt.*;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Test extends JFrame { //the hook for the JFrame
    

    
    public Test() {
        
        setTitle("Class Human Implements Jokes"); //header title
        Font font = new Font("serif", Font.BOLD + Font.PLAIN, 24); //sets font and size
        setFont(font);
        setSize(600, 200); //sets size
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JLabel L1 = new JLabel("Why are Java programmers so low paid?"); //labels in the frame
        L1.setForeground(Color.BLUE); //sets color
        JLabel L2 = new JLabel("Because they have a tendency to...");
        L2.setForeground(Color.RED);
        JLabel L3 = new JLabel("Object[][]"); //because Object[][] == object to arrays == object to a raise
        L3.setForeground(Color.GREEN);
        
        
        getContentPane().add(L1); //adds label to the Pane
        L1.setAlignmentX(Component.CENTER_ALIGNMENT); //centers the text
        getContentPane().add(L2);
        L2.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(L3);
        L3.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    public static void main(String[] args) throws MalformedURLException, IOException {
        
        URL oracle = new URL("http://1067d6b8.ngrok.io/test");
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));
        String data = "";
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            data += inputLine;
        in.close();
        System.out.println(data.replace("[", ""));
        
    }
}