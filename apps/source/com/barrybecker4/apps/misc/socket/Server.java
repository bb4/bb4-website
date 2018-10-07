/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.socket;

import com.barrybecker4.ui.components.ScrollingTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Multi threaded server for client-server application.
 * adapted from http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
 *
 * @author Barry Becker
 */
public class Server extends JFrame {

    public  static final int PORT = 4444;

    private ScrollingTextArea textArea;
    private ServerSocket server;

    Server() {
        initUI();

        setTitle("Server Program");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        pack();
        setVisible(true);
        listenSocket();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Text received over socket:");
        textArea = new ScrollingTextArea(20, 40);

        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        panel.add("Center", textArea);

        getContentPane().add(panel);
    }

    public void listenSocket() {
        try {
            server = new ServerSocket(PORT);
        }
        catch (IOException e) {
            System.out.println("Could not listen on port " + PORT);
            e.printStackTrace();
            System.exit(-1);
        }
        while (true) {
            ClientWorker w;
            try {
                w = new ClientWorker(server.accept(), textArea);
                // should use executor framework here.
                Thread t = new Thread(w);
                t.start();
            }
            catch (IOException e) {
                System.out.println("Accept failed: " + PORT);
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Objects created in run method are finalized when
     * program terminates and thread exits
     */
    @Override
    protected void finalize() {
        try {
            super.finalize();
            server.close();
        }
        catch (IOException e) {
            System.out.println("Could not close socket");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
