/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Checkers
 *
 *  @author Barry Becker
 */
class CheckersNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    // constructor
    public CheckersNewGameDialog( JFrame parent, GameViewable viewer )
    {
        super( parent, viewer );
    }

}

