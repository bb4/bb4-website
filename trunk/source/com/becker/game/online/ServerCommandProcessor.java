package com.becker.game.online;

import com.becker.game.common.*;

/**
 * Handles the processing of all commands send to the online game server.
 *
 * @author Barry Becker Date: Sep 16, 2006
 */
public class ServerCommandProcessor {

    // maintain a list of game tables.
    private OnlineGameTableList tables_;


    /**
     * Create the online game server to serve all online clients.
     */
    public ServerCommandProcessor() {

        tables_ = new OnlineGameTableList();
    }

    public OnlineGameTableList getTables() {
        return tables_;
    }

    /**
     * Update our internal game table list given the cmd from the client.
     * @param cmd to process. The command that the player has issued.
     * @return true if successful
     */
    public boolean processCmd(GameCommand cmd) {
        switch (cmd.getName()) {
            case ENTER_ROOM :
                System.out.println("Entering room.");                
                break;
            case LEAVE_ROOM :
                System.out.println("Leaving room.");
                tables_.removePlayer((String) cmd.getArgument());
                break;
            case ADD_TABLE :
                addTable((OnlineGameTable) cmd.getArgument());
                break;
            case JOIN_TABLE :
                joinTable((OnlineGameTable) cmd.getArgument());
                break;
            case CHANGE_NAME :
                String[] names = ((String)cmd.getArgument()).split(GameCommand.CHANGE_TO);
                if (names.length >1) {
                    changeName(names[0], names[1]);
                }
                break;
            case UPDATE_TABLES :
                break;
        }
        return true;
    }

    private void addTable(OnlineGameTable table) {

        // if the table we are adding has the same name as an existing table change it to something unique
        String uniqueName = verifyUniqueName(table.getName());
        table.setName(uniqueName);
        // if the player at this new table is already sitting at another table,
        // remove him from the other tables(s) and delete those other tables (if no one else is there).
        assert(table.getPlayers().size() == 1):
            "It is expected that when you add a new table there is exactly one player at it - the table owner";
        tables_.removePlayer(table.getOwner());
        tables_.add(table);
    }

    /**
     * get the recent player from table and have them join the table with the same name.
     */
    private void joinTable(OnlineGameTable table) {


        // if the player at this new table is already sitting at another table,
        // remove him from the other tables(s) and delete those other tables (if no one else is there).
        Player p = table.getNewestPlayer();
        System.out.println("in join table on the server p="+p);
        tables_.removePlayer(p);
        tables_.join(table.getName(), p);
    }

    private void changeName(String oldName, String newName) {

        tables_.changeName(oldName, newName);
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
