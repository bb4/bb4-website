package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.set.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class SetOptionsDialog extends MultiGameOptionsDialog
        implements ActionListener, ItemListener
{

    NumberInput initialNumCards_;

    // constructor
    SetOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    /**
     * @return Set game options tab panel.
     */
    protected JComponent[] getControllerParamComponents()
    {
        SetOptions options = (SetOptions) controller_.getOptions();

        initialNumCards_ =
                new NumberInput(GameContext.getLabel("INITIAL_NUM_CARDS"), options.getInitialNumCardsShown(),
                                GameContext.getLabel("INITIAL_NUM_CARDS_TIP"), 8, 16, true);

        initMultiControllerParamComponents(options);
        
        return new JComponent[] {initialNumCards_, maxNumPlayers_, numRobotPlayers_};
    }


    public GameOptions getOptions() {
        return new SetOptions(maxNumPlayers_.getIntValue(),
                              numRobotPlayers_.getIntValue(),
                              initialNumCards_.getIntValue());
    }


}