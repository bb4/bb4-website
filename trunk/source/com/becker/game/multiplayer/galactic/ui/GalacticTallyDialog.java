package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.galactic.player.*;

import java.awt.*;
import java.util.List;



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
    GalacticTallyDialog( Frame parent, GalacticController controller )
    {
        super( parent, controller );
    }

    protected SummaryTable createSummaryTable(Player[] players) {
        return new GalacticSummaryTable(players);
    }

    protected String findWinner(Player[] players)
    {
        String winner ="nobody";
        double maxCriteria = -1.0;
        for (final Player newVar : players) {
            GalacticPlayer player = (GalacticPlayer) newVar;
            List planets = Galaxy.getPlanets(player);
            double criteria = planets.size() + (double) player.getTotalNumShips() / 100000000000.0;

            if (criteria > maxCriteria) {
                maxCriteria = criteria;
                winner = player.getName();
            }
        }
        return winner;
    }

}

