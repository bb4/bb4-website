package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.common.ui.PlayerTableModel;
import com.becker.game.common.GameContext;
import com.becker.ui.HeaderRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.util.*;
import java.awt.geom.Point2D;


/**
 * OrdersTable contains a list of orders that a player has made to direct his ships.
 * All the cells are editable.
 * It is initialized with a list of Orders and returns a list of Orders.
 * @see Order
 *
 * @author Barry Becker
 */
public class OrdersTable
{
    private JTable table_;
    private List<Order> lastOrders_;

    private static final int ORIGIN_INDEX = 0;
    private static final int DESTINATION_INDEX = 1;
    private static final int NUM_SHIPS_INDEX = 2;
    private static final int DISTANCE_INDEX = 3;

    private static final String ORIGIN = GameContext.getLabel("ORIGIN");
    private static final String DESTINATION = GameContext.getLabel("DESTINATION");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String DISTANCE = GameContext.getLabel("ETA");

    private static final String[] columnNames_ =  {ORIGIN,
                                             DESTINATION,
                                             NUM_SHIPS,
                                             DISTANCE };

    private static final String ORIGIN_TIP = GameContext.getLabel("ORIGIN_TIP");
    private static final String DESTINATION_TIP = GameContext.getLabel("DESTINATION_TIP");
    private static final String NUM_SHIPS_TIP = GameContext.getLabel("NUM_SHIPS_TIP");
    private static final String DISTANCE_TIP = GameContext.getLabel("ETA_TIP");

    private static final String[] columnTips_ =  {ORIGIN_TIP,
                                             DESTINATION_TIP,
                                             NUM_SHIPS_TIP,
                                             DISTANCE_TIP };

    private static final int NUM_COLS = columnNames_.length;



    /**
     * constructor
     * @param orders to initialize the rows in the table with.
     */
    public OrdersTable(List<Order> orders)
    {
        initializeTable();
        lastOrders_ = orders;

        if (orders!=null) {
            for (int i=0; i<orders.size(); i++)  {
                Order order = orders.get(i);
                addRow(order);
            }
        }
        // add tooltips
        for (int i=0; i<columnNames_.length; i++) {
            TableCellRenderer r = new HeaderRenderer();
            table_.getColumn(columnNames_[i]).setHeaderRenderer(r);
            //System.out.println("r="+r);
            JComponent c = (JComponent)r.getTableCellRendererComponent(table_, columnNames_[i], false, false, 0, 0);
            c.setToolTipText(columnTips_[i]);
        }

    }

    private void initializeTable()
    {
        TableModel m = new PlayerTableModel(columnNames_, 0, false);
        table_ = new JTable(m);


    }

    public int getNumRows() {
        return table_.getRowCount();
    }

    public void removeRow(int rowIndex) {
         getModel().removeRow(rowIndex);
    }

    /**
     * @return  the players represented by rows in the table
     */
    public List<Order> getOrders()
    {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        List<Order> orders = new ArrayList<Order>(nRows);
        int numOldOrders = lastOrders_.size();

        for (int i=0; i<nRows; i++) {
            String origin = model.getValueAt(i,ORIGIN_INDEX).toString();
            String dest = model.getValueAt(i,DESTINATION_INDEX).toString();
            int numShips = ((Integer)model.getValueAt(i, NUM_SHIPS_INDEX));

            Order o;
            Planet originPlanet = Galaxy.getPlanet(origin.charAt(0));
            Planet destPlanet = Galaxy.getPlanet(dest.charAt(0));

            if (i < numOldOrders) {
                Point2D currLoc = (lastOrders_.get(i)).getCurrentLocation();
                o = new Order(originPlanet, destPlanet, numShips, currLoc);
            }
            else {
                o = new Order(Galaxy.getPlanet(origin.charAt(0)), destPlanet, numShips);
                originPlanet.deductShips(numShips);
            }

            orders.add( o );
        }

        return orders;
    }


    /**
     *
     * @return total outgoing ships for new orders (excluding existing)
     */
    protected Map getCurrentOutGoingShips()
    {
        Map<Planet,Integer> outgoingMap = new HashMap<Planet,Integer>();
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();

        int numOldOrders = lastOrders_.size();

        for (int i=numOldOrders; i<nRows; i++) {
            Character s = ((Character)model.getValueAt(i, ORIGIN_INDEX));
            Planet source = Galaxy.getPlanet(s.charValue());
            Integer numShips = ((Integer)model.getValueAt(i, NUM_SHIPS_INDEX));
            if (outgoingMap.get(source) != null) {
                Integer n = (Integer)outgoingMap.get(source);
                outgoingMap.put(source, numShips+n);
            }
            else {
                outgoingMap.put(source, numShips);
            }
        }
        return outgoingMap;
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
     * @param order to add
     */
    public void addRow(Order order)
    {
        Object d[] = new Object[NUM_COLS];
        d[ORIGIN_INDEX] = new Character( order.getOrigin().getName());
        d[DESTINATION_INDEX ] = new Character( order.getDestination().getName());
        d[NUM_SHIPS_INDEX] = order.getFleetSize();
        d[DISTANCE_INDEX] = new Float(order.getTimeRemaining());

        getModel().addRow(d);
    }

}
