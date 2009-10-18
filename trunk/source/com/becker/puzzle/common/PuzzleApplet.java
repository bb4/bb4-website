package com.becker.puzzle.common;

import com.becker.ui.components.GradientButton;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Base class for Puzzle applets.
 *
 * @author Barry Becker  Date: Sep 2005
 */
public abstract class PuzzleApplet extends ApplicationApplet
                                                         implements ActionListener, ItemListener
{
    protected PuzzleController controller_;
    protected PuzzleViewer viewer_;

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

    protected abstract PuzzleViewer createViewer();

    protected abstract PuzzleController createController(Refreshable viewer);

    protected JPanel createCustomControls() {
        return null;
    }

    /**
     * solve and generate button at the top.
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
     *The dropdown menu at the top for selecting an algorithm for solving the puzzle.
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


    protected abstract AlgorithmEnum[] getAlgorithmValues();

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

