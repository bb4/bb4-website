// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid;

import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;
import com.becker.ui.table.BasicCellRenderer;
import com.becker.ui.table.BasicTableModel;
import com.becker.ui.table.TableBase;
import com.becker.ui.table.TableColumnMeta;

import javax.swing.table.TableModel;


/**
 * Turns the list of search option configurations into a NxN grid
 * to compare the performance of all the search configurations in the list.
 * 
 * @author Barry Becker
 */
class ComparisonGrid extends TableBase {

    String[] colNames;


    /**
     * constructor
     * @param optionsList to initialize the rows in the table with. May be null.
     */
    public static ComparisonGrid createInstance(SearchOptionsConfigList optionsList)  {
        return new ComparisonGrid(optionsList, createColumnNames(optionsList));
    }

    /**
     * constructor
     * @param optionsList to initialize the rows in the table with. May be null.
     */
    private ComparisonGrid(SearchOptionsConfigList optionsList, String[] colNames)  {
        super(optionsList, colNames);
        this.colNames = colNames;
        this.initializeTable(optionsList);
    }
    
    private static String[] createColumnNames(SearchOptionsConfigList optionsList) {
        String[] names = new String[optionsList.size() + 1];
        names[0] = "---";
        int i=1;
        for (SearchOptionsConfig config : optionsList) {
             names[i++] = config.getName();
        }
        return names;
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[0].setPreferredWidth(210);
        columnMeta[0].setMaxWidth(310);
        columnMeta[0].setCellRenderer(new BasicCellRenderer());
        //for (int i = 1; i < getNumColumns(); i++) {
         //   columnMeta[i].setTooltip(columnTips_[i]);
        //}
    }

    @Override
    protected TableModel createTableModel(String[] columnNames) {
        return new BasicTableModel(columnNames, 0, false);
    }

    private BasicTableModel getPlayerModel() {
        return (BasicTableModel)getModel();
    }

    /**
     * add a row based on a player object
     * @param opts to add
     */
    @Override
    public void addRow(Object opts) {

        SearchOptionsConfig optionsConfig = (SearchOptionsConfig) opts;
        SearchOptions sOptions = optionsConfig.getSearchOptions();

        if (colNames == null)   return;

        Object[] d = new Object[colNames.length + 1];

        d[0] = optionsConfig.getName();
        for (int i=1; i<=colNames.length; i++) {
            d[i] = "...";
        }
        getPlayerModel().addRow(d);
    }

}
