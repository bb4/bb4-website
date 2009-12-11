package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.Worker;
import com.becker.puzzle.sudoku.test.Data;
import com.becker.ui.GUIUtil;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * A combo box that allows the user to select the size of the puzzle
 *
 * @author Barry becker
 */
public final class SizeSelector extends Choice
{

    private String[] boardSizeMenuItems_ = {
        "4 cells on a side",
        "9 cells on a side",
        "16 cells on a side",
        "25 cells (prepare to wait)"
    };

    /**
     * Constructor.
     */
    public SizeSelector() {
        for (final String item : boardSizeMenuItems_) {
            add(item);
        }
        select(1);
    }

    /**
     * @return  the puzzle size for what was selected.
     */
    public int getSelectedSize() {
        return this.getSelectedIndex() + 2;
    }
}