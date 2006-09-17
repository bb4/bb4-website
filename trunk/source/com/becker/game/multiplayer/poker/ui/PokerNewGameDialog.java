package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.poker.player.*;

import javax.swing.*;

/**
 * @author Barry Becker
 */
public class PokerNewGameDialog extends MultiPlayerNewGameDialog
{

    public PokerNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
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

