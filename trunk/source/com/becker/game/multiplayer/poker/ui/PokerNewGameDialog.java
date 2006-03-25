package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.poker.*;

import javax.swing.*;

public class PokerNewGameDialog extends MultiPlayerNewGameDialog
{



    public PokerNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }


    public final String getTitle()
    {
        return GameContext.getLabel("POKER_OPTIONS");
    }

    protected PlayerTable createPlayerTable() {
        return  new PokerPlayerTable((PokerPlayer[]) controller_.getPlayers());
    }


    /**
     * we don't allow them to change the dimensions of the board in poker since its not played on a grid.
     */
    protected JPanel createBoardParamPanel()
    {
        return null;
    }

}

