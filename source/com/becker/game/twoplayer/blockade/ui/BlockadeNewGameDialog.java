/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Blockade
 *
 *  @author Barry Becker
 */
class BlockadeNewGameDialog extends TwoPlayerNewGameDialog
                            implements ActionListener {

    // constructor
    public BlockadeNewGameDialog(Component parent, GameViewable viewer )
    {
        super(parent, viewer);
    }

}

