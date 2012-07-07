/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.dialogs.GameOptionsDialog;
import com.becker.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.becker.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.becker.game.multiplayer.common.ui.PlayerTable;

import java.awt.*;

public class GalacticNewGameDialog extends MultiPlayerNewGameDialog {

    public GalacticNewGameDialog( Component parent, GameViewable viewer ) {
        super( parent, viewer );
    }

    @Override
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


