package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.poker.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class PokerOptionsDialog extends GameOptionsDialog
                         implements ActionListener, ItemListener, KeyListener
{

    private NumberInput ante_;
    private NumberInput initialChips_;
    private NumberInput maxAbsoluteRaise_;
    private NumberInput playerLimit_;
    private NumberInput numRobotPlayers_;

    // constructor
    PokerOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    /**
     * @return an array of panels to put in the parent controller param panel.
     */
    protected JComponent[] getControllerParamComponents() {

        PokerOptions options = (PokerOptions)controller_.getOptions();
        ante_ = new NumberInput( GameContext.getLabel("ANTE"), options.getAnte(),
                                         GameContext.getLabel("ANTE_TIP"), 1, 100, true);
        initialChips_ =
                new NumberInput(GameContext.getLabel("INITIAL_CHIPS"), options.getInitialChips(),
                                GameContext.getLabel("INITIAL_CHIPS_TIP"), 0, 10, true);
        maxAbsoluteRaise_ =
                new NumberInput(GameContext.getLabel("MAX_RAISE"), options.getMaxAbsoluteRaise(),
                                GameContext.getLabel("MAX_RAISE_TIP"), 1, 100, true);

        playerLimit_ =
                new NumberInput(GameContext.getLabel("PLAYER_LIMIT"), options.getPlayerLimit(),
                                GameContext.getLabel("PLAYER_LIMIT_TIP"), 2, 20, true);
        playerLimit_.addKeyListener(this);

        numRobotPlayers_ =
                new NumberInput(GameContext.getLabel("NUM_ROBOTS"), options.getNumRobotPlayers(),
                                GameContext.getLabel("NUM_ROBOTS_TIP"), 0, 20, true);


        return new JComponent[] {ante_, initialChips_, maxAbsoluteRaise_, playerLimit_, numRobotPlayers_};
    }


    protected GameOptions getOptions() {
        return new PokerOptions(ante_.getIntValue(),
                                initialChips_.getIntValue(),
                                maxAbsoluteRaise_.getIntValue(),
                                playerLimit_.getIntValue());

    }

    public void keyTyped(KeyEvent e) {

         if (playerLimit_.getIntValue() > 0) {
             numRobotPlayers_.setMax(playerLimit_.getIntValue());
         }
    }

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}
}