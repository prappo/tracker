/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracker;

import java.sql.*;

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
    public static int hours;
    public static int minutes;
    public static int sec;
    public static int mClicks;
    public static int mDrags;
    public static int mPressed;
    public static int keyPressed;
    public static String tUrl = getTurl();
    public static String tPostUrl = getTpostUrl();
    public static String tUrlLogin = getLoginUrl();
    public static String imgFileName;
    public static String backupUserName = getUser();
    public static String tUserName;
    public static String tPasswrod;
    public static String tProject;
    public static String performance;
    public static String tTime;
    public static String tshot;
    public static String imgData;

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
            if(msg.equals("")){
                msg="";
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            msg = "";
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Can't get username");
        }
        return msg;
    }

}
