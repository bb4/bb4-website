package com.becker.puzzle.common;

import com.becker.ui.ApplicationApplet;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Base class for Puzzle applets.
 *
 * @author Barry Becker  Date: Sep 2005
 */
public abstract class PuzzleApplet<P, M> extends ApplicationApplet
                                  implements ActionListener, ItemListener
{
    protected PuzzleController<P, M> controller_;
    protected PuzzleViewer<P, M> viewer_;

    private JButton solveButton_;
    private Choice algorithmChoice_;

    /**
     * Construct the application.
     */
    public PuzzleApplet() {}

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    @Override
    protected JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        viewer_ = createViewer();
        controller_ = createController(viewer_);

        mainPanel.add(createButtonPanel(), BorderLayout.NORTH);
        mainPanel.add(viewer_, BorderLayout.CENTER);
        JPanel customControls = createCustomControls();
        if (customControls != null) {
            mainPanel.add(customControls, BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    protected abstract PuzzleViewer<P, M> createViewer();

    protected abstract PuzzleController<P, M> createController(Refreshable<P, M> viewer);

    protected JPanel createCustomControls() {
        return null;
    }

    /**
     * solve and generate button at the top.
     * @return panel will solve button and other interface controls.
     */
    public JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        solveButton_ = new GradientButton("Solve");
        solveButton_.addActionListener(this);

        panel.add(solveButton_);
        panel.add(createAlgorithmDropdown());
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    /**
     * The dropdown menu at the top for selecting an algorithm for solving the puzzle.
     */
    private Choice createAlgorithmDropdown() {
        algorithmChoice_ = new Choice();
        algorithmChoice_.addItemListener(this);
        for (AlgorithmEnum a: getAlgorithmValues()) {
            algorithmChoice_.add(a.getLabel());
        }
        algorithmChoice_.select(controller_.getAlgorithm().ordinal());
        return algorithmChoice_;
    }


    protected abstract AlgorithmEnum<P, M>[] getAlgorithmValues();

    /**
     * algorithm selected.
     * @param e
     */
    public void itemStateChanged(ItemEvent e) {

        int selected = algorithmChoice_.getSelectedIndex();
        controller_.setAlgorithm(getAlgorithmValues()[selected]);
    }

    /**
     *Solve button clicked.
     */
    public void actionPerformed(ActionEvent e) {
        // must execute long tasks in a separate thread,
        // otherwise you don't see the steps of the animation.
        Object src = e.getSource();

        if (src == solveButton_)  {
            controller_.startSolving();
        }
    }
}

