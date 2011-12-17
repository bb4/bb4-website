/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.online;

import com.becker.common.ClassLoaderSingleton;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.plugin.PluginManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles the processing of all commands send to the online game server.
 *
 * @author Barry Becker Date: Sep 16, 2006
 */
class ServerCommandProcessor {

    /** Maintain a list of game tables. */
    private OnlineGameTableList tables_;

    /** Maintain the master game state on the server. */
    private GameController controller_;

    /**
     * Create the online game server to serve all online clients.
     */
    public ServerCommandProcessor(String gameName) {

        createController(gameName);
        tables_ = new OnlineGameTableList();
    }

    public OnlineGameTableList getTables() {
        return tables_;
    }

    /**
     * Factory method to create the game controller.
     */
    private void createController(String gameName) {
        String controllerClass = PluginManager.getInstance().getPlugin(gameName).getControllerClass();
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
     * Update our internal game table list, or server controlller given the cmd from the client.
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
                tables_.removePlayer((String) cmd.getArgument());
                break;
            case ADD_TABLE :
                addTable((OnlineGameTable) cmd.getArgument());
                break;
            case JOIN_TABLE :
                GameCommand startCmd = joinTable((OnlineGameTable) cmd.getArgument());
                if (startCmd != null)
                    responses.add(startCmd);
                break;
            case CHANGE_NAME :
                String[] names = ((String)cmd.getArgument()).split(GameCommand.CHANGE_TO);
                if (names.length >1) {
                    changeName(names[0], names[1]);
                }
                break;
            case UPDATE_TABLES :
                break;
            case CHAT_MESSAGE :
                GameContext.log(2, "chat message=" + cmd.getArgument());
                useUpdateTable = false;
                responses.add(cmd);          
                break;
            case DO_ACTION :
                // a player or robot moves, this action is sent here to the server, then we broadcast it out so the surrogate(s) can be updated.
                useUpdateTable = false;
                GameContext.log(2, "Ignoring DO_ACTION in ServerCommandProcessor. Surrogates to handle");
                // one of the client players has acted. We need to apply this to the server controller.
                //PlayerAction action = (PlayerAction) cmd.getArgument();
                //controller_.handlePlayerAction(action);     
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
     *
     * @param table
     */
    private void addTable(OnlineGameTable table) {

        // if the table we are adding has the same name as an existing table change it to something unique
        String uniqueName = verifyUniqueName(table.getName());
        table.setName(uniqueName);
        // if the player at this new table is already sitting at another table,
        // remove him from the other tables, and delete those other tables if no one else is there.
        assert(table.getPlayers().size() >= 1):
            "It is expected that when you add a new table there is at least one player at it" +
            " (exactly one human owner and 0 or more robots).";
        tables_.removePlayer(table.getOwner());
        tables_.add(table);
    }

    /**
     * Get the most recently added human player from table and have them join the table with the same name.
     * If there is a table now ready to play after this change, then start it.
     */
    private GameCommand joinTable(OnlineGameTable table) {

        GameCommand response = null;
        // if the player at this new table is already sitting at another table,
        // remove him from the other tables(s) and delete those other tables (if no one else is there).
        Player p = table.getNewestHumanPlayer();
        GameContext.log(2, "in join table on the server p="+p);
        tables_.removePlayer(p);
        tables_.join(table.getName(), p);
        OnlineGameTable tableToStart = tables_.getTableReadyToPlay(p.getName());
        if (tableToStart != null) {
            startGame(tableToStart);
            response = new GameCommand(GameCommand.Name.START_GAME,  tableToStart);
        }
        return response;
    }

    /**
     * Change the players name from oldName to newName. 
     */
    private void changeName(String oldName, String newName) {

        tables_.changeName(oldName, newName);
    }


    /**
     * When all the conditions are met for starting a new game, we create a new game controller of the
     * appropriate type and start the game here on the server.
     * All human players will be surrogate and robots will be themselves.
     * @param table
     */
    private void startGame(OnlineGameTable table) {

        GameContext.log(1, "NOW starting game on Server! "+ table);

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
        if (controller_.getPlayers().getFirstPlayer() != null &&  !controller_.getPlayers().getFirstPlayer().isHuman()) {
            controller_.computerMovesFirst();
        }
       
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

}
