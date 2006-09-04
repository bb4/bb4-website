package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
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
class SetOptionsDialog extends GameOptionsDialog implements ActionListener, ItemListener
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

        return new JComponent[] {initialNumCards_};
    }


    protected GameOptions getOptions() {
        return new SetOptions(initialNumCards_.getIntValue());
    }


}