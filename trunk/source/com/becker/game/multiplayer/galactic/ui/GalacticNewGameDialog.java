package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;

import javax.swing.*;

public class GalacticNewGameDialog extends MultiPlayerNewGameDialog
{


    public GalacticNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }


    public String getTitle()
    {
        return GameContext.getLabel("GALACTIC_OPTIONS");
    }

    protected PlayerTable createPlayerTable() {
        return  new GalacticPlayerTable( controller_.getPlayers());
    }

}


