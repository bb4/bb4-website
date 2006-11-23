package com.becker.puzzle.redpuzzle;

import com.becker.common.*;
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
public final class RedPuzzle extends JApplet implements ChangeListener, ActionListener, ItemListener
{

    // shows the puzzle.
    private PuzzlePanel puzzlePanel_;
    // allows you to change the animation speed.
    private LabeledSlider animSpeedSlider_;
    private static final int INITIAL_ANIM_SPEED = 20; // max = 100

    private JButton solveButton_;
    // size dropdown
    private Choice algorithmChoice_;
    private String[] boardSizeMenuItems_ = {
        "using brute force",
        "using genetic algorithm search",
    };

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
        puzzlePanel_ = new PuzzlePanel(9);

        animSpeedSlider_ = new LabeledSlider("Speed", INITIAL_ANIM_SPEED, 1, PuzzleSolver.MAX_ANIM_SPEED);
        animSpeedSlider_.setResolution(PuzzleSolver.MAX_ANIM_SPEED - 1);
        animSpeedSlider_.setShowAsInteger(true);
        animSpeedSlider_.addChangeListener(this);
        puzzlePanel_.setAnimationSpeed(INITIAL_ANIM_SPEED);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createButtonPanel(), BorderLayout.NORTH);
        panel.add(puzzlePanel_, BorderLayout.CENTER);
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

    private Choice createAlgorithmDropdown() {
        algorithmChoice_ = new Choice();
        algorithmChoice_.addItemListener(this);
        for (final String item : boardSizeMenuItems_) {
            algorithmChoice_.add(item);
        }
        algorithmChoice_.select(0);
        return algorithmChoice_;
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {

        puzzlePanel_.setSize(this.getSize());
    }

    /**
     * stop and cleanup.
     */
    public void stop() {}


    public void stateChanged(ChangeEvent e) {
        puzzlePanel_.setAnimationSpeed((int) animSpeedSlider_.getValue());
    }

    /**
     * size choice selected.
     * @param e
     */
    public void itemStateChanged(ItemEvent e) {

        int selected = algorithmChoice_.getSelectedIndex();

        PuzzlePanel.Algorithm alg;
        switch (selected) {
            case 0 : alg = PuzzlePanel.Algorithm.BRUTE_FORCE; break;
            case 1 : alg = PuzzlePanel.Algorithm.GENETIC_SEARCH; break;
            default : alg = PuzzlePanel.Algorithm.BRUTE_FORCE; break;
        }

        puzzlePanel_.setAlgorithm(alg);
        puzzlePanel_.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        // must execute long tasks in a separate thread,
        // otherwise you don't see the steps of the animation.
        Worker worker;
        Object src = e.getSource();
        if (src == solveButton_)  {

            worker = new Worker() {

                public Object construct() {
                    puzzlePanel_.setAnimationSpeed((int) animSpeedSlider_.getValue());
                    puzzlePanel_.startSolving();

                    return null;
                }

                public void finished() {
                    puzzlePanel_.repaint();
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
