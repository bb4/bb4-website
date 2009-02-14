package com.becker.game.multiplayer.trivial.online.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.online.ui.*;
import com.becker.game.multiplayer.trivial.ui.*;

import javax.swing.event.*;
import java.awt.event.*;

/**
 * Manage online player games.
 * Player can join no more than one table at a time.
 *
 * @author Barry Becker Date: May 14, 2006
 */
public class OnlineTrivialManagerPanel extends MultiPlayerOnlineManagerPanel {

    private static final long serialVersionUID = 1;
    
    public OnlineTrivialManagerPanel(ViewerCallbackInterface viewer, ChangeListener dlg) {
        super(viewer, dlg);
    }

    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String playersName,
                                                                      ActionListener listener) {
        return new TrivialOnlineGameTablesTable(listener);
    }

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected GameOptionsDialog createNewGameTableDialog() {
        return new TrivialOptionsDialog(null, controller_);
    }

}