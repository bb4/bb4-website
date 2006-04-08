package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.multiplayer.set.*;

import javax.swing.*;
import javax.swing.Box;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class SetOptionsDialog extends GameOptionsDialog implements ActionListener, ItemListener
{

    // constructor
    SetOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    /**
     * @return Set game options tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        JPanel p = new JPanel();

        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder(
                       BorderFactory.createEtchedBorder(),
                         GameContext.getLabel("GAME_OPTIONS")) );

        SetController c = (SetController)controller_;

        p.add(Box.createVerticalGlue());

        p.setName(GameContext.getLabel("GAME"));
        return p;
    }


    protected void ok()
    {
        SetController c = (SetController)controller_;
        super.ok();
    }

}