package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.trivial.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TrivialOptionsDialog extends MultiGameOptionsDialog
                         implements ActionListener, ItemListener
{

    /**
     * Constructor
     */
    public TrivialOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }

    /**
     * @return an array of panels to put in the parent controller param panel.
     */
    @Override
    protected JComponent[] getControllerParamComponents() {

        TrivialOptions options = (TrivialOptions)controller_.getOptions();      

        initMultiControllerParamComponents(options);
        
        JPanel spacer = new JPanel();     

        return new JComponent[] { maxNumPlayers_, numRobotPlayers_, spacer};
    }


    @Override
    public GameOptions getOptions() {
        return new TrivialOptions(maxNumPlayers_.getIntValue(),
                                numRobotPlayers_.getIntValue());
    }

}