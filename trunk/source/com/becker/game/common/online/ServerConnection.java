package com.becker.game.common.online;


import java.io.*;
import java.net.*;

/**
 * Opens a socket to the Game server so we can talk to it.
 * We pass data using object seriealization over the input and output streams.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class ServerConnection {

    private static final String DEFAULT_HOST = "127.0.0.1"; // localhost // "192.168.1.100";
    private Socket socket_;
    //private PrintWriter out_;
    //private BufferedReader in_;
    private ObjectOutputStream oStream_;
    private ObjectInputStream iStream_;

    private boolean isConnected_ = false;

    /**
     * @param port to open the connection on.
     */
    public ServerConnection(int port) {
        createListenSocket(port);
    }

    /**
     * @return true if we have a live connection to the server.
     */
    public boolean isConnected() {
        return isConnected_;
    }

    /**
     * @param cmd object to seriealize over the wire.
     */
    public void sendCommand(GameCommand cmd)  {

        try {
            // Send data over socket
             oStream_.writeObject(cmd);

            // Receive obj from server
            GameCommand receivedCmd = (GameCommand) iStream_.readObject();
            System.out.println("Received:" + receivedCmd);
        }
        catch (IOException e) {
            exceptionOccurred("Read failed.", e);
        }
        catch (ClassNotFoundException e) {
            exceptionOccurred("No Such Class.", e);
        }

    }

    public void createListenSocket(int port) {
        try {
            System.out.println("Attempting to connect to Server="+DEFAULT_HOST + " port="+port);
            socket_ = new Socket(DEFAULT_HOST, port);
            //out_ = new PrintWriter(socket_.getOutputStream(), true);
            //in_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
            oStream_ = new ObjectOutputStream(socket_.getOutputStream());
            iStream_ = new ObjectInputStream(socket_.getInputStream());
            isConnected_ = true;
        }
        catch (ConnectException e) {
             System.out.println("failed to get connection. " +
                                "Probably because the server is not running or accessable. " +
                                "Play local game.");
            isConnected_ = false;
        }
        catch (UnknownHostException e) {
            exceptionOccurred("Unknown host: "+ DEFAULT_HOST, e);
        }
        catch (IOException e) {
            exceptionOccurred("No I/O", e);
        }
    }

    public void addGameTable(OnlineGameTable newTable) {
        sendCommand(new GameCommand("add_table" , newTable));
    }

    private static void exceptionOccurred(String msg, Throwable t) {
        System.out.println(msg);
        t.printStackTrace();
        System.exit(1);
    }

}
