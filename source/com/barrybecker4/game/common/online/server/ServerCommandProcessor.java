/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.common.ClassLoaderSingleton;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.OnlineGameTableList;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.plugin.PluginManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles the processing of all commands send to the online game server.
 *
 * @author Barry Becker
 */
class ServerCommandProcessor {

    /** Maintains the active list of game tables. */
    private GameTableManager tableManager;

    /** Maintain the master game state on the server. */
    private GameController controller_;

    /**
     * Create the online game server to serve all online clients.
     */
    public ServerCommandProcessor(String gameName) {

        createController(gameName);
        tableManager = new GameTableManager();
    }

    public OnlineGameTableList getTables() {
        return tableManager.getTables();
    }

    /**
     * Factory method to create the game controller via reflection.
     */
    private void createController(String gameName) {
        String controllerClass =
                PluginManager.getInstance().getPlugin(gameName).getControllerClass();
        Class c = ClassLoaderSingleton.loadClass(controllerClass);
        try {
            controller_ = (GameController) c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return controller_.getServerPort();
    }

    /**
     * Update our internal game table list, or server controller given the cmd from the client.
     * @param cmd to process. The command that the player has issued.
     * @return the response command(s) to send to all the clients.
     */
    public List<GameCommand> processCommand(GameCommand cmd) {

        List<GameCommand> responses = new LinkedList<GameCommand>();
        boolean useUpdateTable = true;

        switch (cmd.getName()) {
            case ENTER_ROOM :
                GameContext.log(2,"Entering room.");
                break;
            case LEAVE_ROOM :
                GameContext.log(0, "Player "+cmd.getArgument()+" is now leaving the room.");
                tableManager.removePlayer((String) cmd.getArgument());
                break;
            case ADD_TABLE :
                tableManager.addTable((OnlineGameTable) cmd.getArgument());
                break;
            case JOIN_TABLE :
                tableManager.joinTable((OnlineGameTable) cmd.getArgument());
                break;
            case CHANGE_NAME :
                String[] names = ((String)cmd.getArgument()).split(GameCommand.CHANGE_TO);
                if (names.length > 1) {
                    tableManager.changeName(names[0], names[1]);
                }
                break;
            case UPDATE_TABLES :
                break;
            case CHAT_MESSAGE :
                GameContext.log(2, "chat message=" + cmd.getArgument());
                useUpdateTable = false;
                responses.add(cmd);
                break;
            case START_GAME:
                OnlineGameTable tableToStart = (OnlineGameTable) cmd.getArgument();
                startGame(tableToStart);
                useUpdateTable = false;
                tableManager.removeTable(tableToStart);
                break;
            case DO_ACTION :
                // When a player on some client moves, the action is sent here to the server,
                // and then broadcast out so the surrogate(s) can be updated.
                // When a robot (on the server) moves, then that action is broadcast to the clients so
                // the surrogates on the clients can be updated.
                useUpdateTable = false;
                PlayerAction action = (PlayerAction) cmd.getArgument();
                GameContext.log(0, "Ignoring DO_ACTION ("+action+") in ServerCommandProcessor. Surrogates to handle");
                // one of the client players has acted. We need to apply this to the server controller.

                controller_.handlePlayerAction(action);
                responses.add(cmd);
                break;
            default:
                assert false : "Unhandled command: "+ cmd;
        }

        if (useUpdateTable) {
            GameCommand response = new GameCommand(GameCommand.Name.UPDATE_TABLES, getTables());
            responses.add(0, response);  // add as first command in response.
        }

        return responses;
    }

    /**
     * When all the conditions are met for starting a new game, we create a new game controller of the
     * appropriate type and start the game here on the server.
     * All human players will be surrogates and robots will be themselves.
     * @param table the table to start a game for
     */
    private void startGame(OnlineGameTable table) {

        GameContext.log(0, "Now starting game on Server! "+ table);

        // Create players from the table and start.
        PlayerList players = table.getPlayers();
        assert (players.size() == table.getNumPlayersNeeded());
        PlayerList newPlayers = new PlayerList();
        for (Player player : players) {
            if (player.isHuman()) {
                newPlayers.add(player.createSurrogate(controller_.getServerConnection()));
            } else {
                newPlayers.add(player);
            }
        }
        controller_.reset();
        controller_.setPlayers(newPlayers);

        // if getFirstPlayer returns null, then it is not a turn based game
        Player firstPlayer = controller_.getPlayers().getFirstPlayer();
        if (firstPlayer != null && !firstPlayer.isHuman()) {
            controller_.computerMovesFirst();
        }
    }
}
