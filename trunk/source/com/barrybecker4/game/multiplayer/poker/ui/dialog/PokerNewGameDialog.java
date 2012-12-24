/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameViewable;
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel;
import com.barrybecker4.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;
import com.barrybecker4.game.multiplayer.poker.online.ui.OnlinePokerManagerPanel;
import com.barrybecker4.game.multiplayer.poker.ui.PokerPlayerTable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class PokerNewGameDialog extends MultiPlayerNewGameDialog {

    public PokerNewGameDialog(Component parent, GameViewable viewer ) {
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

}

