package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 *  Any special options that are needed for Checkers
 *
 *  @author Barry Becker
 */
public class CheckersNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public CheckersNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        super( parent, viewer );
    }

}

