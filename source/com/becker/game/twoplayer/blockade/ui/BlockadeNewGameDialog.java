package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.event.*;

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

}

