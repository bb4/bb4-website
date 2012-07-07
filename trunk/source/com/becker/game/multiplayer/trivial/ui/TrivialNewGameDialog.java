/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.common.online.ui.OnlineGameManagerPanel;
import com.becker.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.becker.game.multiplayer.common.ui.PlayerTable;
import com.becker.game.multiplayer.trivial.online.ui.OnlineTrivialManagerPanel;

import java.awt.*;

/**
 * @author Barry Becker
 */
public class TrivialNewGameDialog extends MultiPlayerNewGameDialog {

    public TrivialNewGameDialog(Component parent, GameViewable viewer ) {
        super( parent, viewer );
    }

    @Override
    protected PlayerTable createPlayerTable() {
        return  new TrivialPlayerTable(controller_.getPlayers());
    }

    @Override
    protected OnlineGameManagerPanel createPlayOnlinePanel() {
        return new OnlineTrivialManagerPanel(viewer_, this);
    }

}

