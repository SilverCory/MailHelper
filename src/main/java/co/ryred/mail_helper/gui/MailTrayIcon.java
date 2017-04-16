package co.ryred.mail_helper.gui;

import co.ryred.mail_helper.MailHelperConfig;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by Cory Redmond on 14/04/2017.
 * ace@ac3-servers.eu
 */
public class MailTrayIcon {

    public static void show(MailHelperConfig config) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            ex.printStackTrace();
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);
        SwingUtilities.invokeLater(() -> createAndShowGUI(config));

    }

    private static void createAndShowGUI(MailHelperConfig config) {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage("/Bread_Pleasured.png", "Mail helper."));
        final SystemTray tray = SystemTray.getSystemTray();

        // Image stuffs.
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Mail helper");

        MenuItem exitItem = new MenuItem("Exit");
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(e -> MailTrayPopup.display(config));

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

    }

    //Obtain the image URL
    public static Image createImage(String path, String description) {
        URL imageURL = MailTrayIcon.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}