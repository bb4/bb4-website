package com.becker.game.common.online;


import com.becker.game.common.GameContext;
import com.becker.game.common.Player;
import com.becker.game.common.PlayerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens a socket to the Game server from the client so we can talk to it.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
public class ServerConnection implements IServerConnection {

    /** Hardcoded for now, but should be configurable. */
    private static final String DEFAULT_HOST = "127.0.0.1"; // localhost // "192.168.1.100";

    private ObjectOutputStream oStream_;

    private boolean isConnected_ = false;

    /** a list of things that want to hear about broadcasts form the server about changed game state. */
    private List<OnlineChangeListener> changeListeners_;

    /**
     * @param port to open the connection on.
     */
    public ServerConnection(int port) {
        changeListeners_ = new ArrayList<OnlineChangeListener>();
        createListenSocket(port);
    }

    /**
     * @return true if we have a live connection to the server.
     */
    public boolean isConnected() {
        return isConnected_;
    }

    public void addOnlineChangeListener(OnlineChangeListener listener) {
        changeListeners_.add(listener);
    }

    public void removeOnlineChangeListener(OnlineChangeListener listener) {
        changeListeners_.remove(listener);
    }

    /**
     * @param cmd object to serialize over the wire.
     */
    public void sendCommand(GameCommand cmd)  {

        try {
            // Send data over socket
            assert(oStream_!=null && cmd!=null): "No socket: oStream="+ oStream_ +" cmd=" + cmd;
            oStream_.writeObject(cmd);
            oStream_.flush();
        }
        catch (IOException e) {
            exceptionOccurred("Send failed.", e);
        }
    }

    /**
     * Open a socket to the server to listen for, and send information.
     * @param port to open the connection on.
     */
    void createListenSocket(int port) {
        try {
            isConnected_ = false;
            GameContext.log(1, "Attempting to connect to Server=" + DEFAULT_HOST + " port="+port);
            Socket socket = new Socket(DEFAULT_HOST, port);
            oStream_ = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream iStream =new ObjectInputStream(socket.getInputStream());

            // create a thread to listen for updates from the server.
            UpdateWorker w = new UpdateWorker(iStream);
            Thread t = new Thread(w);
            t.start();

            GameContext.log(0, "connected.");
            isConnected_ = true;
        }
        catch (ConnectException e) {
             GameContext.log(0, "failed to get connection. " +
                                "Probably because the server is not running or accessable. " +
                                "Playing a local game instead. " + e.getMessage());
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
            GameContext.log(0, "Failed to createListenSocket. \n"
                               +"You don't have permission to open a socket to "
                               + DEFAULT_HOST + " in the current context." + e.getMessage());
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
        sendCommand(new GameCommand(GameCommand.Name.ADD_TABLE, newTable));
    }

    public void nameChanged(String oldName, String newName) {
        String changer = oldName + GameCommand.CHANGE_TO + newName;
        sendCommand(new GameCommand(GameCommand.Name.CHANGE_NAME, changer))   ;
    }

    /**
     * Tell the server to add player p to this table.
     * The server will look at the most recently added player to this table to
     * determine who was added.
     */
    public void joinTable(Player p, OnlineGameTable table) {
        table.addPlayer(p);
        sendCommand(new GameCommand(GameCommand.Name.JOIN_TABLE, table));
    }

    public void leaveRoom(String playerName) {
        sendCommand(new GameCommand(GameCommand.Name.LEAVE_ROOM, playerName));
    }

    public void playerActionPerformed(PlayerAction action) {
        sendCommand(new GameCommand(GameCommand.Name.DO_ACTION, action));
    }

    private void exceptionOccurred(String msg, Throwable t) {
        isConnected_ = false;
        GameContext.log(0, msg);
        t.printStackTrace();
        throw new RuntimeException(t);
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

                    // we got a change to the tables on the server, update our client listeners.
                    int num = changeListeners_.size();
                    for (int i=0; i<num; i++)
                        changeListeners_.get(i).handleServerUpdate(cmd);

                }
                catch (IOException e) {
                    GameContext.log(0, "Read failed. Breaking connection.");
                    isConnected_ = false;
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
