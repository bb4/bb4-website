package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.MultiGamePlayer;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.galactic.*;

import java.util.List;
import javax.swing.JFrame;



/**
 * Show a summary of the final results.
 * We will show how many planets and how many ships each remaining player has.
 * The winner is the player with the most planets.
 * If there are more than one player with the same number of planets,
 * then the number of ships will be used to break ties.
 *
 * @author Barry Becker
 */
final class GalacticTallyDialog extends TallyDialog
{
    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param controller
     */
    GalacticTallyDialog( JFrame parent, GalacticController controller )
    {
        super( parent, controller );
    }

    @Override
    protected SummaryTable createSummaryTable(PlayerList players) {
        return new GalacticSummaryTable(players);
    }

    /**
     * 
     * @param players
     * @return the player with the most planets (num ships used only as a tie breaker).
     */
    @Override
    public MultiGamePlayer findWinner(PlayerList players)
    {
        return controller_.determineWinner();        
    }

}

