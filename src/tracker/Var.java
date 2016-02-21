/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import tracker.Base64;
import tracker.Dashboard;

/**
 *
 * @author prappo
 */
public class Var {

    public static Boolean firstClick;
    public static Boolean timerStarted;
    public static Boolean timerPused;
    public static Boolean timerStop;
    public static Boolean timerResume;
    public static Boolean shotEnable;
    public static Boolean timerStart;
    public static Boolean isStoped;
    public static Boolean online;
    public static Boolean offline;
    public static Boolean trackerHide;
    public static Boolean webCam = camOption();
    public static Boolean opened;
    public static Boolean show;
    public static Boolean workOffline = false;
    public static int hours;
    public static int minutes;
    public static int sec;
    public static int mClicks;
    public static int mDrags;
    public static int mPressed;
    public static int keyPressed;
    public static int workID;
    public static String tUrl = getTurl();
    public static String tPostUrl = getTpostUrl();
    public static String tUrlLogin = getLoginUrl();
    public static String imgFileName;
    public static String camImgFileName;
    public static String backupUserName = getUser();
    public static String tUserName = getUser();
    public static String tPasswrod = getPass();
    public static String tProject;
    public static String performance;
    public static String tTime;
    public static String tshot;
    public static String imgData;
    public static String camImgData;
    public static String tProjectsUrl = "http://42951068.ngrok.io/projects";
    public static String projects = "noting";
//    public static String tProjects = getProjects();
    public static ArrayList<String> ids = getIds();
    
   
    public static String getSettings(String fieldName){
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                String tUrl = rs.getString(fieldName);

                msg = tUrl;
                System.out.println("Url is " + tUrl);
                System.out.println("ID is : " + rs.getString("id"));

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return msg;
    }
    

    public static String getTurl() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                String tUrl = rs.getString("tUrl");

