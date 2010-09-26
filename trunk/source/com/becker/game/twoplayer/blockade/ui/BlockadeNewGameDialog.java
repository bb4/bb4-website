package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.GameViewable;
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
    public BlockadeNewGameDialog( JFrame parent, GameViewable viewer )
    {
        super( parent, viewer );
    }

}

