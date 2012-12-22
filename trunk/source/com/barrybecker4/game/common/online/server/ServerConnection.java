/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Opens a socket to the Game server from the client so we can talk to it.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
public class ServerConnection implements IServerConnection {

    /**
     * Hardcoded for now, but should be configurable.
     * localhost "127.0.0.1";
     * or maybe "192.168.1.100";
     */
    private static final String DEFAULT_HOST = "192.168.1.66";

    private ObjectOutputStream oStream_;

    private boolean isConnected_ = false;

    /** a list of things that want to hear about broadcasts from the server about changed game state. */
    private List<OnlineChangeListener> changeListeners_;

    /**
     * Constructor
     * Note that the list of listeners is a CopyOnWriteArrayList to
     * avoid ConcurrentModificationErrors when we iterate over the list when
     * new listeners may be added concurrently.
     * @param port to open the connection on.
     */
    public ServerConnection(int port) {
        changeListeners_ = new CopyOnWriteArrayList<OnlineChangeListener>();
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
     * Send data over the socket to the server using the output stream.
     * @param cmd object to serialize over the wire.
     */
    public void sendCommand(GameCommand cmd)  {

        try {
            assert(oStream_ != null && cmd != null) : "No socket: oStream="+ oStream_ +" cmd=" + cmd;
            oStream_.writeObject(cmd);
            oStream_.flush();
        }
        catch (IOException e) {
            exceptionOccurred("Send failed.", e);
        }
    }

    /**
     * Open a socket to the server to listen for, and send information.
     * Consider using executor framework.
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
                    GameContext.log(1, "Connection: got an update of the table from the server:\n" + cmd);

                    for (OnlineChangeListener aChangeListeners_ : changeListeners_) {
                        aChangeListeners_.handleServerUpdate(cmd);
                    }
                }
                catch (SocketException e) {
                    GameContext.log(0, "Read failed (probably because player closed client).  Breaking connection.");
                    isConnected_ = false;
                    break;
                }
                catch (IOException e) {
                    GameContext.log(0, "Read failed.  Breaking connection.");
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
