package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.common.online.ui.*;
import com.becker.game.multiplayer.online.ui.*;

import javax.swing.*;

public class GalacticNewGameDialog extends MultiPlayerNewGameDialog
{


    public GalacticNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }


    protected PlayerTable createPlayerTable() {
        return  new GalacticPlayerTable( controller_.getPlayers());
    }


    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name) {
        return null;
    }

    protected GameOptionsDialog createNewGameTableDialog() {
        return null;
    }


}


