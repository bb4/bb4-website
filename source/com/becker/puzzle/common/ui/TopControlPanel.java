/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.common.ui;

import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleController;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Shows the main button controls in a panel above the puzzle.
 *
 * @author Barry Becker
 */
public class TopControlPanel<P, M> extends JPanel
                                   implements ActionListener, ItemListener {
    protected PuzzleController<P, M> controller_;
    protected AlgorithmEnum<P, M>[] algorithmValues_;

    private JButton solveButton_;
    private Choice algorithmChoice_;

    /**
     * Constructor.
     */
    public TopControlPanel(PuzzleController<P, M> controller, AlgorithmEnum<P, M>[] algorithmValues) {
        controller_ = controller;
        algorithmValues_ = algorithmValues;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        solveButton_ = new GradientButton("Solve");
        solveButton_.addActionListener(this);

        add(solveButton_);
        add(createAlgorithmDropdown());
        add(Box.createHorizontalGlue());
    }

    /**
     * The dropdown menu at the top for selecting an algorithm for solving the puzzle.
     * @return a dropdown/down component.
     */
    private Choice createAlgorithmDropdown() {
        algorithmChoice_ = new Choice();
        algorithmChoice_.addItemListener(this);
        for (AlgorithmEnum a: algorithmValues_) {
            algorithmChoice_.add(a.getLabel());
        }
        algorithmChoice_.select(controller_.getAlgorithm().ordinal());
        return algorithmChoice_;
    }

    /**
     * algorithm selected.
     * @param e
     */
    public void itemStateChanged(ItemEvent e) {

        int selected = algorithmChoice_.getSelectedIndex();
        controller_.setAlgorithm(algorithmValues_[selected]);
    }

    /**
     * Solve button clicked.
     * Must execute long tasks in a separate thread,
     * otherwise you don't see the steps of the animation.
     */
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == solveButton_)  {
            controller_.startSolving();
        }
    }
}

