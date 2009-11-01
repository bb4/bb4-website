package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Choice;
import com.becker.ui.table.*;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.table.*;


/**
 * Shows a list of the child scenes, allows editing the navigation text,
 * and allows navigating to them.
 *
 * @author Barry Becker
 */
public class ChildTable extends TableBase  {

    public static final String NAVIGATE_TO_CHILD_BUTTON_ID = "navToChild";
    public static final String DELETE_CHOICE_BUTTON_ID = "deleteChild";

    protected static final int DELETE_INDEX = 0;
    protected static final int NAVIGATE_INDEX = 1;
    protected static final int NAME_INDEX = 2;
    protected static final int ACTION_DESCRIPTION_INDEX = 3;

    protected static final String DELETE = "Delete";
    protected static final String NAVIGATE = "Navigate to";
    protected static final String NAME = "Name";
    protected static final String ACTION_DESCRIPTION = "Action Description";

    private static String[] childColumnNames_ =  {
       DELETE,
       NAME,
       NAVIGATE,
       ACTION_DESCRIPTION
    };

    private ActionListener actionListener_;


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public ChildTable(List<Choice> choices, ActionListener listener)
    {
        super(choices, childColumnNames_);
        actionListener_ = listener;
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Object choice)
    {
        Choice childChoice = (Choice) choice;
        Object d[] = new Object[getNumColumns()];
        d[DELETE_INDEX] = "x";
        d[NAME_INDEX] = childChoice.getDestination();
        d[ACTION_DESCRIPTION_INDEX] = childChoice.getDescription();
        getChildTableModel().addRow(d);
    }


    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta deleteCol = columnMeta[DELETE_INDEX];

        ButtonCellEditor deleteCellEditor =
                new ButtonCellEditor("x",  actionListener_, DELETE_CHOICE_BUTTON_ID);

        deleteCol.setCellRenderer(deleteCellEditor.getCellRenderer());
        deleteCol.setCellEditor(deleteCellEditor);
        deleteCol.setPreferredWidth(40);
        deleteCol.setPreferredWidth(80);

        TableColumnMeta navigateCol = columnMeta[NAVIGATE_INDEX];

        ButtonCellEditor navCellEditor =
                new ButtonCellEditor("Go",  actionListener_, NAVIGATE_TO_CHILD_BUTTON_ID);

        navigateCol.setCellRenderer(navCellEditor.getCellRenderer());
        navigateCol.setCellEditor(navCellEditor);
        navigateCol.setPreferredWidth(40);
        navigateCol.setMaxWidth(100);

        columnMeta[NAME_INDEX].setPreferredWidth(140);
        columnMeta[ACTION_DESCRIPTION_INDEX].setPreferredWidth(500);
    }



    protected TableModel createTableModel(String[] columnNames)  {
        return  new ChildTableModel(columnNames, 0);
    }


    protected DefaultTableModel getChildTableModel()
    {
        return (ChildTableModel)table_.getModel();
    }
}
