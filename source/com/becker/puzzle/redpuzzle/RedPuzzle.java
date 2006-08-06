package com.becker.puzzle.redpuzzle;

import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * Red Puzzle
 * This program solves a 9 piece puzzle that has nubs on all 4 sides of every piece.
 * Its virtually impossible to solve by hand because of all the possible permutations.
 * This program can usually solve it by trying between 10,000 and 50,000 combinations.
 *
 * @author Barry becker
 */
public final class RedPuzzle extends JApplet implements ChangeListener
{

    // shows the puzzle.
    private PuzzlePanel puzzlePanel_;
    // allows you to change the animation speed.
    private JSlider animSpeedSlider_;
    private static final int INITIAL_ANIM_SPEED = 20; // max = 100

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

        animSpeedSlider_ = new JSlider(1, PuzzleSolver.MAX_ANIM_SPEED, INITIAL_ANIM_SPEED);
        animSpeedSlider_.addChangeListener(this);
        puzzlePanel_.setAnimationSpeed(animSpeedSlider_.getValue());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(puzzlePanel_, BorderLayout.CENTER);
        panel.add(animSpeedSlider_, BorderLayout.SOUTH);

        getContentPane().add( panel );
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {

        puzzlePanel_.setSize(this.getSize());

        // if we don't solve in a separate thread the panel may not refresh initially.
        Thread thread = new Thread(new Runnable() {
            public void run() {
                puzzlePanel_.startSolving();
            }
        });

        thread.start();
    }

    /**
     * stop and cleanup.
     */
    public void stop() {}


    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        RedPuzzle applet = new RedPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Red Puzzle Solver");
    }

    public void stateChanged(ChangeEvent e) {
        puzzlePanel_.setAnimationSpeed(animSpeedSlider_.getValue());
    }
}
