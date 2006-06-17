package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.online.ui.*;
import com.becker.game.multiplayer.poker.online.*;
import com.becker.game.online.*;

import java.awt.*;

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

    protected MultiPlayerOnlineGamesTable createOnlineGamesTable(String playersName) {
        return new PokerOnlineGamesTable(null);
    }

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected GameOptionsDialog createNewGameTableDialog() {
        return new PokerOptionsDialog(null, controller_);
    }

    protected ServerConnection createServerConnection(OnlineChangeListener l) {
         return new ServerConnection(OnlinePokerServer.PORT, l);
     }

}
