package com.becker.puzzle.adventure.ui.editor;

import com.becker.common.util.OrderedMap;
import com.becker.game.multiplayer.common.ui.*;
import javax.swing.table.*;
import java.util.*;
import com.becker.puzzle.adventure.Scene;
import com.becker.puzzle.adventure.Choice;


/**
 * Basically the DefaultTableModel with a few customizations.
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
     * Make the text for the scene choice descriptions match the scene passed in.
     * Also the order may have changed, so that needs to be checked as well.
     * @param currentScene scene to update to.
     */
    public void updateSceneChoices(Scene currentScene) {
        OrderedMap<String, String> choiceMap = new OrderedMap<String, String>();

        System.out.println("num choices in table="+ getRowCount()); 
        for (int i = 0; i < getRowCount(); i++) {
            String dest = (String) getValueAt(i, ChildTable.NAVIGATE_INDEX);
            choiceMap.put(dest, getValueAt(i, ChildTable.CHOICE_DESCRIPTION_INDEX).toString());
        }
        currentScene.getChoices().update(choiceMap);
    }

    public String getChoiceDescription(int row) {
        return (String) this.getValueAt(row, ChildTable.CHOICE_DESCRIPTION_INDEX);
    }

    /**
     * Set the scene name of the current add row and add another add row.
     * @param row location to add the new choice
     * @param addedSceneName  name pf the scene to add.
     */
    public void addNewChildChoice(int row, String addedSceneName) {
        System.out.println("adding new scene :" + addedSceneName + " at " + row);

        Object d[] = new Object[this.getColumnCount()];
        d[ChildTable.NAVIGATE_INDEX] = addedSceneName;
        d[ChildTable.CHOICE_DESCRIPTION_INDEX] = ChildTable.NEW_CHOICE_DESC_LABEL;
        this.insertRow(row, d);

        this.fireTableRowsInserted(row, row);  // need this
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
        return true;
    }
}
