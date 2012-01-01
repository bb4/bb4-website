/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente.ui;

import com.becker.game.common.GameViewable;
import com.becker.game.twoplayer.common.ui.TwoPlayerNewGameDialog;

import javax.swing.*;
import java.awt.event.ActionListener;

public class PenteNewGameDialog extends TwoPlayerNewGameDialog implements ActionListener
{

    public PenteNewGameDialog( JFrame parent, GameViewable viewer )
    {
        super( parent, viewer );
    }

}
