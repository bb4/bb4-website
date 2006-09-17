package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
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
class PokerOptionsDialog extends MultiGameOptionsDialog
                         implements ActionListener, ItemListener
{
    private NumberInput ante_;
    private NumberInput initialChips_;
    private NumberInput maxAbsoluteRaise_;

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
                new NumberInput(GameContext.getLabel("INITIAL_CASH"), options.getInitialCash(),
                                GameContext.getLabel("INITIAL_CASH_TIP"), 0, 10, true);
        maxAbsoluteRaise_ =
                new NumberInput(GameContext.getLabel("MAX_RAISE"), options.getMaxAbsoluteRaise(),
                                GameContext.getLabel("MAX_RAISE_TIP"), 1, 100, true);

        initMultiControllerParamComponents(options);

        return new JComponent[] {ante_, initialChips_, maxAbsoluteRaise_, maxNumPlayers_, numRobotPlayers_};
    }


    public GameOptions getOptions() {
        return new PokerOptions(maxNumPlayers_.getIntValue(),
                                numRobotPlayers_.getIntValue(),
                                ante_.getIntValue(),
                                maxAbsoluteRaise_.getIntValue(),
                                initialChips_.getIntValue());

    }

}