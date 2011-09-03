package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.Worker;
import com.becker.ui.components.GradientButton;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Buttons at the top for generating and solving the puzzle using different strategies.
 *
 * @author Barry becker
 */
public final class TopControlPanel extends JPanel
                                   implements ActionListener, ItemListener {
    private SudokuController controller_;

    /** click this button to generate a new puzzle */
    private JButton generateButton_;
    /** click this button to solve the current puzzle. */
    private JButton solveButton_;

    private SizeSelector sizeSelector_;
    private SpeedSelector speedSelector_;

    /**
     * The solve and generate button at the top.
     */
    public TopControlPanel(SudokuController controller) {

        controller_ = controller;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        generateButton_ = new GradientButton("Generate");
        generateButton_.addActionListener(this);
        solveButton_ = new GradientButton("Solve");
        solveButton_.addActionListener(this);

        add(generateButton_);
        add(solveButton_);
        sizeSelector_ = new SizeSelector();
        speedSelector_ = new SpeedSelector();
        sizeSelector_.addItemListener(this);
        speedSelector_.addItemListener(this);
        add(sizeSelector_);
        add(speedSelector_);
        add(Box.createHorizontalGlue());
    }

    /**
     * Must execute long tasks in a separate thread,
     * otherwise you don't see the steps of the animation.
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == generateButton_)  {
            generatePuzzle(speedSelector_.getSelectedDelay());
        }
        else if (src == solveButton_)  {
            solvePuzzle();
        }
    }

    private void generatePuzzle(final int delay) {
        controller_.generatePuzzle(delay, sizeSelector_.getSelectedSize());
        solveButton_.setEnabled(true);
    }

    /** */
    private void solvePuzzle() {
        controller_.solvePuzzle(speedSelector_.getSelectedDelay());
        solveButton_.setEnabled(false);
    }

    /**
     * size choice selected.
     * @param e  item event.
     */
    public void itemStateChanged(ItemEvent e) {

        if (e.getSource() == sizeSelector_)  {
            generatePuzzle(10);
        }
    }
}
