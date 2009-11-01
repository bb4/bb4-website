package com.becker.ui.table;


import com.becker.ui.components.GradientButton;
import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 17, 2006
 */
public class TableButton extends GradientButton {

    private int row_;
    private String id_;

    public TableButton(String label) {

        super(label);
        setHorizontalAlignment(JLabel.CENTER);
        setPreferredSize(new Dimension(100, 18));
    }

    /**
     * Some string that we can use to identify this button in an action handler.
     * @param id
     */
    public void setId(String id) {
        id_ = id;
    }

    public String getId() {
        return id_;
    }

    public int getRow() {
        return row_;
    }

    public void setRow(int row) {
        row_ = row;
    }

    @Override
    public String toString() {
        return "TableButton row = "+getRow() ;
    }
}
