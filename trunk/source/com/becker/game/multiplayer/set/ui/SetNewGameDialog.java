package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.common.online.ui.*;

import com.becker.game.multiplayer.set.SetPlayer;
import java.util.List;
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


    protected PlayerTable createPlayerTable() {
        return  new SetPlayerTable((List<SetPlayer>) controller_.getPlayers());
    }


    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name) {
        return null;
    }

    protected GameOptionsDialog createNewGameTableDialog() {
        return null;
    }

}
