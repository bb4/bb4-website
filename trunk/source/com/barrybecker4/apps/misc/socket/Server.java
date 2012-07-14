/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Multi threaded server for client-server application.
 * adapted from http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
 *
 * @author Barry Becker
 */
public class Server extends JFrame {

    public  static final int PORT = 4444;

    private JTextArea textArea_;
    private ServerSocket server_;

    Server() {
        initUI();

        setTitle("Server Program");
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        addWindowListener(l);
        pack();
        setVisible(true);
        listenSocket();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Text received over socket:");
        textArea_ = new JTextArea(20, 40);

        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        panel.add("Center", textArea_);

        getContentPane().add(panel);
    }

    public void listenSocket() {
        try {
            server_ = new ServerSocket(PORT);
        }
        catch (IOException e) {
            System.out.println("Could not listen on port " + PORT);
            e.printStackTrace();
            System.exit(-1);
        }
        while (true) {
            ClientWorker w;
            try {
                w = new ClientWorker(server_.accept(), textArea_);
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
    protected void finalize() {
        try {
            super.finalize();
            server_.close();
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
        Server frame = new Server();

    }


    private static class ClientWorker implements Runnable {
        private Socket client_;
        private JTextArea text_;

        ClientWorker(Socket client, JTextArea textArea) {
            this.client_ = client;
            this.text_ = textArea;
        }

        public void run() {
            String line;
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(client_.getInputStream()));
                out = new PrintWriter(client_.getOutputStream(), true);
            }
            catch (IOException e) {
                System.out.println("in or out failed");
                e.printStackTrace();
                System.exit(-1);
            }

            while (true) {
                try {
                    line = in.readLine();
                     //Send data back to client
                    out.println("RECIEVED:" + line);
                    text_.append(line + '\n');
                }
                catch (IOException e) {
                    System.out.println("Read failed");
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
