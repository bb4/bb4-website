package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Choice;
import com.becker.ui.table.*;
import java.awt.event.ActionListener;
import java.util.List;
import com.becker.puzzle.adventure.Scene;

import javax.swing.table.*;


/**
 * Shows a list of the child scenes, allows editing the navigation text,
 * and allows navigating to them.
 *
 * @author Barry Becker
 */
public class ChildTable extends TableBase  {

    public static final String NAVIGATE_TO_CHILD_BUTTON_ID = "navToChild";
    public static final String ACTION_BUTTON_ID = "actOnChild";

    protected static final int ACTION_INDEX = 0;
    protected static final int NAVIGATE_INDEX = 1;
    protected static final int CHOICE_DESCRIPTION_INDEX = 2;

    protected static final String ACTION = "Action";
    protected static final String NAVIGATE = "Navigate to";
    protected static final String CHOICE_DESCRIPTION = "Choice Description";

    private static final String[] CHILD_COLUMN_NAMES =  {
         ACTION,
         NAVIGATE,
         CHOICE_DESCRIPTION
    };

    private TableButtonListener tableButtonListener_;

    /**
     * Constructor
     * @param players to initializet the rows in the table with.
     */
    public ChildTable(List<Choice> choices, TableButtonListener listener)
    {
        initColumnMeta(CHILD_COLUMN_NAMES);
        tableButtonListener_ = listener;
        initializeTable(choices);

        // add a final row which allows adding a new option
        Choice addChoice = new Choice(" - Put your choice description here -", "-add new scene-");
        addRow(addChoice);
    }

    public void updateSceneChoices(Scene currentScene) {
        DefaultTableModel children = getChildTableModel();
        for (int i=0; i<children.getRowCount()-1; i++) {
            String dest = children.getValueAt(i, ChildTable.NAVIGATE_INDEX).toString();
            Choice c =  currentScene.getChoiceByDestination(dest);
            c.setDescription(children.getValueAt(i, ChildTable.CHOICE_DESCRIPTION_INDEX).toString());
        }
    }

    /**
     * Add a row based on a player object.
     * @param player to add
     */
    protected void addRow(Object choice)
    {
        Choice childChoice = (Choice) choice;
        Object d[] = new Object[getNumColumns()];
        d[ACTION_INDEX] = "add";
        d[NAVIGATE_INDEX] = childChoice.getDestination();
        d[CHOICE_DESCRIPTION_INDEX] = childChoice.getDescription();
        if (getChildTableModel().getRowCount() > 0) {
            getChildTableModel().setValueAt("delete", getChildTableModel().getRowCount()-1, ACTION_INDEX);
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

    protected DefaultTableModel getChildTableModel()
    {
        return (ChildTableModel)table_.getModel();
    }
}
