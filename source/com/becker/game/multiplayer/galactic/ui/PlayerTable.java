package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.*;
import com.becker.game.common.*;
import com.becker.ui.ColorCellEditor;
import com.becker.ui.ColorCellRenderer;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.util.*;
import java.util.List;
import java.awt.*;


/**
 * PlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see com.becker.game.multiplayer.galactic.GalacticPlayer
 *
 * @author Barry Becker
 */
public class PlayerTable
{
    private JTable table_;

    // remember the deleted rows, so we can add them back when the user clicks add again
    private List deletedRows_;

    private static final int NAME_INDEX = 0;
    private static final int COLOR_INDEX = 1;
    private static final int PLANET_INDEX = 2;
    private static final int SHIPS_INDEX = 3;
    private static final int PRODUCTION_INDEX = 4;
    private static final int TYPE_INDEX = 5;

    private static final String NAME = GameContext.getLabel("NAME");
    private static final String COLOR = GameContext.getLabel("COLOR");
    private static final String HOME_PLANET = GameContext.getLabel("HOME_PLANET");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");
    private static final String HUMAN = GameContext.getLabel("HUMAN");

    private static String[] columnNames_ =  {NAME,
                                             COLOR,
                                             HOME_PLANET,
                                             NUM_SHIPS,
                                             PRODUCTION,
                                             HUMAN};

    private static int NUM_COLS = columnNames_.length;

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public PlayerTable(GalacticPlayer[] players)
    {
        deletedRows_ = new ArrayList();

        initializeTable();

        for (int i=0; i<players.length; i++)  {
            GalacticPlayer p = players[i];
            addRow(p);
        }

    }

    private void initializeTable()
    {
        TableModel m = new PlayerTableModel(columnNames_, 0, true);
        table_ = new JTable(m);

        TableColumn colColumn = table_.getColumn(COLOR);
        colColumn.setCellRenderer(new ColorCellRenderer());
        colColumn.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
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

    public JTable getTable()
    {
        return table_;
    }

    public void addListSelectionListener(ListSelectionListener l)
    {
        table_.getSelectionModel().addListSelectionListener(l);
    }

    private PlayerTableModel getModel()
    {
        return (PlayerTableModel)table_.getModel();
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    private void addRow(GalacticPlayer player)
    {
        Object d[] = new Object[NUM_COLS];
        d[NAME_INDEX] = player.getName();
        d[COLOR_INDEX ] = player.getColor();
        d[PLANET_INDEX] = new Character(player.getHomePlanet().getName());
        d[SHIPS_INDEX] = new Integer(player.getHomePlanet().getNumShips());
        d[PRODUCTION_INDEX] = new Integer(player.getHomePlanet().getProductionCapacity());
        d[TYPE_INDEX] = new Boolean(player.isHuman());
        //data[i] = d;
        getModel().addRow(d);
    }

    /**
     * add another row to the end of the table.
     */
    public void addRow()
    {
        if (deletedRows_.isEmpty()) {
            int ct = table_.getRowCount();
            Planet planet = new Planet((char)('A'+ct), GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new Location(0,0));
            Color newColor = GalacticPlayer.getNewPlayerColor((GalacticPlayer[])getPlayers());
            GalacticPlayer player = GalacticPlayer.createGalacticPlayer(
                                             "Admiral "+(ct+1), planet, newColor, true);
            planet.setOwner(player);
            addRow(player);
        }
        else
            getModel().addRow((Vector)deletedRows_.remove(0));
    }

    /**
     * remove the selected rows from the table
     */
    public void removeSelectedRows()
    {
        int nSelected = table_.getSelectedRowCount();
        int[] selectedRows = table_.getSelectedRows();
        for (int i=nSelected-1; i>=0; i--) {
            int selRow = selectedRows[i];
            deletedRows_.add(getModel().getDataVector().elementAt(selRow));
            getModel().removeRow(selRow);
        }
    }
}
