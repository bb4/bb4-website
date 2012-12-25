/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.InfoLabel;
import com.barrybecker4.game.common.ui.panel.RowEntryPanel;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.WinProbabilityCaclulator;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public class TwoPlayerInfoPanel extends GameInfoPanel implements GameChangedListener {


    /**
     * Constructor
     */
    public TwoPlayerInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected GeneralInfoPanel createGeneralInfoPanel(Player player) {
        return new TwoPlayerGeneralInfoPanel(player);
    }

    protected TwoPlayerController getController() {
        return (TwoPlayerController)controller_;
    }


    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {

        if ( controller_ == null ) {
            return;
        }
        generalInfoPanel_.update(controller_);
    }

}