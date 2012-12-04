/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.online.ui;

import com.barrybecker4.game.common.GameViewable;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineManagerPanel;
import com.barrybecker4.game.multiplayer.poker.ui.PokerOptionsDialog;
import com.barrybecker4.ui.table.TableButtonListener;

import javax.swing.event.ChangeListener;

/**
 * Manage online player games.
 * Player can join no more than one table at a time.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class OnlinePokerManagerPanel extends MultiPlayerOnlineManagerPanel {

    private static final long serialVersionUID = 1;

    public OnlinePokerManagerPanel(GameViewable viewer, ChangeListener dlg) {
        super(viewer, dlg);
    }

    @Override
    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(TableButtonListener tableButtonListener) {
        return new PokerOnlineGameTablesTable(tableButtonListener);
    }

    /**
     * You are free to set your own options for the table that you are creating.
     */
    @Override
    protected GameOptionsDialog createNewGameTableDialog() {
        return new PokerOptionsDialog(null, controller_);
    }


}
