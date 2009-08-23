package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.*;
import com.becker.game.common.online.ui.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.game.multiplayer.trivial.online.ui.*;
import java.util.List;

import javax.swing.*;

/**
 * @author Barry Becker
 */
public class TrivialNewGameDialog extends MultiPlayerNewGameDialog
{

    public TrivialNewGameDialog( JFrame parent, GameViewable viewer ) {
        super( parent, viewer );
    }

    protected PlayerTable createPlayerTable() {
        return  new TrivialPlayerTable((List<TrivialPlayer>) controller_.getPlayers());
    }

    protected OnlineGameManagerPanel createPlayOnlinePanel() {
        return new OnlineTrivialManagerPanel(viewer_, this);
    }

}

