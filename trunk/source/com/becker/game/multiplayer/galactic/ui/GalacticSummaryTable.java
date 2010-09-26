package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.PlayerList;
import com.becker.game.multiplayer.common.ui.SummaryTable;
import com.becker.game.multiplayer.galactic.Galaxy;
import com.becker.game.multiplayer.galactic.player.GalacticPlayer;

import java.util.List;


/**
 * Shows a list of the players stats at the end of the game.
 * None of the cells are editable.
 * @see com.becker.game.multiplayer.galactic.player.GalacticPlayer
 *
 * @author Barry Becker
 */
public class GalacticSummaryTable extends SummaryTable
{

    private static final int NUM_PLANETS_INDEX = 2;
    private static final int SHIPS_INDEX = 3;
    private static final int PRODUCTION_INDEX = 4;   // total over all owned planets

    private static final String NUM_PLANETS = GameContext.getLabel("NUM_PLANETS");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");

    private static final String[] COLUMN_NAMES =  {NAME,
                                             COLOR,
                                             NUM_PLANETS,
                                             NUM_SHIPS,
                                             PRODUCTION };

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public GalacticSummaryTable(PlayerList players)
    {
        super(players, COLUMN_NAMES);
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    @Override
    protected void addRow(Object player)
    {
        GalacticPlayer p = (GalacticPlayer)player;
        Object d[] = new Object[getNumColumns()];
        List planets = Galaxy.getPlanets(p);
        // sum the num ships and productions

        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX ] = p.getColor();
        d[NUM_PLANETS_INDEX] = planets.size();
        d[SHIPS_INDEX] =  p.getTotalNumShips();
        d[PRODUCTION_INDEX] = p.getTotalProductionCapacity();
        getPlayerModel().addRow(d);
    }
}