                msg = tUrl;
                System.out.println("Url is " + tUrl);
                System.out.println("ID is : " + rs.getString("id"));

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return msg;
    }

    public static String getTpostUrl() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                String tPostUrl = rs.getString("tPostUrl");

                msg = tPostUrl;

                System.out.println("PostUrl is : " + tPostUrl);

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return msg;
    }

    public static String getLoginUrl() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                String tUrl = rs.getString("tLoginUrl");

                msg = tUrl;
                System.out.println("Login Url " + tUrl);

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return msg;

    }

    public static String getUser() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user;");
            while (rs.next()) {

                String tUrl = rs.getString("USERNAME");

                msg = tUrl;

            }
            if (msg.equals("")) {
                msg = "";
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            msg = "";
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Can't get username");
            System.out.println(e.getMessage());
        }
        return msg;
    }

    public static String history() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM backup;");
            while (rs.next()) {

                String tUrl = rs.getString("tLoginUrl");

                msg = tUrl;
                System.out.println("Login Url " + tUrl);

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return msg;
    }

    public static String sync() throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException {

        Connection dr = null;
        Statement stmt = null;
        String msg = "";
        String ID = "";
        try {
            Class.forName("org.sqlite.JDBC");
            dr = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            dr.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = dr.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM backup;");
            while (rs.next()) {

                ////////////////////data fetching start/////////////////////
                String imgData = Base64.encodeFromFile(rs.getString("screenShot"));
                String camImgData = Base64.encodeFromFile(rs.getString("camShot"));
                ID = rs.getString("id");
                URL url = new URL(Var.tPostUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("email", rs.getString("user"));
                params.put("time", rs.getString("time"));
                params.put("project", rs.getString("project"));
                params.put("workId", rs.getString("workId"));
                params.put("screenshotFileName", rs.getString("screenShot"));
                params.put("webcamFileName", rs.getString("camShot"));
                params.put("keyboard", rs.getString("tKey"));
                params.put("drags", rs.getString("tDrag"));
                params.put("clicks", rs.getString("tClicks"));
                params.put("screenshot", imgData);
                params.put("webcam", camImgData);
                params.put("status", rs.getString("status"));
                params.put("ip", rs.getString("ip"));
                params.put("country", rs.getString("country"));
                params.put("city", rs.getString("city"));
                params.put("localTime", rs.getString("sTime"));

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
                String postMsg = "";
                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                System.out.println("Status starting : ");

                for (int c = in.read(); c != -1; c = in.read()) {
                    System.out.print((char) c);
                    postMsg += String.valueOf((char) c);

                }

                System.out.println("Status end");
                File dIf = new File(rs.getString("screenShot"));
                File dCiF = new File(rs.getString("camShot"));
                if (postMsg.equals("success")) {
                    System.out.println("Trying to delete screenshots and camshots");
                    if (dIf.delete()) {
                        System.out.println("Screenshot deleted");
                    } else {
                        System.out.println("can't delete screenshot");
                    }

                    if (dCiF.delete()) {
                        System.out.println("CamShot deleted");
                    } else {
                        System.out.println("Can't delte camshot");
                    }
                    System.out.println("Deleting job done");

                }
//                return postMsg;
                if (postMsg.equals("success")) {
                    if (deleteBackup(ID).equals("success")) {
                        System.out.println("Backup data deleted");
                    }
                }
                ////////////////////data fetching end//////////////////////
                System.out.println(rs.getString("time"));

            }
            if (msg.equals("")) {
                msg = "";
            }
            rs.close();
            stmt.close();
            dr.close();
            msg = "success";

        } catch (Exception e) {
            msg = "";
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
        return msg;

    }

    public void refresh() {
        tUrl = getTurl();
        tPostUrl = getTpostUrl();
        tUrlLogin = getLoginUrl();
        backupUserName = getUser();
        webCam = camOption();

    }

    public static String deleteBackup(String id) {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "DELETE from backup where id='" + id + "'";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
            System.out.println("Deleted data from backup");
            msg = "success";
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Something went wrong , we can't delete data form database");
            msg = "error";
        }
        return msg;
    }

    public static ArrayList<String> getIds() {
        ArrayList<String> ar = new ArrayList<String>();
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM backup;");

            while (rs.next()) {
                ar.add(rs.getString("id"));

            }

            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {

            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Ops , Something went wrong . error #111");
            ar.add("nothing");
        }
        return (ar);

    }

    public static Boolean camOption() {
        Connection c = null;
        Statement stmt = null;
        Boolean msg = false;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                String camop = rs.getString("webCam");

                if (camop.equals("yes")) {
                    msg = true;
                } else if (camop.equals("no")) {
                    msg = false;
                } else {
                    msg = false;
                }

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            msg = false;
        }
        return msg;
    }

    public static String getPass() {
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user;");
            while (rs.next()) {

                msg = rs.getString("PASSWORD");

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return msg;
    }

    public static void deleteBackup() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "DELETE  from backup";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
            System.out.println("Deleted data from backup");
            JOptionPane.showMessageDialog(null, "Success !");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Something went wrong , we can't delete data form database");
            JOptionPane.showMessageDialog(null, "Something went wrong , we can't delete data from delete");
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

//    public static String getProjects() throws MalformedURLException, IOException {
//        URL oracle = new URL(Var.tProjectsUrl);
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(oracle.openStream()));
//        String data = "";
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            data += inputLine;
//        }
//        in.close();
//        return data;
//    }
    
    
    public static void getProjects(String user, String pass){
        String msg = "";
        try {
            URL url = new URL(Var.tProjectsUrl);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("email", user);
            params.put("password", pass);

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

//            return msg;
            Var.projects = msg;
        } catch (Exception lol) {
            msg = "error";
            Var.projects = "nothing";
    }
        
    }
    
    public static String getCamSettings(){
        Connection c = null;
        Statement stmt = null;
        String msg = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings;");
            while (rs.next()) {

                msg = rs.getString("webCam");

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return msg;
    }
    
    public static void updateProjects(){
        
    }

}
