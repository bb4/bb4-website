package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.dialogs.GameOptionsDialog;
import com.becker.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.becker.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.becker.game.multiplayer.common.ui.PlayerTable;

import javax.swing.*;

/**
 * @author Barry Becker Date: Feb 5, 2006
 */
public class SetNewGameDialog extends MultiPlayerNewGameDialog
{

    /**
     * constructor.
     */
    public SetNewGameDialog( JFrame parent, GameViewable viewer )
    {
        super( parent, viewer );
    }


    @Override
    protected PlayerTable createPlayerTable() {
        return  new SetPlayerTable(controller_.getPlayers());
    }


    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name) {
        return null;
    }

    protected GameOptionsDialog createNewGameTableDialog() {
        return null;
    }

}
