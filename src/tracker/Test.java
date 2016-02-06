package tracker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class Test{
    

    public static void main(String[] args) {
        String msg = "";
        try{
       URL url = new URL("http://localhost:8000/tracker");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("email", "prappo");
        params.put("time", "1 : 2 : 3");
        params.put("project","Optimus prime");
        params.put("screenShot", "File.jpg");
       

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        for (int c = in.read(); c != -1; c = in.read()) {
//            System.out.print((char)c);
            msg += String.valueOf((char) c);
        }
        }
        catch(Exception ee){
            ee.printStackTrace();
        }
        System.out.println(msg);
//       Var.insert();
        
//        int tClicks = Var.mClicks;
//        int tDrag = Var.mDrags;
//        int tKey = Var.keyPressed;
//        String performance = Var.performance;
//        String time = Var.tTime;
//        String user = Var.tUserName;
//        String project = Var.tProject;
//        String screenShot =Var.tshot ;
//        
//        Connection c = null;
//        Statement stmt = null;
//        try {
//             Class.forName("org.sqlite.JDBC");
//      c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
//      c.setAutoCommit(false);
//      System.out.println("Opened database successfully");
//
//      stmt = c.createStatement();
//      String sql = "INSERT INTO backup (tClicks,tDrag,tKey,performance,time,user,project,screenShot) " +
//                   "VALUES ('"+tClicks+"','"+tDrag+"','"+tKey+"','"+performance+"','"+time+"','"+user+"','"+project+"','"+screenShot+"');"; 
//      stmt.executeUpdate(sql);
//
//     
//
//      stmt.close();
//      c.commit();
//      c.close();
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Something went wrong , couldn't take backup");
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.out.println(e.getMessage());
//            
//        }
       
    }
    
}
