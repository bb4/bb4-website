package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.multiplayer.poker.PokerController;

import javax.swing.*;
import javax.swing.Box;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * todo: add options
 *  -for starting chips for each player
 *  - bet limit
 *  - player limit
 *
 * @author Barry Becker
 */
class PokerOptionsDialog extends GameOptionsDialog implements ActionListener, ItemListener
{

    // constructor
    PokerOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    /**
     * @return Poker game optiosn tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        JPanel p = new JPanel();

        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder(
                       BorderFactory.createEtchedBorder(),
                         GameContext.getLabel("GAME_OPTIONS")) );

        PokerController c = (PokerController)controller_;

        p.add(Box.createVerticalGlue());

        p.setName(GameContext.getLabel("GAME"));
        return p;
    }


    protected void ok()
    {
        PokerController c = (PokerController)controller_;
        super.ok();
    }

}