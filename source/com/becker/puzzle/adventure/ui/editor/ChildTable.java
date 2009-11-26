package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Choice;
import com.becker.puzzle.adventure.ChoiceList;
import com.becker.ui.table.*;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.*;


/**
 * Shows a list of the child scenes, allows editing the navigation text,
 * and allows navigating to them.
 *
 * @author Barry Becker
 */
class ChildTable extends TableBase  {

    public static final String NEW_CHOICE_DESC_LABEL = " - Put your choice description here -";

    public static final String NAVIGATE_TO_CHILD_BUTTON_ID = "navToChild";
    public static final String ACTION_BUTTON_ID = "actOnChild";

    static final int ACTION_INDEX = 0;
    static final int NAVIGATE_INDEX = 1;
    static final int CHOICE_DESCRIPTION_INDEX = 2;

    private static final String ACTION = "Action";
    private static final String NAVIGATE = "Navigate to";
    private static final String CHOICE_DESCRIPTION = "Choice Description";

    static final String DELETE_BUTTON_LABEL = "delete";
    static final String ADD_BUTTON_LABEL = "- Add -";

    private static final String[] CHILD_COLUMN_NAMES =  {
         ACTION,
         NAVIGATE,
         CHOICE_DESCRIPTION
    };

    private TableButtonListener tableButtonListener_;

    /**
     * Constructor
     */
    public ChildTable(ChoiceList choices, TableButtonListener listener)
    {
        initColumnMeta(CHILD_COLUMN_NAMES);
        tableButtonListener_ = listener;
        initializeTable(choices);

        // add a final row which allows adding a new option
        Choice addChoice = new Choice(NEW_CHOICE_DESC_LABEL, ADD_BUTTON_LABEL);
        addRow(addChoice);
    }

    /**
     * Add a row based on a player object.
     */
    @Override protected void addRow(Object choice)
    {
        Choice childChoice = (Choice) choice;
        Object d[] = new Object[getNumColumns()];
        d[ACTION_INDEX] = ADD_BUTTON_LABEL;
        d[NAVIGATE_INDEX] = childChoice.getDestination();
        d[CHOICE_DESCRIPTION_INDEX] = childChoice.getDescription();
        if (getChildTableModel().getRowCount() > 0) {
            getChildTableModel().setValueAt(DELETE_BUTTON_LABEL,
                     getChildTableModel().getRowCount()-1, ACTION_INDEX);
        }
        getChildTableModel().addRow(d);
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta actionCol = columnMeta[ACTION_INDEX];

        TableButton actionCellEditor = new TableButton(ACTION_INDEX, ACTION_BUTTON_ID);
        actionCellEditor.addTableButtonListener(tableButtonListener_);
        actionCol.setCellRenderer(actionCellEditor);
        actionCol.setCellEditor(actionCellEditor);
        actionCol.setPreferredWidth(100);
        actionCol.setMaxWidth(200);

        TableColumnMeta navigateCol = columnMeta[NAVIGATE_INDEX];

        TableButton navCellEditor = new TableButton(NAVIGATE_INDEX, NAVIGATE_TO_CHILD_BUTTON_ID);

        List<Object> disabledList = new LinkedList<Object>();
        disabledList.add(Choice.EXIT_DEST);
        navCellEditor.setDisabledValues(disabledList);
        
        navCellEditor.addTableButtonListener(tableButtonListener_);
        actionCellEditor.setToolTipText("navigate to this scene");
        navigateCol.setCellRenderer(navCellEditor);
        navigateCol.setCellEditor(navCellEditor);
        navigateCol.setPreferredWidth(200);
        navigateCol.setMaxWidth(400);

        columnMeta[CHOICE_DESCRIPTION_INDEX].setPreferredWidth(500);
    }


    protected TableModel createTableModel(String[] columnNames)  {
        return  new ChildTableModel(columnNames, 0);
    }

    ChildTableModel getChildTableModel()
    {
        return (ChildTableModel)table_.getModel();
    }
}
