package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.set.*;

import javax.swing.*;

/**
 * @author Barry Becker Date: Feb 5, 2006
 */
public class SetNewGameDialog extends MultiPlayerNewGameDialog
{

    // constructor
    public SetNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer);
    }


    public final String getTitle()
    {
        return GameContext.getLabel("SET_OPTIONS");
    }


    protected PlayerTable createPlayerTable() {
        return  new SetPlayerTable((SetPlayer[]) controller_.getPlayers());
    }

}
