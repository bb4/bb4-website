/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online;

import com.barrybecker4.common.CommandLineOptions;
import com.barrybecker4.game.common.GameContext;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract server for online games.
 * This can be run from within a ui or from a command line.
 * If running from the command line use:
 *       java OnlineGameServer -game <gameName>
 * e.g.  java OnlineGameServer -game poker
 *
 * If running from the UI, see
 *   OnlineGameServerFrame
 *
 * Manages the tables for the game room.
 * Has a GameController for each table.
 * Consider using executor framework to manage threads.
 *
 * @author Barry Becker
 */
public class OnlineGameServer  {

    public static final String GAME_OPTION = "game";

    private JTextArea textArea;
    private ServerSocket server;
    private int port;

    /** processes server commands. May someday need subclassing.  */
    private ServerCommandProcessor cmdProcessor;

    /** keep a list of the threads that we have for each client connection.  */
    private List<ClientWorker> clientConnections;

    /**
     * Create the online game server to serve all online clients.
     * @param gameType - one of the supported games (see plugin file)
     * @param textArea - may be null if not running in UI, elese shows traffic messages.
     */
    public OnlineGameServer(String gameType, JTextArea textArea) {

        //port = PluginManager.getInstance().getPlugin(gameType).getPort();
        this.textArea = textArea;
        cmdProcessor = new ServerCommandProcessor(gameType);
        port = cmdProcessor.getPort();
        clientConnections = new LinkedList<ClientWorker>();
        openListenSocket();
    }

    /**
     * open a server socket to listen on our assigned port for
     * requests from clients. Updates will be broadcast on this socket.
     * Maintain a list of clientConnections corresponding to the players
     * that we need to broadcast to when something changes.
     */
    void openListenSocket() {

        try {
            server = new ServerSocket(port);
        }
        catch (BindException e) {
            GameContext.log(0, "Address already in use! Perhaps there is already another game server sunning on this port:" + port);
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            GameContext.log(0, "Could not listen on port " + port);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        while (true) {
            OnlineGameServer.ClientWorker w;
            try {
                // accept new connections from players wanting to join.
                w = new ClientWorker(server.accept(), textArea);
                Thread t = new Thread(w);
                clientConnections.add(w);
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
    @Override
    protected void finalize() {
        try {
            super.finalize();
            server.close();
        }
        catch (IOException e) {
            GameContext.log(0, "Could not close socket");
            e.printStackTrace();
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }

            try {
                // initial update to the game tables for someone entering the room.
                update(new GameCommand(GameCommand.Name.UPDATE_TABLES, cmdProcessor.getTables()));

                while (true) {

                    // receive the serialized commands that are sent and process them.
                    GameCommand cmd = (GameCommand) iStream_.readObject();

                    // we got a change to the tables, update internal structure and broadcast new list.
                    List<GameCommand> responses = cmdProcessor.processCommand(cmd);

                    for (GameCommand response: responses) {
                        for (ClientWorker w : clientConnections) {
                            w.update(response);
                        }
                    }

                    if (text_ == null)  {
                       GameContext.log(0,cmd.toString());
                    }  else {
                       text_.append(cmd.toString() + '\n');
                       JScrollPane spane = ((JScrollPane)text_.getParent().getParent());
                       spane.getVerticalScrollBar().setValue(spane.getVerticalScrollBar().getMaximum());
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
            clientConnections.remove(this);
        }

        /**
         * broadcast the current list of tables to all the online clients.
         */
        public synchronized void update(GameCommand response) throws IOException {

            GameContext.log(1, "OnlineGameServer: sending:" + cmdProcessor.getTables());

            // must reset the stream first, otherwise tables_ will always be the same as first sent.
            oStream_.reset();
            oStream_.writeObject(response);
            oStream_.flush();

        }

        @Override
        protected void finalize() {
            try {
               super.finalize();
               oStream_.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * @param options
     * @return true if valid.
     */
    public static boolean verifyCmdLineOptions(CommandLineOptions options) {
        if (options.contains("help") || !options.contains(GAME_OPTION)) {
           GameContext.log(0, "Usage: -game <game name>\n");
            return false;
        }
        if (options.getValueForOption(GAME_OPTION) == null) {
            GameContext.log(0,"You must specify a valid game. See plugins.xml for list of available games.");
            return false;
        }
        return true;
    }

     /**
      * Implements OnlineGameServerInterface which is also implemented by GtpTesujiSoftGoServer.
      * not currently used, but I'm trying to have a consistent game server interface.
      *
     public boolean handleCommand(String cmdLine, StringBuilder response) {
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
            String gameName = options.getValueForOption(GAME_OPTION);
            OnlineGameServer server = new OnlineGameServer(gameName, null);
        }
    }
}
