package com.becker.puzzle.redpuzzle;

import com.becker.ui.*;

import javax.swing.*;

/**
 * Red Puzzle
 * This program solves a 9 piece puzzle that has nubs on all 4 sides of every piece.
 * Its virtually impossible to solve by hand because of all the possible permutations.
 * This program can usually solve it by trying between 10,000 and 50,000 combinations.
 *
 * @author Barry becker
 */
public final class RedPuzzle extends JApplet
{

    private PuzzlePanel puzzlePanel_;

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
        puzzlePanel_ = new PuzzlePanel();

        this.getContentPane().add( puzzlePanel_ );
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {

        puzzlePanel_.setSize(this.getSize());
        puzzlePanel_.startSolving();
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
        GUIUtil.showApplet( applet, "Red Puzzle Solver" );
    }
}
