// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui;

import com.becker.game.common.GameContext;
import com.becker.game.multiplayer.common.ui.PlayerTableModel;
import com.becker.game.multiplayer.galactic.Galaxy;
import com.becker.game.multiplayer.galactic.Order;
import com.becker.game.multiplayer.galactic.Planet;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.ui.table.TableBase;
import com.becker.ui.table.TableColumnMeta;

import javax.swing.table.TableModel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Contains a list of search option configurations .
 * 
 * @author Barry Becker
 */
class ConfigurationsTable extends TableBase {

    private List<SearchOptions> lastOptions_;

    private static final int NAME_INDEX = 0;
    private static final int ALGORITHM_INDEX = 1;
    private static final int OPTIONS_INDEX = 2;

    private static final String NAME = "Name";
    private static final String ALGORITHM = "Search Algorithm";
    private static final String OPTIONS = "Options";

    private static final String[] columnNames_ =  {
            NAME, ALGORITHM, OPTIONS 
    };

    private static final String NAME_TIP = "Some descriptive name";
    private static final String ALGORITHM_TIP = "Name of the search algorithm";
    private static final String OPTIONS_TIP = "Searialization of the algorithm options";

    private static final String[] columnTips_ =  {
            NAME_TIP, ALGORITHM_TIP, OPTIONS_TIP};

    private static final int NUM_COLS = columnNames_.length;

    /**
     * default constructor
     */
    public ConfigurationsTable()  {
        this(null);
    }

    /**
     * constructor
     * @param optionsList to initialize the rows in the table with. May be null.
     */
    public ConfigurationsTable(List<SearchOptions> optionsList)  {
        super(optionsList, columnNames_);
        lastOptions_ = optionsList;
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        for (int i = 0; i < getNumColumns(); i++) {
            columnMeta[i].setTooltip(columnTips_[i]);
        }
    }

    @Override
    protected TableModel createTableModel(String[] columnNames) {
        return  new PlayerTableModel(columnNames_, 0, false);
    }

    public void removeRow(int rowIndex) {
         getPlayerModel().removeRow(rowIndex);
    }

    /**
     * @return the search options represented by rows in the table
     */
    public List<SearchOptions> getSearchOptions() {

        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        List<SearchOptions> searchOptions = new ArrayList<SearchOptions>(nRows);
        int numOldOrders = lastOptions_.size();

        for (int i = 0; i < nRows; i++) {
            String name = model.getValueAt(i, NAME_INDEX).toString();
            String algorithm = model.getValueAt(i, ALGORITHM_INDEX).toString();
            SearchOptions options = ((SearchOptions)model.getValueAt(i, OPTIONS_INDEX));
            searchOptions.add( options );
        }

        return searchOptions;
    }

    private PlayerTableModel getPlayerModel() {
        return (PlayerTableModel)getModel();
    }

    /**
     * add a row based on a player object
     * @param opts to add
     */
    @Override
    public void addRow(Object opts) {

        SearchOptions options = (SearchOptions) opts;
        Object d[] = new Object[NUM_COLS];

        d[NAME_INDEX] = "some name";
        d[ALGORITHM_INDEX] = options.getSearchStrategyMethod();
        d[OPTIONS_INDEX ] = options.toString();

        getPlayerModel().addRow(d);
    }

}
