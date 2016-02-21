package tracker;

import java.io.File;
import com.github.sarxos.webcam.Webcam;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import static java.awt.SystemColor.window;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JToggleButton;
import java.util.*;
import java.text.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
//mouse tracking
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
//keyboard tracking
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
//importing my files
import tracker.Var;
import java.sql.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author prappo
 */
public class Dashboard extends javax.swing.JFrame implements NativeMouseInputListener, NativeKeyListener, ComponentListener {

    protected static Boolean badde = false;

    JFrame f = new JFrame("TimeCop");

    /**
     * Creates new form Dashboard
     */
    public Dashboard() throws MalformedURLException, AWTException, IOException {

        initComponents();
// init floating window 
        fww();
        ImageIcon img = new ImageIcon("icon.png");
        this.setIconImage(img.getImage()); // set icon

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE); // set default closing action

        // for floating window 
        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                /* code run when component hidden*/
                f.setVisible(true);
            }

            public void componentShown(ComponentEvent e) {
                /* code run when component shown */
                f.setVisible(false);
            }
        });

        trackerTray();
        //get geo info
        getGeo();

        name.setText(Var.tUserName);
        Var.firstClick = false;
        Var.timerPused = false;
        Var.timerResume = false;
        Var.timerStarted = false;
        Var.timerStop = false;
        Var.timerStart = false;
        Var.isStoped = false;

        Var.hours = 0;
        Var.minutes = 0;
        Var.sec = 0;
        //checking connection
        Thread checkConnection = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (Var.online) {
                            iconOnline.setEnabled(true);
                            iconOffline.setEnabled(false);
                        } else {
                            iconOnline.setEnabled(false);
                            iconOffline.setEnabled(true);
                        }

                        Thread.sleep(500);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        };
        if (Var.workOffline) {
            Var.offline = true;
            iconOffline.setEnabled(true);
        } else {
            checkConnection.start();
        }

        //sending data
        Thread sd = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (Var.timerStart) {
                        Var.tProject = txtProjects.getSelectedItem().toString();
                        Var.tTime = timerText.getText();
                        if (Var.workOffline) {
                            try {
                                offline();
                            } catch (IOException ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(null, "Something went working we can't save your working data");
                            }
                        } else {
                            try {
                                if (sendData().equals("success")) {
                                    status("Tracker sent data to client");
                                } else if (sendData().equals("error")) {
                                    JOptionPane.showMessageDialog(null, "We got error response");
                                } else {
//                                backup();
                                    status("Seomething went wrong , we couldn't sent data to client");
                                }
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
//                            backup();
                                status("Error #1 : Seomething went wrong , we can't sent data to client");
                            } catch (ProtocolException ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
//                            backup();
                                status("Error #2 : Seomething went wrong , we can't sent data to client");
                            } catch (IOException ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                                backup();
                                status("Error #3 : Seomething went wrong , we can't sent data to client");
                            }
                        }

                    }
                    try {
                        Thread.sleep(randomNumber());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        sd.start();

        // get projects name
        getProjects();
        setResizable(false);
//        takeScreenshot();
        trackerTimer();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        // Construct the example object.
        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeKeyListener(this);

    }

    private int randomNumber() {
        Random r = new Random();
        int Low = 5;
        int High = 10;
        int Result = r.nextInt(High - Low) + Low;
        return Result * 6000;
    }

    private int workdID() {
        Random id = new Random();
        int low = 100;
        int high = 900;
        int result = id.nextInt(high - low) + low;
        return result;
    }

    public void windowVisibal() {

        this.setVisible(true);

    }

    private void windowHide() {

        this.setVisible(false);

    }

    //get location
    private void getGeo() throws MalformedURLException, IOException {
        String sURL = "http://ip-api.com/json"; //just a string

        // Connect to the URL using java's native library
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 

        txtIp.setText(rootobj.get("query").getAsString());
        txtCountry.setText(rootobj.get("country").getAsString());
        txtCity.setText(rootobj.get("city").getAsString());
        txtRegion.setText(rootobj.get("regionName").getAsString());
    }

    // taking screenshot 
    private void takeScreenshot() {

        Thread tss = new Thread() {
            @Override
            public void run() {
                while (true) {

                    try {

                        shot();

                        Thread.sleep(randomNumber());

                    } catch (Exception cool) {
                        System.out.println(cool.getMessage());
                    }

                }

            }
        };
        tss.start();
    }

    public String sendData() throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException {
        shot();
        camShot();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        String dateAndTime = ft.format(dNow);
        String imgData = null;
        String camImgData = null;

        try {
            imgData = Base64.encodeFromFile(Var.imgFileName);
        } catch (Exception a) {
            status("Can't read screenshot image file");
        }
        try {
            camImgData = Base64.encodeFromFile(Var.camImgFileName);
        } catch (Exception b) {
            status("Can't read camshot image file");
        }

        Var.imgData = imgData;
        Var.camImgData = camImgData;
        URL url = new URL(Var.tPostUrl);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("email", Var.tUserName);
        params.put("password", Var.tPasswrod);
        params.put("time", timerText.getText());
        params.put("project", txtProjects.getSelectedItem());
        params.put("workId", Var.workID);
        params.put("screenshotFileName", Var.imgFileName);
        params.put("webcamFileName", Var.camImgFileName);
        params.put("keyboard", Var.keyPressed);
        params.put("drags", Var.mDrags);
        params.put("clicks", Var.mClicks);
        params.put("screenshot", Var.imgData);
        params.put("webcam", Var.camImgData);
        params.put("status", txtMyStatus.getText());
        params.put("ip", txtIp.getText());
        params.put("country", txtCountry.getText());
        params.put("city", txtCity.getText());
        params.put("localTime", dateAndTime);

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
        String msg = "";
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        for (int c = in.read(); c != -1; c = in.read()) {
//            System.out.print((char)c);
            msg += String.valueOf((char) c);
        }
        File dIf = new File(Var.imgFileName);
        if (msg.equals("success")) {
            if (dIf.delete()) {
                status("Screenshot deleted");
            }
            File dCiF = new File(Var.camImgFileName);
            if (dCiF.delete()) {
                status("CamShot deleted");
            }

        }
        return msg;

    }

    private void shot() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        if (Var.timerStart) {

            try {

                String fileName = new Date().getTime() + "Screenshot.png";

                BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                ImageIO.write(image, "png", new File(fileName));
                status("Screenshot taken at " + ft.format(dNow));
                jLabel9.setText("Screenshot:" + ft.format(dNow));
                System.out.println("Screenshot taken at " + ft.format(dNow));
                Var.imgFileName = fileName;
                imgView.setIcon(new ImageIcon(new ImageIcon(fileName).getImage().getScaledInstance(225, 123, Image.SCALE_DEFAULT)));

            } catch (Exception e) {
                status("Error while taking screenshot");
            }
        }
    }

    public void camShot() throws IOException {
        if (Var.webCam == true) {
            try {
                String fileName = new Date().getTime() + "Capmshot.png";
                Webcam webcam = Webcam.getDefault();
                webcam.open();
                BufferedImage image = webcam.getImage();
                ImageIO.write(image, "PNG", new File(fileName));
                Var.camImgFileName = fileName;
                status("WebCam shot taken");
                webcam.close();
            } catch (Exception ee) {
                status("webcam error , didn't found any webcam device");
                System.out.println(ee.getMessage());
            }
        }
    }

    protected PopupMenu createPopupMenu() {
        final PopupMenu popup = new PopupMenu();

        MenuItem StartItem = new MenuItem("Start");
        MenuItem StopItem = new MenuItem("Stop");
        MenuItem OpenItem = new MenuItem("Open");
        MenuItem HideItem = new MenuItem("Hide");
        MenuItem ExitItem = new MenuItem("Exit");
        // Add components to pop-up menu
        StartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Var.timerStart = true;
            }
        });

        StopItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Var.timerStart = false;
            }
        });

        OpenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                windowVisibal();
            }
        });
        HideItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowHide();
            }
        });
        ExitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

