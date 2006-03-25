package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Blockade
 *
 *  @author Barry Becker
 */
class BlockadeNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public BlockadeNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }

    public final String getTitle()
    {
        return GameContext.getLabel("BLOCKADE_OPTIONS");
    }

}

