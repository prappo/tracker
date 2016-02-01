package tracker;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
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
import org.apache.commons.lang.time.StopWatch;
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

/**
 *
 * @author prappo
 */
public class Dashboard extends javax.swing.JFrame implements NativeMouseInputListener, NativeKeyListener {

    protected static Boolean badde = false;
    public StopWatch st = new StopWatch();

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {

        initComponents();
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
        checkConnection.start();
        String projects = "Optimus Prime,Niomika,Tracker";
        //sending data
        Thread sendData = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (sendData().equals("success")) {
                            status("Tracker sent you data to client");
                        }
                        Thread.sleep(randomNumber());
                    } catch (Exception ss) {
                        ss.printStackTrace();

                    }

                }
            }
        };

        sendData.start();

        String[] projectLists = projects.split(",");
        for (String pro : projectLists) {
            txtProjects.addItem(pro);
        }
//        name.setText(Var.tUserName);
        setResizable(false);
        takeScreenshot();
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
        return Result * 600;
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

    private String sendData() throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException {

        URL url = new URL(Var.tPostUrl);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user", Var.tUserName);
        params.put("time", timerText.getText());
        params.put("project", txtProjects.getSelectedItem());
        params.put("performance", "pending");
        params.put("screenShot", Var.imgFileName);
        params.put("tKey", Var.keyPressed);
        params.put("tDrag", Var.mDrags);
        params.put("tClicks", Var.mClicks);

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

            } catch (Exception e) {
                status("Error while taking screenshot");
            }
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

        jLabel1 = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        timerText = new javax.swing.JLabel();
        btnTimer = new javax.swing.JToggleButton();
        txtProjects = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        txtMouse = new javax.swing.JLabel();
        txtKeyboard = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        iconOnline = new javax.swing.JLabel();
        iconOffline = new javax.swing.JLabel();
        Status = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        projectDetails = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtStatus = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Welcome");

        name.setText("prappo");

        timerText.setFont(new java.awt.Font("Droid Sans Mono for Powerline", 0, 12)); // NOI18N
        timerText.setText("0 : 0 : 0");

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

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray, 3));

        txtMouse.setText("Mouse Acivity");

        txtKeyboard.setText("Keyboard Activity");

        jLabel7.setIcon(new javax.swing.ImageIcon("/home/prappo/NetBeansProjects/Tracker/src/tracker/assets/img/193fc323-e8b5-4552-8e76-313d114e6e67.png")); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon("/home/prappo/NetBeansProjects/Tracker/src/tracker/assets/img/98878b38-0eaa-4074-9342-f1470a78a69e_13.png")); // NOI18N

        jLabel9.setText("Screenshot Status");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        iconOnline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tracker/assets/img/dot_green.png"))); // NOI18N
        iconOnline.setText("Online");
        iconOnline.setEnabled(false);

        iconOffline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tracker/assets/img/red_dot.png"))); // NOI18N
        iconOffline.setText("Offline");
        iconOffline.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(27, 27, 27)
                                    .addComponent(txtMouse)
                                    .addGap(12, 12, 12))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtKeyboard)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(iconOffline)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(txtMouse)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKeyboard))
                .addGap(34, 34, 34)
                .addComponent(jLabel9)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        projectDetails.setEditable(false);
        projectDetails.setColumns(20);
        projectDetails.setRows(5);
        projectDetails.setEnabled(false);
        jScrollPane2.setViewportView(projectDetails);

        Status.addTab("Status", jScrollPane2);

        txtStatus.setEditable(false);
        txtStatus.setColumns(20);
        txtStatus.setRows(5);
        txtStatus.setEnabled(false);
        jScrollPane1.setViewportView(txtStatus);

        Status.addTab("Logs", jScrollPane1);

        jLabel4.setForeground(java.awt.Color.black);
        jLabel4.setText("Copyright © 2016 VarDump");

        jLabel2.setText("Hou");

        jLabel3.setText("Min");

        jLabel5.setText("Sec");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tracker/assets/img/clock.png"))); // NOI18N

        jMenu1.setText("File");

        jMenuItem3.setText("Stop Work");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

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
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Status)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addComponent(txtProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(124, 124, 124)
                                .addComponent(btnTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(35, 35, 35)
                                                .addComponent(jLabel3)
                                                .addGap(37, 37, 37)
                                                .addComponent(jLabel5))
                                            .addComponent(timerText)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(name))))
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 54, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(name))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timerText)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addGap(3, 3, 3)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtProjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTimer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Status, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(10, 10, 10))
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
                    timerText.setText(Var.hours + " : " + Var.minutes + " : " + Var.sec);
                }

            }
        };

        timer.start();
    }

    private void btnTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimerActionPerformed
        txtProjects.setEnabled(false);
        JToggleButton btn = (JToggleButton) evt.getSource();
        if (btn.isSelected()) {

            btn.setText("Stop");

            status("Tracker Started");

            Var.timerStart = true;

            projectDetails.setText("Now you are working on \n" + (String) txtProjects.getSelectedItem());
            projectDetails.setText(projectDetails.getText() + "\n" + "Started at " + timerText.getText());

        } else {
            Var.timerStart = false;

            btn.setText("Start");

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

    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
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
                new Dashboard().setVisible(true);

            }

        });

    }

    // for mouse tracking 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Status;
    private javax.swing.JToggleButton btnTimer;
    public static javax.swing.JLabel iconOffline;
    public static javax.swing.JLabel iconOnline;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JLabel name;
    private javax.swing.JTextArea projectDetails;
    private javax.swing.JLabel timerText;
    private javax.swing.JLabel txtKeyboard;
    private javax.swing.JLabel txtMouse;
    private javax.swing.JComboBox txtProjects;
    private javax.swing.JTextArea txtStatus;
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

}
