package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.multiplayer.common.MultiGameOptions;
import com.becker.ui.components.NumberInput;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Barry Becker Date: Sep 9, 2006
 */
public abstract class MultiGameOptionsDialog extends GameOptionsDialog
                                    implements KeyListener  {

    protected NumberInput maxNumPlayers_;
    protected NumberInput numRobotPlayers_;

    private static final int ABS_MAX_NUM_PLAYERS = 30;

    protected MultiGameOptionsDialog(JFrame parent, GameController controller ) {
        super(parent, controller);
    }

    protected void initMultiControllerParamComponents(MultiGameOptions options) {
        maxNumPlayers_ =
            new NumberInput(GameContext.getLabel("MAX_NUM_PLAYERS"), options.getMaxNumPlayers(),
                                GameContext.getLabel("MAX_NUM_PLAYERS_TIP"), options.getMinNumPlayers(), ABS_MAX_NUM_PLAYERS, true);
        maxNumPlayers_.addKeyListener(this);

        numRobotPlayers_ =
                new NumberInput(GameContext.getLabel("NUM_ROBOTS"), options.getNumRobotPlayers(),
                                GameContext.getLabel("NUM_ROBOTS_TIP"), 0, ABS_MAX_NUM_PLAYERS, true);
    }


    public void keyTyped(KeyEvent e) {

         if (maxNumPlayers_.getIntValue() > 0) {
             numRobotPlayers_.setMax(maxNumPlayers_.getIntValue());
         }
    }

    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}

