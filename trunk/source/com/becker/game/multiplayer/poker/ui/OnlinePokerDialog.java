package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.online.ui.*;
import com.becker.game.multiplayer.common.online.ui.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Manage online player games.
 * Player can join no more than one table at a time.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class OnlinePokerDialog extends MultiPlayerOnlineGameDialog {

    public OnlinePokerDialog(Frame parent, ViewerCallbackInterface viewer) {
        super(parent, viewer);
    }

    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String playersName,
                                                                      ActionListener listener) {
        return new PokerOnlineGameTablesTable(listener);
    }

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected GameOptionsDialog createNewGameTableDialog() {
        return new PokerOptionsDialog(null, controller_);
    }


}
