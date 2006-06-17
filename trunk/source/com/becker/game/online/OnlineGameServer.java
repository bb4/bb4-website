package com.becker.game.online;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

/**
 * The abstract server for online gmaes.
 * Long term this should probably not have a UI.
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

    // maintain a list of game tables
    OnlineGameTableList tables_;

    // keap a list of the threads that we have for each client connection.
    List<ClientWorker> clientConnections_;

    /**
     * Create the online game server to serve all online clinets.
     */
    public OnlineGameServer() {
        initUI();

        tables_ = new OnlineGameTableList();
        clientConnections_ = new LinkedList<ClientWorker>();

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


    /**
     * open a server socket to listen on our assigned port for
     * requests from clients. Updates will be broadcast on this socket.
     * Maintain a list of clientConnections that we need to broadcast to
     * when something changes.
     */
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
                w = new ClientWorker(server_.accept(), textArea_);
                Thread t = new Thread(w);
                clientConnections_.add(w);
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


    /**
     * Subclasses should override
     * @param cmdLine
     * @param response
     * @return true if successful
     */
    public boolean handleCommand(String cmdLine, StringBuffer response) {
        return false;
    }


    /**
     * A client worker is created for each client player connection to this server.
     */
    private class ClientWorker implements Runnable {
        private Socket clientConnection_;
        private JTextArea text_;

        private ObjectInputStream iStream_ = null;
        private ObjectOutputStream oStream_ = null;

        ClientWorker(Socket client, JTextArea textArea) {
            this.clientConnection_ = client;
            this.text_ = textArea;
        }

        public void run() {

            try {
                iStream_ = new ObjectInputStream(clientConnection_.getInputStream());
                oStream_ = new ObjectOutputStream(clientConnection_.getOutputStream());
            }
            catch (IOException e) {
                System.out.println("in or out stream creation failed.");
                e.printStackTrace();
                System.exit(-1);
            }


            try {
                // initial update to the game tables for someone entering the room.
                update();

                while (true) {

                    GameCommand cmd = (GameCommand) iStream_.readObject();

                    // we got a change to the tables, update internal structure and broadcast new list.
                    processCmd(cmd);

                    for (ClientWorker w : clientConnections_) {
                        w.update();
                    }

                    //Send acknowledgment back to client
                    //oStream.writeObject(new GameCommand("received", cmd));
                    text_.append(cmd.toString() + '\n');
                }

            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Read failed.");
                e.printStackTrace();
            }

            System.out.println("Connection closed removing thread");
            clientConnections_.remove(this);
        }

        /**
         * Update our internal game table list given the cmd from the client.
         * @param cmd to process.
         */
        private void processCmd(GameCommand cmd) throws IOException {
            switch (cmd.getName()) {
                case ADD_TABLE :
                    addTable((OnlineGameTable) cmd.getArgument());
                    break;
            }
        }

        private void addTable(OnlineGameTable table) {

            // if the table we are adding has the same name as an existing table change it to something unique
            String uniqueName = verifyUniqueName(table.getName());
            table.setName(uniqueName);

            tables_.add(table);
        }

        /**
         * @param name
         * @return a unique name if not unique already
         */
        private String verifyUniqueName(String name) {

            int ct = 0;
            for (OnlineGameTable t : tables_) {
                if (t.getName().indexOf(name + '_') == 0) {
                    ct++;
                }
            }
            return name + '_' + ct;
        }

        /**
         * broadcast the current list of tables to all the online clients.
         */
        public void update() throws IOException {

            System.out.println("In OnlineGameServer: sending:"+tables_);

            // must reset the stream first, otherwise tables_ will always be the same as first sent.
            oStream_.reset();
            oStream_.writeObject(new GameCommand(GameCommand.Name.UPDATE_TABLES, tables_));
            oStream_.flush();
        }
    }

}
