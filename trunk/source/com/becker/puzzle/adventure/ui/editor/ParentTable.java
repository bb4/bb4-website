package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Scene;
import com.becker.ui.table.TableBase;
import com.becker.ui.table.TableButton;
import com.becker.ui.table.TableButtonListener;
import com.becker.ui.table.TableColumnMeta;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;


/**
 * Shows a list of the parent scenes and allows navigating to them.
 *
 * @author Barry Becker
 */
class ParentTable extends TableBase  {

    public static final String NAVIGATE_TO_PARENT_BUTTON_ID = "navToParent";

    static final int NAVIGATE_INDEX = 0;
    private static final int NUM_CHILDREN_INDEX = 1;

    private static final String NAVIGATE = "Navigate to";
    private static final String NUM_CHILDREN = "Num Children";

    private static final String[] PARENT_COLUMN_NAMES =  {
        NAVIGATE,
         NUM_CHILDREN
    };

    private TableButtonListener tableButtonListener_;

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public ParentTable(List<Scene> scenes, TableButtonListener listener)  {
        initColumnMeta(PARENT_COLUMN_NAMES);
        tableButtonListener_ = listener;
        initializeTable(scenes);
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Object scene)
    {
        Scene parentScene = (Scene) scene;
        Object d[] = new Object[getNumColumns()];
        d[NAVIGATE_INDEX] = parentScene.getName();
        d[NUM_CHILDREN_INDEX] = parentScene.getChoices().size();
        getParentTableModel().addRow(d);
    }


    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta navigateCol = columnMeta[NAVIGATE_INDEX];

        TableButton navCellEditor = new TableButton(NAVIGATE_INDEX, NAVIGATE_TO_PARENT_BUTTON_ID);
        navCellEditor.addTableButtonListener(tableButtonListener_);

        navigateCol.setCellRenderer(navCellEditor);
        navigateCol.setCellEditor(navCellEditor);
        navigateCol.setPreferredWidth(210);
        navigateCol.setMaxWidth(500);

        columnMeta[NUM_CHILDREN_INDEX].setMinWidth(40);
        columnMeta[NUM_CHILDREN_INDEX].setPreferredWidth(100);
    }


    protected TableModel createTableModel(String[] columnNames)  {
        return new ParentTableModel(columnNames, 0);
    }


    DefaultTableModel getParentTableModel()
    {
        return (DefaultTableModel)table_.getModel();
    }
}
