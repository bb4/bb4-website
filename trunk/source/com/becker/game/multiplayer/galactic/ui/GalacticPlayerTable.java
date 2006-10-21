package com.becker.game.multiplayer.galactic.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.galactic.*;

import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see com.becker.game.multiplayer.galactic.GalacticPlayer
 *
 * @author Barry Becker
 */
public class GalacticPlayerTable extends PlayerTable implements TableModelListener
{

    private static final int ICON_INDEX = 3;
    private static final int PLANET_INDEX = 4;
    private static final int SHIPS_INDEX = 5;
    private static final int PRODUCTION_INDEX = 6;

    private static final String ICON = GameContext.getLabel("ICON");
    private static final String HOME_PLANET = GameContext.getLabel("HOME_PLANET");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");

    /** hight enought o accommodate the icon. */
    private static final int ROW_HEIGHT = 30;


    private static final String[] galacticColumnNames_ =  {
         NAME,
         COLOR,
         HUMAN,
         ICON,
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
        table_.getModel().addTableModelListener(this);
    }

    protected void initializeTable() {
        super.initializeTable();
        table_.getColumn(ICON).setPreferredWidth(48);
        table_.getColumn(HOME_PLANET).setPreferredWidth(100);
        table_.getColumn(NUM_SHIPS).setPreferredWidth(100);
        table_.getColumn(PRODUCTION).setPreferredWidth(100);
        table_.setRowHeight(ROW_HEIGHT);
        table_.doLayout();
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
            char planetName = (Character) model.getValueAt(i, PLANET_INDEX);
            Planet planet = Galaxy.getPlanet(planetName);
            planet.setProductionCapacity((Integer) model.getValueAt(i, PRODUCTION_INDEX));
            planet.setNumShips((Integer) (model.getValueAt(i, SHIPS_INDEX)));
            ImageIcon icon = (ImageIcon) (model.getValueAt(i, ICON_INDEX));
            players[i] = GalacticPlayer.createGalacticPlayer(
                                    (String)model.getValueAt(i, NAME_INDEX),
                                    planet,
                                    (Color)model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean)model.getValueAt(i, HUMAN_INDEX)), icon);
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
        d[ICON_INDEX] = p.getIcon();
        d[PLANET_INDEX] = p.getHomePlanet().getName();
        d[SHIPS_INDEX] = p.getHomePlanet().getNumShips();
        d[PRODUCTION_INDEX] = p.getHomePlanet().getProductionCapacity();
        d[HUMAN_INDEX] = player.isHuman();
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

    /**
     * The user has switched from human to alien or vice versa.
     * @param e
     */
    public void tableChanged(TableModelEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("table changed " + e.getFirstRow() + " col="+ e.getColumn());
        if (e.getColumn() == HUMAN_INDEX)  {
            int row = e.getFirstRow();
            TableModel m = table_.getModel();
            boolean isHuman = (Boolean) m.getValueAt(row, HUMAN_INDEX);
            char c = (Character) m.getValueAt(row, PLANET_INDEX);
            Planet p = Galaxy.getPlanet(c);
            //Color color = (Color) m.getValueAt(row, COLOR_INDEX);
            // create a dummy player of the correct type and get the image icon.
            GalacticPlayer np =
                    GalacticPlayer.createGalacticPlayer("", p, Color.WHITE, isHuman);
            System.out.println("h="+isHuman);
            m.setValueAt(np.getIcon(), row, ICON_INDEX);
        }
    }
}