//        popup.add(StartItem);
//        popup.add(StopItem);
//        popup.addSeparator();
        popup.add(OpenItem);
        popup.add(HideItem);
        popup.addSeparator();
        popup.add(ExitItem);
        return popup;
    }

    protected void trackerTray() throws MalformedURLException, AWTException {
        final Frame frame = new Frame("");
        frame.setUndecorated(true);
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "SystemTray is not supported");
            System.out.println("SystemTray is not supported");
            return;
        }
        final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("icon.png"), "TimeCop");

        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        final PopupMenu popup = createPopupMenu();
        trayIcon.setPopupMenu(popup);
        trayIcon.displayMessage("TimeCop running", "You can start or stop you work by suing tray menu", TrayIcon.MessageType.INFO);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    frame.add(popup);
                    popup.show(frame, e.getXOnScreen(), e.getYOnScreen());
                }
            }
        });
        try {
            frame.setResizable(false);
            frame.setVisible(true);

            tray.add(trayIcon);

        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private void backup() {

        int tClicks = Var.mClicks;
        int tDrag = Var.mDrags;
        int tKey = Var.keyPressed;
        String performance = Var.performance;
        String time = Var.tTime;
        String user = Var.tUserName;
        String project = Var.tProject;
        String screenShot = Var.imgFileName;

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        String dateAndTime = ft.format(dNow);

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO backup (pass, workId, status, city, country, ip, camShot,tClicks,tDrag,tKey,performance,time,user,project,screenShot,sTime) "
                    + "VALUES ('" + Var.tPasswrod + "','" + Var.workID + "','" + txtMyStatus.getText() + "','" + txtCity.getText() + "','" + txtCountry.getText() + "','" + txtIp.getText() + "','" + Var.camImgFileName + "','" + tClicks + "','" + tDrag + "','" + tKey + "','" + performance + "','" + time + "','" + user + "','" + project + "','" + screenShot + "','" + dateAndTime + "');";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Something went wrong , couldn't take backup");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println(e.getMessage());

        }
    }

    public void offline() throws IOException {
        shot();
        camShot();

        int tClicks = Var.mClicks;
        int tDrag = Var.mDrags;
        int tKey = Var.keyPressed;
        String performance = Var.performance;
        String time = Var.tTime;
        String user = Var.tUserName;
        String project = Var.tProject;
        String screenShot = Var.imgFileName;

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        String dateAndTime = ft.format(dNow);

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Tracker.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO backup (pass, workId, status, city, country, ip, camShot,tClicks,tDrag,tKey,performance,time,user,project,screenShot,sTime) "
                    + "VALUES ('" + Var.tPasswrod + "','" + Var.workID + "','" + txtMyStatus.getText() + "','" + txtCity.getText() + "','" + txtCountry.getText() + "','" + txtIp.getText() + "','" + Var.camImgFileName + "','" + tClicks + "','" + tDrag + "','" + tKey + "','" + performance + "','" + time + "','" + user + "','" + project + "','" + screenShot + "','" + dateAndTime + "');";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
            status("Data saved");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Something went wrong , couldn't take backup");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println(e.getMessage());
            status("Can't save data");
            JOptionPane.showMessageDialog(null, "Ops ! , can't save data");

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        timerText = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtProjects = new javax.swing.JComboBox();
        btnTimer = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        iconOnline = new javax.swing.JLabel();
        iconOffline = new javax.swing.JLabel();
        imgView = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtKeyboard = new javax.swing.JLabel();
        txtMouse = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        Status = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        projectDetails = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtStatus = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtMyStatus = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtIp = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtCountry = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCity = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        logOut = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        setTitle("Time Cop [ Dashboard ]");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel4.setText("Copyright © 2016 VarDump");

        jLabel1.setText("Welcome");

        name.setText("prappo");

        timerText.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        timerText.setText("0:0:0");

        jLabel2.setText("Hou");

        jLabel3.setText("Min");

        jLabel5.setText("Sec");

        btnTimer.setText("Start");
        btnTimer.setName("btnTimer"); // NOI18N
        btnTimer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnTimerStateChanged(evt);
            }
        });
        btnTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimerActionPerformed(evt);
            }
        });
        btnTimer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnTimerKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnTimerKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timerText, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addComponent(name))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timerText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(txtProjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTimer)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray, 3));

        iconOnline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tracker/assets/img/dot_green.png"))); // NOI18N
        iconOnline.setText("Online");
        iconOnline.setEnabled(false);

        iconOffline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tracker/assets/img/red_dot.png"))); // NOI18N
        iconOffline.setText("Offline");
        iconOffline.setEnabled(false);

        imgView.setText("Latest screenshot");

        txtKeyboard.setText("Keyboard Activity");

        txtMouse.setText("Mouse Acivity");

        jLabel9.setText("Screenshot Status");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtKeyboard)
                            .addComponent(txtMouse))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(txtKeyboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMouse)
                .addGap(12, 12, 12)
                .addComponent(jLabel9)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imgView, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(iconOffline)
                                .addGap(39, 39, 39)
                                .addComponent(iconOnline)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iconOnline)
                    .addComponent(iconOffline))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imgView, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        projectDetails.setEditable(false);
        projectDetails.setColumns(20);
        projectDetails.setRows(5);
        jScrollPane2.setViewportView(projectDetails);

        Status.addTab("Status", jScrollPane2);

        txtStatus.setEditable(false);
        txtStatus.setColumns(20);
        txtStatus.setRows(5);
        jScrollPane1.setViewportView(txtStatus);

        Status.addTab("Logs", jScrollPane1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Status)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Status, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        txtMyStatus.setColumns(20);
        txtMyStatus.setRows(5);
        txtMyStatus.setToolTipText("Type your Current status");
        jScrollPane3.setViewportView(txtMyStatus);

        jLabel10.setText("Your Status");

        jLabel8.setText("Your IP");

        txtIp.setText("127.0.0.1");

        jLabel11.setText("Country");

        txtCountry.setText("offline");

        jLabel13.setText("City");

        txtCity.setText("offline");

        jLabel15.setText("Region");

        txtRegion.setText("offline");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8))
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCountry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIp)
                            .addComponent(txtRegion))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtIp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCountry))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRegion)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setText("File");

        jMenuItem3.setText("Stop Work");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        logOut.setText("Logout");
        logOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutActionPerformed(evt);
            }
        });
        jMenu1.add(logOut);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Settings");

        jMenuItem2.setText("Settings");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Backups");

        jMenuItem4.setText("Show Backups");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4))
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(24, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // simulate a time consuming task
    private void trackerTimer() {

        Thread timer;
        timer = new Thread() {

            @Override
            public void run() {
                while (true) {

                    if (Var.timerStart) {
                        try {
                            Var.sec++;
                            if (Var.sec == 59) {
                                Var.sec = 0;
                                Var.minutes++;
                            }
                            if (Var.minutes == 59) {
                                Var.minutes = 0;
                                Var.hours++;
                            }
                            Thread.sleep(1000);
                        } catch (Exception cool) {
                            System.out.println(cool.getMessage());
                        }

                    }
                    timerText.setText(Var.hours + ":" + Var.minutes + ":" + Var.sec);
                }

            }
        };

        timer.start();
    }

    private void btnTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimerActionPerformed
        txtProjects.setEnabled(false);
        JToggleButton btn = (JToggleButton) evt.getSource();
        if (btn.isSelected()) {
            Var.workID = workdID();
            btn.setText("Stop");

            status("Tracker Started");

            Var.timerStart = true;

            projectDetails.setText("Now you are working on \n" + (String) txtProjects.getSelectedItem());
            projectDetails.setText(projectDetails.getText() + "\n" + "Started at " + timerText.getText());

        } else {
            Var.timerStart = false;
            txtProjects.setEnabled(true);
            btn.setText("Start");
            timerText.setText("0:0:0");
            Var.hours = 0;
            Var.minutes = 0;
            Var.sec = 0;

            if (!Var.tProject.equals(txtProjects.getSelectedItem())) {
                Var.tProject = (String) txtProjects.getSelectedItem();
            }

            status("Tracker Stoped");

        }

    }//GEN-LAST:event_btnTimerActionPerformed

    public void status(String txt) {
        if (Var.timerStart) {
            txtStatus.setText(txtStatus.getText() + "\n" + txt);
        }
    }


    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btnTimerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnTimerStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_btnTimerStateChanged

    private void btnTimerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnTimerKeyPressed
        if (Var.timerStart) {
            txtStatus.setText(txtStatus.getText() + "\n" + "Pressed");
        }
    }//GEN-LAST:event_btnTimerKeyPressed

    private void btnTimerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnTimerKeyReleased
        // TODO add your handling code here:
        if (Var.timerStart) {
            txtStatus.setText(txtStatus.getText() + "\n" + "Relesed");
        }

    }//GEN-LAST:event_btnTimerKeyReleased

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        Var.timerStart = false;
        projectDetails.setEnabled(true);

        Var.timerStop = true;
        btnTimer.setSelected(false);
        btnTimer.setText("Start");
        timerText.setText("0:0:0");
        Var.hours = 0;
        Var.minutes = 0;
        Var.sec = 0;
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void logOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutActionPerformed
        if (Var.timerStart) {
            JOptionPane.showMessageDialog(null, "You have to stop your work first to logout \n To Stop Your work go to Menu File > Stop Work ");
        } else {
            Var.timerStart = false;
            this.dispose();

            new Tracker().setVisible(true);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_logOutActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        new Settings().setVisible(true);

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
//        new Backups().setVisible(true);
        Backups b = new Backups();

        b.setVisible(true);
//        b = null;
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        f.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_formFocusGained

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:

    }//GEN-LAST:event_formWindowOpened

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified

    }//GEN-LAST:event_formWindowDeiconified

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus

    }//GEN-LAST:event_formWindowGainedFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Dashboard().setVisible(true);

                } catch (MalformedURLException ex) {
                    Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AWTException ex) {
                    Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });

    }

    // for mouse tracking 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTabbedPane Status;
    public static javax.swing.JToggleButton btnTimer;
    public static javax.swing.JLabel iconOffline;
    public static javax.swing.JLabel iconOnline;
    public static javax.swing.JLabel imgView;
    public static javax.swing.JLabel jLabel1;
    public static javax.swing.JLabel jLabel10;
    public static javax.swing.JLabel jLabel11;
    public static javax.swing.JLabel jLabel13;
    public static javax.swing.JLabel jLabel15;
    public static javax.swing.JLabel jLabel2;
    public static javax.swing.JLabel jLabel3;
    public static javax.swing.JLabel jLabel4;
    public static javax.swing.JLabel jLabel5;
    public static javax.swing.JLabel jLabel6;
    public static javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabel8;
    public static javax.swing.JLabel jLabel9;
    public static javax.swing.JMenu jMenu1;
    public static javax.swing.JMenu jMenu2;
    public static javax.swing.JMenu jMenu3;
    public static javax.swing.JMenuBar jMenuBar1;
    public static javax.swing.JMenuItem jMenuItem1;
    public static javax.swing.JMenuItem jMenuItem2;
    public static javax.swing.JMenuItem jMenuItem3;
    public static javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JPanel jPanel1;
    public static javax.swing.JPanel jPanel2;
    public static javax.swing.JPanel jPanel3;
    public static javax.swing.JPanel jPanel4;
    public static javax.swing.JPanel jPanel5;
    public static javax.swing.JPanel jPanel6;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JScrollPane jScrollPane3;
    public static javax.swing.JMenuItem logOut;
    public static javax.swing.JLabel name;
    public static javax.swing.JTextArea projectDetails;
    public static javax.swing.JLabel timerText;
    public static javax.swing.JLabel txtCity;
    public static javax.swing.JLabel txtCountry;
    public static javax.swing.JLabel txtIp;
    public static javax.swing.JLabel txtKeyboard;
    public static javax.swing.JLabel txtMouse;
    public static javax.swing.JTextArea txtMyStatus;
    public static javax.swing.JComboBox txtProjects;
    public static javax.swing.JLabel txtRegion;
    public static javax.swing.JTextArea txtStatus;
    // End of variables declaration//GEN-END:variables
