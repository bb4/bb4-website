package com.becker.game.online;


import java.io.*;
import java.net.*;
import java.security.*;

/**
 * Opens a socket to the Game server so we can talk to it.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class ServerConnection {

    private static final String DEFAULT_HOST = "127.0.0.1"; // localhost // "192.168.1.100";
    private Socket socket_;

    private ObjectOutputStream oStream_;
    private ObjectInputStream iStream_;

    private boolean isConnected_ = false;

    private OnlineChangeListener changeListener_;

    /**
     * @param port to open the connection on.
     */
    public ServerConnection(int port, OnlineChangeListener listener) {
        System.out.println("in ServerConnection constructor");
        changeListener_ = listener;
        createListenSocket(port);
    }

    /**
     * @return true if we have a live connection to the server.
     */
    public boolean isConnected() {
        return isConnected_;
    }

    /**
     * @param cmd object to serialize over the wire.
     */
    public void sendCommand(GameCommand cmd)  {

        try {
            // Send data over socket
            oStream_.writeObject(cmd);
            oStream_.flush();

            // Receive obj from server. Should response have success or error condition?
            // GameCommand receivedCmd = (GameCommand) iStream_.readObject();
            // System.out.println("Received:" + receivedCmd);
        }
        catch (IOException e) {
            exceptionOccurred("Read failed.", e);
        }
    }

    /**
     * Open a socket to the server to listen for, and send information.
     * @param port to open the connection on.
     */
    public void createListenSocket(int port) {
        try {
            isConnected_ = false;
            System.out.println("Attempting to connect to Server=" + DEFAULT_HOST + " port="+port);
            socket_ = new Socket(DEFAULT_HOST, port);
            oStream_ = new ObjectOutputStream(socket_.getOutputStream());
            iStream_ = new ObjectInputStream(socket_.getInputStream());

            // create a thread to listen for updates from the server.
            UpdateWorker w = new UpdateWorker(iStream_);
            System.out.println(" .....");
            Thread t = new Thread(w);
            t.start();

            System.out.println("connected.");
            isConnected_ = true;
        }
        catch (ConnectException e) {
             System.out.println("failed to get connection. " +
                                "Probably because the server is not running or accessable. " +
                                "Play local game.");
            //e.printStackTrace();
            isConnected_ = false;
        }
        catch (UnknownHostException e) {
            exceptionOccurred("Unknown host: "+ DEFAULT_HOST, e);
        }
        catch (IOException e) {
            exceptionOccurred("No I/O", e);
        }
        catch (AccessControlException e) {
            System.out.println("Failed to createListenSocket. \n"
                               +"You don't have permission to open a socket to "
                               + DEFAULT_HOST + " in the current context.");
            isConnected_ = false;
        }
    }

    /**
     * Request an initial update when we enter the room with the game tables
     */
    public void enterRoom() {
        sendCommand(new GameCommand(GameCommand.Name.ENTER_ROOM, ""));
    }

    /**
     * Tell the server to add another game table to the list that is available.
     * @param newTable  to add.
     */
    public void addGameTable(OnlineGameTable newTable) {
        sendCommand(new GameCommand(GameCommand.Name.ADD_TABLE , newTable));
    }

    private void exceptionOccurred(String msg, Throwable t) {
        isConnected_ = false;
        System.out.println(msg);
        t.printStackTrace();
        System.exit(1);
    }


    /**
     * A client worker is created for each client player connection to this server.
     */
    private class UpdateWorker implements Runnable {

        private ObjectInputStream inputStream_;

        UpdateWorker(ObjectInputStream input) {
            this.inputStream_ = input;
        }

        public void run() {

            while (true) {
                try {
                    GameCommand cmd = (GameCommand) inputStream_.readObject();

                    // we got a change to the tables on the server, update our client listener
                    changeListener_.handleServerUpdate(cmd);

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
