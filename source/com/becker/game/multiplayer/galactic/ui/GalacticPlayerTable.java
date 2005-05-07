package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.common.*;

import javax.swing.table.*;
import java.awt.*;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see com.becker.game.multiplayer.galactic.GalacticPlayer
 *
 * @author Barry Becker
 */
public class GalacticPlayerTable extends PlayerTable
{

    private static final int PLANET_INDEX = 3;
    private static final int SHIPS_INDEX = 4;
    private static final int PRODUCTION_INDEX = 5;

    private static final String HOME_PLANET = GameContext.getLabel("HOME_PLANET");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");


    private static final String[] galacticColumnNames_ =  {
         NAME,
         COLOR,
         HUMAN,
         HOME_PLANET,
         NUM_SHIPS,
         PRODUCTION
    };


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public GalacticPlayerTable(Player[] players)
    {
        super(players, galacticColumnNames_);
    }


    /**
     * @return  the players represented by rows in the table
     */
    public Player[] getPlayers()
    {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        Player[] players = new GalacticPlayer[nRows];
        for (int i=0; i<nRows; i++) {
            char planetName = ((Character)model.getValueAt(i,PLANET_INDEX)).charValue();
            Planet planet = Galaxy.getPlanet(planetName);
            planet.setProductionCapacity( ((Integer)model.getValueAt(i, PRODUCTION_INDEX)).intValue());
            planet.setNumShips(((Integer)model.getValueAt(i, SHIPS_INDEX)).intValue());
            players[i] = GalacticPlayer.createGalacticPlayer(
                                    (String)model.getValueAt(i, NAME_INDEX),
                                    planet,
                                    (Color)model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean)model.getValueAt(i, TYPE_INDEX)).booleanValue());
        }
        return players;
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Player player)
    {
        GalacticPlayer p = (GalacticPlayer) player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = player.getName();
        d[COLOR_INDEX ] = player.getColor();
        d[PLANET_INDEX] = new Character(p.getHomePlanet().getName());
        d[SHIPS_INDEX] = new Integer(p.getHomePlanet().getNumShips());
        d[PRODUCTION_INDEX] = new Integer(p.getHomePlanet().getProductionCapacity());
        d[TYPE_INDEX] = new Boolean(player.isHuman());
        //data[i] = d;
        getModel().addRow(d);
    }

    protected Player createPlayer() {
        int ct = table_.getRowCount();
        Planet planet = new Planet((char)('A'+ct), GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new Location(0,0));
        Color newColor = GalacticPlayer.getNewPlayerColor((GalacticPlayer[])getPlayers());
        GalacticPlayer player = GalacticPlayer.createGalacticPlayer(
                                             "Admiral "+(ct+1), planet, newColor, true);
        planet.setOwner(player);
        return player;
    }

}
