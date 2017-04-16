package co.ryred.mail_helper.gui;

import co.ryred.mail_helper.MailHelperConfig;
import co.ryred.mail_helper.enigma.Enigma;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by Cory Redmond on 14/04/2017.
 * ace@ac3-servers.eu
 */
public class MailTrayPopup extends JFrame {

    public static void display(MailHelperConfig config) {
        new MailTrayPopup(config);
    }

    public MailTrayPopup(MailHelperConfig config) throws HeadlessException {
        super("Mail Helper");

        setIconImage(MailTrayIcon.createImage("/Bread_Pleasured.png", "Mail helper."));
        setLayout(new GridBagLayout());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        GridBagConstraints c = new GridBagConstraints();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up suffixes.
        String[] suffixes = new String[config.getEmailSuffixes().length + 1];
        suffixes[0] = "";
        System.arraycopy(config.getEmailSuffixes(), 0, suffixes, 1, config.getEmailSuffixes().length);

        // Email suffix selector.
        JComboBox<String> emailSuffix = new JComboBox<>(suffixes);
        emailSuffix.setSelectedIndex(0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        add(emailSuffix, c);

        // Input.
        JTextField inputField = new JTextField(30);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        add(inputField, c);

        // Output
        JTextField outputField = new JTextField();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;
        add(outputField, c);
        outputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = ((JTextField) e.getComponent());
                field.select(0, field.getText().length());
            }
        });
        outputField.setEnabled(false); // Enabled is the one that changes.
        outputField.setEditable(false);

        // Crypt button.
        JButton cryptButton = new JButton("Crypt");
        cryptButton.addActionListener(e -> {

            String crypt = Enigma.crypt( config.getEnigmaSettings(), inputField.getText().trim() ).toLowerCase().trim();
            String suffix = emailSuffix.getSelectedItem().toString().trim();
            if(!suffix.isEmpty()) suffix = "_e@" + suffix;
            outputField.setText(crypt + suffix);
            outputField.setEnabled(true);

        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 3;
        add(cryptButton, c);

        // Copy button.
        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> {
            if(!outputField.getText().trim().isEmpty()) {
                StringSelection stringSelection = new StringSelection(outputField.getText().trim());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 3;
        add(copyButton, c);

        // Exit button.
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 3;
        add(exitButton, c);

        validate();
        repaint();
        pack();
        setVisible(true);

    }

}
