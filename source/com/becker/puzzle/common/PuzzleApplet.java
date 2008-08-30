package com.becker.puzzle.common;

import com.becker.puzzle.common.Refreshable;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Base class for Puzzle applets.
 * @author Barry Becker
 */
public abstract class PuzzleApplet extends JApplet 
                                                 implements ActionListener, ItemListener
{
    
    protected PuzzleController controller_;
    protected PuzzleViewer viewer_;
    private ResizableAppletPanel resizablePanel_ = null;
    
    private JButton solveButton_;
    private Choice algorithmChoice_;

    /**
     * Construct the application
     */
    public PuzzleApplet() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    public void init() {

        viewer_ = createViewer();
        controller_ = createController(viewer_);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        resizablePanel_ = new ResizableAppletPanel(mainPanel);
        
        mainPanel.add(createButtonPanel(), BorderLayout.NORTH);
        mainPanel.add(viewer_, BorderLayout.CENTER);
        JPanel customControls = createCustomControls();
        if (customControls != null) {
            mainPanel.add(customControls, BorderLayout.SOUTH);
        }
             
        getContentPane().add(resizablePanel_);
        getContentPane().setPreferredSize(viewer_.getPreferredSize());
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
        algorithmChoice_.select(0);
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
    
    /**
     * called by the browser after init(), if running as an applet
     */
    public void start() {}

    /**
     * stop and cleanup.
     */
    public void stop() {}
    
}

