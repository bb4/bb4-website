package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Scene;
import com.becker.ui.table.*;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.table.*;


/**
 * Shows a list of the parent scenes and allows navigating to them.
 *
 * @author Barry Becker
 */
public class ParentTable extends TableBase  {

    public static final String NAVIGATE_TO_PARENT_BUTTON_ID = "navToParent";

    protected static final int NAVIGATE_INDEX = 0;
    protected static final int NAME_INDEX = 1;
    protected static final int NUM_CHILDREN_INDEX = 2;

    protected static final String NAVIGATE = "Navigate to";
    protected static final String NAME = "Name";
    protected static final String NUM_CHILDREN = "Num Children";

    private ActionListener actionListener_;

    private static String[] parentColumnNames_ =  {
        NAVIGATE,
         NAME,
         NUM_CHILDREN
    };


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public ParentTable(List<Scene> scenes, ActionListener listener)  {
        super(scenes, parentColumnNames_);
        actionListener_ = listener;
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Object scene)
    {
        Scene parentScene = (Scene) scene;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = parentScene.getName();
        d[NUM_CHILDREN_INDEX] = parentScene.getChoices().size();
        getParentTableModel().addRow(d);
    }


    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta numChildrenMeta = columnMeta[NUM_CHILDREN_INDEX];

        // more space needed for the names list.
        columnMeta[NAME_INDEX].setPreferredWidth(200);

        TableColumnMeta navigateCol = columnMeta[NAVIGATE_INDEX];

        ButtonCellEditor navCellEditor =
                new ButtonCellEditor("Go",  actionListener_, NAVIGATE_TO_PARENT_BUTTON_ID);

        navigateCol.setCellRenderer(navCellEditor.getCellRenderer());
        navigateCol.setCellEditor(navCellEditor);
        navigateCol.setPreferredWidth(40);
        navigateCol.setMaxWidth(100);
     

        columnMeta[NUM_CHILDREN_INDEX].setMinWidth(40);
        columnMeta[NUM_CHILDREN_INDEX].setPreferredWidth(100);
    }



    protected TableModel createTableModel(String[] columnNames)  {
        return  new DefaultTableModel(columnNames, 0);
    }


    protected DefaultTableModel getParentTableModel()
    {
        return (DefaultTableModel)table_.getModel();
    }
}
