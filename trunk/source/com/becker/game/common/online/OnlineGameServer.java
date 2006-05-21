package com.becker.game.common.online;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * The abstract server for online gmaes.
 * @@ Long term this should not have a UI.
 *
 * Manages the tables for the game room.
 * Has a GameController for each table.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class OnlineGameServer extends JFrame
                                       implements OnlineGameServerInterface {

    protected JTextArea textArea_;
    protected ServerSocket server_;

    public OnlineGameServer() {
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
        openListenSocket();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Commands received over socket:");
        textArea_ = new JTextArea(20, 40);

        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        panel.add("Center", new JScrollPane(textArea_));

        getContentPane().add(panel);
    }

    public abstract int getPort();


    public void openListenSocket() {
        int port = getPort();
        try {
            server_ = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        while (true) {
            OnlineGameServer.ClientWorker w;
            try {
                w = new OnlineGameServer.ClientWorker(server_.accept(), textArea_);
                Thread t = new Thread(w);
                t.start();
            }
            catch (IOException e) {
                System.out.println("Accept failed: " + port);
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


    public boolean handleCommand(String cmdLine, StringBuffer response) {
        return false;
    }


    /**
     * A client worker is created for each client player connection to this server.
     */
    private static class ClientWorker implements Runnable {
        private Socket client_;
        private JTextArea text_;

        ClientWorker(Socket client, JTextArea textArea) {
            this.client_ = client;
            this.text_ = textArea;
        }

        public void run() {
            ObjectInputStream iStream = null;
            ObjectOutputStream oStream = null;
            try {
                iStream = new ObjectInputStream(client_.getInputStream());
                oStream = new ObjectOutputStream(client_.getOutputStream());
            }
            catch (IOException e) {
                System.out.println("in or out stream creation failed.");
                e.printStackTrace();
                System.exit(-1);
            }

            while (true) {
                try {
                    GameCommand cmd = (GameCommand) iStream.readObject();

                    //Send acknowledgment back to client
                    oStream.writeObject(new GameCommand("received", cmd));
                    text_.append(cmd.toString() + '\n');
                }
                catch (IOException e) {
                    System.out.println("Read failed.");
                    e.printStackTrace();
                    break;
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
