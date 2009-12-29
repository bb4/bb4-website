package com.becker.game.multiplayer.poker.ui;

import com.becker.ui.components.NumberInput;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.poker.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class PokerOptionsDialog extends MultiGameOptionsDialog
                         implements ActionListener, ItemListener
{
    private NumberInput ante_;
    private NumberInput initialChips_;
    private NumberInput maxAbsoluteRaise_;

    /**
     * Constructor
     */
    public PokerOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }

    /**
     * @return an array of panels to put in the parent controller param panel.
     */
    @Override
    protected JComponent[] getControllerParamComponents() {

        PokerOptions options = (PokerOptions)controller_.getOptions();
        ante_ = new NumberInput( GameContext.getLabel("ANTE"), options.getAnte(),
                                 GameContext.getLabel("ANTE_TIP"),
                                 1, 100 * PokerOptions.DEFAULT_ANTE, true);
        initialChips_ =
                new NumberInput(GameContext.getLabel("INITIAL_CASH"), options.getInitialCash(),
                                GameContext.getLabel("INITIAL_CASH_TIP"),
                                1, 1000 * PokerOptions.DEFAULT_INITIAL_CASH, true);
        maxAbsoluteRaise_ =
                new NumberInput(GameContext.getLabel("MAX_RAISE"), options.getMaxAbsoluteRaise(),
                                GameContext.getLabel("MAX_RAISE_TIP"),
                                1, 100 * PokerOptions.DEFAULT_MAX_ABS_RAISE, true);

        initMultiControllerParamComponents(options);
        
        JPanel spacer = new JPanel();     

        return new JComponent[] {ante_, initialChips_, maxAbsoluteRaise_, maxNumPlayers_, numRobotPlayers_, spacer};
    }


    @Override
    public GameOptions getOptions() {
        return new PokerOptions(maxNumPlayers_.getIntValue(),
                                numRobotPlayers_.getIntValue(),
                                ante_.getIntValue(),
                                maxAbsoluteRaise_.getIntValue(),
                                initialChips_.getIntValue());

    }

}