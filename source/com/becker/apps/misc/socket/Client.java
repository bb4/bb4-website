package com.becker.apps.misc.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private Socket socket_;
    private PrintWriter out_;
    private BufferedReader in_;

    private JButton button_;
    private JTextField textField_;

    public Client() {
        initUI();
    }

    private void initUI() {
        JLabel text =  new JLabel("Text to send over socket:");
        JPanel panel;

        textField_ = new JTextField(40);

        button_ = new JButton("Click Me");
        button_.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JPanel());
        buttonPanel.add(button_);
        buttonPanel.add(new JPanel());

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        getContentPane().add(panel);
        panel.add("North", text);
        panel.add("Center", textField_);
        panel.add("South", buttonPanel);
    }

    /**
     * @param event button click to transmit text.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == button_) {
            //Send data over socket
            String text = textField_.getText();
            out_.println(text);
            textField_.setText("");
            //Receive text from server
            try {
                String line = in_.readLine();
                System.out.println("Text received:" + line);
            }
            catch (IOException e) {
                exceptionOccurred("Read failed", e);
            }
        }
    }

    public void createListenSocket() {
        try {
            socket_ = new Socket(DEFAULT_HOST, Server.PORT);
            out_ = new PrintWriter(socket_.getOutputStream(), true);
            in_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
            System.out.println("create listen out_1 = "+ out_);
        }
        catch (UnknownHostException e) {
            exceptionOccurred("Unknown host: "+ DEFAULT_HOST, e);
        }
        catch (IOException e) {
            exceptionOccurred("No I/O", e);
        }
        assert(out_ != null) : "Failed to create socket";
    }

    private static void exceptionOccurred(String msg, Throwable t) {
        System.out.println(msg);
        t.printStackTrace();
        System.exit(1);
    }

    public static void main(String[] args) {
        Client frame = new Client();
        frame.setTitle("Client Program");
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);
        frame.createListenSocket();   
    }
}