// for mouse tracking bro 

    @Override
    public void nativeMouseClicked(NativeMouseEvent nme) {
//          System.out.println("Mouse Clicked: " + nme.getClickCount());
        if (Var.timerStart) {
            txtMouse.setText("Mouse Clicked: " + nme.getClickCount());
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nme) {
//        System.out.println("Mouse Pressed: " + nme.getButton());
        if (Var.timerStart) {
            txtMouse.setText("Mouse Pressed: " + nme.getButton());
            Var.mClicks++;
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nme) {
//          System.out.println("Mouse Released: " + nme.getButton());
        if (Var.timerStart) {
            txtMouse.setText("Mouse Released: " + nme.getButton());
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nme) {
//        System.out.println("Mouse Moved: " + nme.getX() + ", " + nme.getY());
        if (Var.timerStart) {
            txtMouse.setText("Mouse Moved: " + nme.getX() + ", " + nme.getY());

        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nme) {
//       System.out.println("Mouse Dragged: " + nme.getX() + ", " + nme.getY());
        if (Var.timerStart) {
            txtMouse.setText("Mouse Dragged: " + nme.getX() + ", " + nme.getY());
            Var.mDrags++;
        }
    }

    // for keyboard tracking
    @Override
    public void nativeKeyPressed(NativeKeyEvent nke) {
//        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(nke.getKeyCode()));
        if (Var.timerStart) {
            txtKeyboard.setText("Key Pressed: " + NativeKeyEvent.getKeyText(nke.getKeyCode()));

        }

        if (nke.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nke) {
//         System.out.println("Key Released: " + NativeKeyEvent.getKeyText(nke.getKeyCode()));
        if (Var.timerStart) {
            txtKeyboard.setText("Key Released: " + NativeKeyEvent.getKeyText(nke.getKeyCode()));
            Var.keyPressed++;
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nke) {
        System.out.println("Key Typed: " + nke.getKeyText(nke.getKeyCode()));
        if (Var.timerStart) {
            txtKeyboard.setText("Key Typed: " + nke.getKeyText(nke.getKeyCode()));
        }
    }

    private void getProjects() throws IOException {
        try {
            String jsonData = Var.projects;
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(jsonData);
            JsonObject obj = element.getAsJsonObject(); //since you know it's a JsonObject
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
            String project = "";
            for (Map.Entry<String, JsonElement> entry : entries) {
                txtProjects.addItem(entry.getValue().toString().replace("\"", ""));
            }
        } catch (Exception ee) {
            System.out.println(ee.getMessage());
            JOptionPane.showMessageDialog(null, "Can't load your projects");
        }
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel txtTime;

    public void fww() {

        Dimension windowSize = new Dimension(250, 70);
        Dimension btnStartSize = new Dimension(50, 25);

        JButton btnStart = new JButton("Start");
        JLabel txtTimer = new JLabel("0:0:0");
        Thread gettimes = new Thread() {
            @Override
            public void run() {
                while (true) {
                    txtTimer.setText(Var.hours + ":" + Var.minutes + ":" + Var.sec);

                }
            }
        };
        gettimes.start();
        txtTimer.setFont(new java.awt.Font("Arial", 0, 24));
        JPanel panel = new JPanel();
        ImageIcon img = new ImageIcon("icon.png");
        f.setIconImage(img.getImage());

        JButton btnStop = new JButton("Stop");

        // actions 
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Var.timerStart = true;
                btnTimer.setSelected(true);
                btnTimer.setText("Stop");

            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Var.timerStart = false;
                btnTimer.setSelected(false);
                btnTimer.setText("Start");

            }
        });
        // check if hidden 
        f.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                windowVisibal();
            }

            public void componentShown(ComponentEvent e) {
                windowHide();
            }
        });

//        window.addComponentListener(new ComponentAdapter() {
//            public void componentHidden(ComponentEvent e) {
//                /* code run when component hidden*/
//            }
//
//            public void componentShown(ComponentEvent e) {
//                /* code run when component shown */
//            }
//        });
        f.setLayout(new FlowLayout(FlowLayout.RIGHT));
        f.setSize(windowSize);
        f.add(txtTimer);
        f.add(btnStart);
        f.add(btnStop);
        f.setResizable(false);
        f.setAlwaysOnTop(true);
        f.setVisible(false);

    }

    @Override
    public void componentResized(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void componentShown(ComponentEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
