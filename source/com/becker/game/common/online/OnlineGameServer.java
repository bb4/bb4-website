package com.becker.game.common.online;

import com.becker.common.*;
import com.becker.game.common.*;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The abstract server for online games.
 * This can be run from within a ui or from a command line.
 * If running from the command line use:
 *       java OnlineGameServer -p <port> -game <gameName>
 * e.g.  java OnlineGameServer -9 4443 -game poker
 *
 * If running from the UI, see
 *   OnlineGameServerFrame
 *
 * Manages the tables for the game room.
 * Has a GameController for each table.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class OnlineGameServer  {

    public static final String GAME_OPTION = "game";
    public static final String PORT_OPTION = "port";

    protected JTextArea textArea_;
    protected ServerSocket server_;
    private int port_;

    /** processes server commands. May someday need sublassing.  */
    private ServerCommandProcessor cmdProcessor_;

    /** keep a list of the threads that we have for each client connection.  */
    List<ClientWorker> clientConnections_;

    /**
     * Create the online game server to serve all online clients.
     * @param port - should be unique for each gameServer that is runningon a given server.
     * @param gameType - one of the supported games (see plugin file)
     * @param textArea - may be null if not running in UI, elese shows traffic messages.
     */
    public OnlineGameServer(int port, String gameType, JTextArea textArea) {

        port_ = port;
        textArea_ = textArea;
        cmdProcessor_ = new ServerCommandProcessor(gameType);
        clientConnections_ = new LinkedList<ClientWorker>();
        openListenSocket();
    }


    /**
     * open a server socket to listen on our assigned port for
     * requests from clients. Updates will be broadcast on this socket.
     * Maintain a list of clientConnections corresponding to the players
     * that we need to broadcast to when something changes.
     */
    public void openListenSocket() {

        try {
            server_ = new ServerSocket(port_);
        }
        catch (IOException e) {
            GameContext.log(0, "Could not listen on port " + port_);
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
                GameContext.log(0, "Accept failed: " + port_);
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
                    if (text_ == null)  {
                       System.out.println(cmd.toString());
                    }  else {
                       text_.append(cmd.toString() + '\n');
                    }

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
     * @param options
     * @return true if valid.
     */
    public static boolean verifyCmdLineOptions(CommandLineOptions options) {
        if (options.contains("help") || !options.contains(PORT_OPTION) || !options.contains(GAME_OPTION)) {
            System.out.println("Usage: -port <port number> -game <game name>\n");
            return false;
        }
        if (options.getValueForOption(PORT_OPTION) != null) {
            String p = options.getValueForOption(PORT_OPTION);
            try {
               Integer.parseInt(p);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number : "+p);
                return false;
            }
        }
        else {
            System.out.println("You must specify a port number.");
            return false;
        }
        if (options.getValueForOption(GAME_OPTION) == null) {
            System.out.println("You must specify a valid game. See plugins.xml for list of available games.");
            return false;
        }
        return true;
    }

     /**
      * Implements OnlineGameServerInterface which is also implemented by GtpTesujiSoftGoServer.
      * not currently used, but I'm trying to have a consistent game server interface.
      *
     public boolean handleCommand(String cmdLine, StringBuffer response) {
         String[] cmdArray = StringUtils.tokenize(cmdLine);
         String cmdStr = cmdArray[0];
         boolean status = true;
         GameCommand cmd = new GameCommand(GameCommand.Name.valueOf(cmdStr), cmdStr);
         return processCmd(cmd);
     } */


    /**
     * create and show the server.
     */
    public static void main(String[] args) {

        CommandLineOptions options = new CommandLineOptions(args);

        if (verifyCmdLineOptions(options))  {
            int port = Integer.parseInt(options.getValueForOption(PORT_OPTION));
            String gameName = options.getValueForOption(GAME_OPTION);
            OnlineGameServer server = new OnlineGameServer(port, gameName, null);
        }
    }
}
