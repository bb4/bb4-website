/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Socket portion of client-server program using sockets.
 * Adapted from http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
 *
 * @author Barry Becker
 */
public class Client extends JFrame implements ActionListener {

    private static final String DEFAULT_HOST = "127.0.0.1"; /// "192.168.1.100";
    private PrintWriter out;
    private BufferedReader in;

    private JButton button;
    private JTextField textField;

    public Client() {
        initUI();
    }

    private void initUI() {
        JLabel text =  new JLabel("Text to send over socket:");
        JPanel panel;

        textField = new JTextField(40);

        button = new JButton("Send message");
        button.setToolTipText("Send data to server");
        button.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JPanel());
        buttonPanel.add(button);
        buttonPanel.add(new JPanel());

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        getContentPane().add(panel);
        panel.add("North", text);
        panel.add("Center", textField);
        panel.add("South", buttonPanel);
    }

    /**
     * @param event button click to transmit text.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == button) {
            //Send data over socket
            String text = textField.getText();
            out.println(text);
            textField.setText("");
            //Receive text from server
            try {
                String line = in.readLine();
                System.out.println("Text received:" + line);
            }
            catch (IOException e) {
                exceptionOccurred("Read failed", e);
            }
        }
    }

    public void createListenSocket() {
        try {
            Socket socket_ = new Socket(DEFAULT_HOST, Server.PORT);
            out = new PrintWriter(socket_.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
            System.out.println("create listen out_1 = "+ out);
        }
        catch (UnknownHostException e) {
            exceptionOccurred("Unknown host: "+ DEFAULT_HOST, e);
        }
        catch (IOException e) {
            exceptionOccurred("No I/O", e);
        }
        assert(out != null) : "Failed to create socket";
    }

    private static void exceptionOccurred(String msg, Throwable t) {
        System.out.println(msg);
        t.printStackTrace();
        System.exit(1);
    }

    public static void main(String[] args) {
        Client frame = new Client();
        frame.setTitle("Client Program");

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
        frame.createListenSocket();
    }
}
