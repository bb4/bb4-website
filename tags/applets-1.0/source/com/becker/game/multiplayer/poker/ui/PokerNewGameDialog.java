/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.common.online.ui.OnlineGameManagerPanel;
import com.becker.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.becker.game.multiplayer.common.ui.PlayerTable;
import com.becker.game.multiplayer.poker.online.ui.OnlinePokerManagerPanel;

import javax.swing.*;

/**
 * @author Barry Becker
 */
public class PokerNewGameDialog extends MultiPlayerNewGameDialog
{

    public PokerNewGameDialog( JFrame parent, GameViewable viewer )
    {
        super( parent, viewer );
    }


    @Override
    protected PlayerTable createPlayerTable() {
        return  new PokerPlayerTable(controller_.getPlayers());
    }

    @Override
    protected OnlineGameManagerPanel createPlayOnlinePanel() {
        return new OnlinePokerManagerPanel(viewer_, this);
    }


    /**
     * we don't allow them to change the dimensions of the board in poker since its not played on a grid.
     */
    @Override
    protected JPanel createBoardParamPanel()
    {
        return null;
    }

}

