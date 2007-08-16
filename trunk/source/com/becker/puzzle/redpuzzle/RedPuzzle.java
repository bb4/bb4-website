package com.becker.puzzle.redpuzzle;

import com.becker.common.*;
import com.becker.puzzle.common.Refreshable;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Red Puzzle
 * This program solves a 9 piece puzzle that has nubs on all 4 sides of every piece.
 * Its virtually impossible to solve by hand because of all the possible permutations.
 * This program can usually solve it by trying between 10,000 and 50,000 combinations.
 *
 * @author Barry becker
 */
public final class RedPuzzle extends JApplet 
                                             implements ChangeListener, ActionListener, ItemListener
                                                                 
{
    // shows the puzzle.
    private RedPuzzleViewer puzzleViewer_;
    private RedPuzzleController controller_;
    
    // allows you to change the animation speed.
    private LabeledSlider animSpeedSlider_;
    
    private JButton solveButton_;
    // size dropdown
    private Choice algorithmChoice_;

    /**
     * Construct the application and set the look and feel.
     */
    public RedPuzzle() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    public void init() {
        puzzleViewer_ = new RedPuzzleViewer();
        controller_ = new RedPuzzleController(puzzleViewer_);   

        animSpeedSlider_ = new LabeledSlider("Speed", RedPuzzleViewer.INITIAL_ANIM_SPEED, 1, RedPuzzleViewer.MAX_ANIM_SPEED);
        animSpeedSlider_.setResolution(RedPuzzleViewer.MAX_ANIM_SPEED - 1);
        animSpeedSlider_.setShowAsInteger(true);
        animSpeedSlider_.addChangeListener(this);
        //puzzleViewer_.setAnimationSpeed(INITIAL_ANIM_SPEED);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createButtonPanel(), BorderLayout.NORTH);
        panel.add(puzzleViewer_, BorderLayout.CENTER);
        panel.add(animSpeedSlider_, BorderLayout.SOUTH);

        getContentPane().add( panel );
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
        for (Algorithm a: Algorithm.values()) {
            algorithmChoice_.add(a.getLabel());
        }
        algorithmChoice_.select(0);
        return algorithmChoice_;
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {
        puzzleViewer_.setSize(this.getSize());
    }

    /**
     * stop and cleanup.
     */
    public void stop() {}


    public void stateChanged(ChangeEvent e) {
        puzzleViewer_.setAnimationSpeed((int) animSpeedSlider_.getValue());
    }

    /**
     * size choice selected.
     * @param e
     */
    public void itemStateChanged(ItemEvent e) {

        int selected = algorithmChoice_.getSelectedIndex();
        controller_.setAlgorithm(Algorithm.values()[selected]);
        puzzleViewer_.setAnimationSpeed(1);
        puzzleViewer_.repaint();
    }
    
    private void enableSolveButton(boolean enable) {
        solveButton_.setEnabled(enable);
        algorithmChoice_.setEnabled(enable);
    }

    public void actionPerformed(ActionEvent e) {
        // must execute long tasks in a separate thread,
        // otherwise you don't see the steps of the animation.        
        Object src = e.getSource();
        
        if (src == solveButton_)  {

            Worker worker = new Worker() {

                public Object construct() {
                    // we could run into state problems if you start again while running.
                    enableSolveButton(false);
                    puzzleViewer_.setAnimationSpeed((int) animSpeedSlider_.getValue());
                    controller_.startSolving();                    
                   
                    return null;
                }

                public void finished() {
                    puzzleViewer_.repaint();
                    enableSolveButton(true);                 
                }
            };

            worker.start();
        }
    }


    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        RedPuzzle applet = new RedPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Red Puzzle Solver");
    }

}
