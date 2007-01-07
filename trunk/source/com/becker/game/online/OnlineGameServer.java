package com.becker.game.online;

import com.becker.game.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

/**
 * The abstract server for online games.
 * Long term this should probably not have a UI.
 *
 * Manages the tables for the game room.
 * Has a GameController for each table.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public abstract class OnlineGameServer extends JFrame {

    protected JTextArea textArea_;
    protected ServerSocket server_;

    // processes server commands. May someday need sublassing.
    private ServerCommandProcessor cmdProcessor_;

    // keep a list of the threads that we have for each client connection.
    List<ClientWorker> clientConnections_;

    /**
     * Create the online game server to serve all online clients.
     */
    protected OnlineGameServer() {
        initUI();

        cmdProcessor_ = new ServerCommandProcessor();
        clientConnections_ = new LinkedList<ClientWorker>();

        openListenSocket();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Commands received over the socket:");
        textArea_ = new JTextArea(20, 40);

        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.add("North", label);
        panel.add("Center", new JScrollPane(textArea_));
        setTitle(getTitle());

        getContentPane().add(panel);

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        addWindowListener(l);
        pack();
        setVisible(true);
    }

    public abstract int getPort();


    /**
     * open a server socket to listen on our assigned port for
     * requests from clients. Updates will be broadcast on this socket.
     * Maintain a list of clientConnections corresponding to the players
     * that we need to broadcast to when something changes.
     */
    public void openListenSocket() {
        int port = getPort();
        try {
            server_ = new ServerSocket(port);
        }
        catch (IOException e) {
            GameContext.log(0, "Could not listen on port " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        while (true) {
            OnlineGameServer.ClientWorker w;
            try {
                // accept new connections from players wanting to join.
                w = new ClientWorker(server_.accept(), textArea_);
                Thread t = new Thread(w);
                clientConnections_.add(w);
                t.start();
            }
            catch (IOException e) {
                GameContext.log(0, "Accept failed: " + port);
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
            GameContext.log(0, "Could not close socket");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }


    /**
     * A client worker is created for each client player connection to this server.
     */
    private class ClientWorker implements Runnable {
        private Socket clientConnection_;
        private JTextArea text_;

        private ObjectInputStream iStream_;
        private ObjectOutputStream oStream_;

        ClientWorker(Socket client, JTextArea textArea) {
            clientConnection_ = client;
            text_ = textArea;
        }

        public void run() {

            try {
                iStream_ = new ObjectInputStream(clientConnection_.getInputStream());
                oStream_ = new ObjectOutputStream(clientConnection_.getOutputStream());
            }
            catch (IOException e) {
                GameContext.log(0, "in or out stream creation failed.");
                e.printStackTrace();
                System.exit(-1);
            }

            try {
                // initial update to the game tables for someone entering the room.
                update(new GameCommand(GameCommand.Name.UPDATE_TABLES, cmdProcessor_.getTables()));

                while (true) {

                    // recieve the serialized commands that are sent and process them.
                    GameCommand cmd = (GameCommand) iStream_.readObject();

                    // we got a change to the tables, update internal structure and broadcast new list.
                    GameCommand response = cmdProcessor_.processCmd(cmd);

                    for (ClientWorker w : clientConnections_) {
                        w.update(response);
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
                GameContext.log(0, "Read failed.");
                e.printStackTrace();
            }

            GameContext.log(1, "Connection closed removing thread");
            clientConnections_.remove(this);
        }

        /**
         * broadcast the current list of tables to all the online clients.
         */
        public void update(GameCommand response) throws IOException {

            GameContext.log(1, "OnlineGameServer: sending:"+cmdProcessor_.getTables());

            // must reset the stream first, otherwise tables_ will always be the same as first sent.
            oStream_.reset();
            //oStream_.writeObject(new GameCommand(GameCommand.Name.UPDATE_TABLES, cmdProcessor_.getTables()));
            oStream_.writeObject(response);
            oStream_.flush();
        }
    }

     /**
      * Implements OnlineGameServerInterface which is also implemented by GtpTesujiSoftGoServer.
      * not currently used, but I'm trying to have a consistent game server interface.
      * @param cmdLine command and its arguments in a form that can be parsed.
      * @param response the response from the server to be interpreted by the client.
      * @return true if successfully handled.
      *
     public boolean handleCommand(String cmdLine, StringBuffer response) {
         String[] cmdArray = StringUtils.tokenize(cmdLine);
         String cmdStr = cmdArray[0];
         boolean status = true;

         GameCommand cmd = new GameCommand(GameCommand.Name.valueOf(cmdStr), cmdStr);
         return processCmd(cmd);
     } */
}
