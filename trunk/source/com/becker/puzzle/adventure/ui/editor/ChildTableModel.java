package com.becker.puzzle.adventure.ui.editor;

import com.becker.game.multiplayer.common.ui.*;
import javax.swing.table.*;
import java.util.*;
import com.becker.puzzle.adventure.Scene;
import com.becker.puzzle.adventure.Choice;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see com.becker.game.multiplayer.common.ui.PlayerTable
 *
 * @author Barry Becker
 */
public class ChildTableModel extends DefaultTableModel
{

    public ChildTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    public ChildTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    /**
     * @param row row to check
     * @return true if last row in table.
     */
    public boolean isLastRow(int row) {
         return row == this.getRowCount() -1;
    }

    /**
     * Make the text for the scene choice descriptions match the scene passed in.
     * @param currentScene scene to update to.
     */
    public void updateSceneChoices(Scene currentScene) {
        for (int i=0; i<getRowCount()-1; i++) {
            String dest = (String) getValueAt(i, ChildTable.NAVIGATE_INDEX);
            Choice c =  currentScene.getChoices().getChoiceByDestination(dest);
            c.setDescription(getValueAt(i, ChildTable.CHOICE_DESCRIPTION_INDEX).toString());
        }
    }

    public String getDestinationSceneName(int row) {
        return (String) this.getValueAt(row, ChildTable.NAVIGATE_INDEX);
    }

    public String getChoiceDescription(int row) {
        return (String) this.getValueAt(row, ChildTable.CHOICE_DESCRIPTION_INDEX);
    }

    /**
     * Set the scene name of the current add row and add another add row.
     * @param addedSceneName  name pf the scene to add.
     */
    public void addNewChildChoice(String addedSceneName) {
        System.out.println("adding new scene :" + addedSceneName);
        setValueAt(ChildTable.DELETE_BUTTON_LABEL, getRowCount()-1, ChildTable.ACTION_INDEX);
        setValueAt(addedSceneName, getRowCount()-1, ChildTable.NAVIGATE_INDEX);

        Object d[] = new Object[this.getColumnCount()];
        d[ChildTable.ACTION_INDEX] = ChildTable.ADD_BUTTON_LABEL;
        d[ChildTable.NAVIGATE_INDEX] = ChildTable.ADD_BUTTON_LABEL;
        d[ChildTable.CHOICE_DESCRIPTION_INDEX] = ChildTable.NEW_CHOICE_DESC_LABEL;

        this.addRow(d);
        this.fireTableDataChanged();
        int numRows = this.getRowCount();
        this.fireTableRowsInserted(numRows-1, numRows);
        this.fireTableStructureChanged();
    }

    @Override
    public Class getColumnClass(int col)
    {
        List v = (List) dataVector.elementAt(0);
        return v.get(col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        boolean editableColumn = column == ChildTable.ACTION_INDEX
                || column == ChildTable.NAVIGATE_INDEX
                || column == ChildTable.CHOICE_DESCRIPTION_INDEX;
        return (editableColumn
                && !Choice.EXIT_DEST.equals(getValueAt(row, ChildTable.NAVIGATE_INDEX)));
    }
}
