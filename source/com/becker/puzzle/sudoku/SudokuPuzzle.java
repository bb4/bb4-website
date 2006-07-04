package com.becker.puzzle.sudoku;

import com.becker.ui.*;
import com.becker.puzzle.sudoku.test.*;

import javax.swing.*;

/**
 * Sudoku Puzzle
 * This program solves a Sudoku puzzle.
 * Its difficult to solve by hand because of all the possible permutations.
 *
 * @author Barry becker
 */
public final class SudokuPuzzle extends JApplet
{

    private PuzzlePanel puzzlePanel_;

    /**
     * Construct the application and set the look and feel.
     */
    public SudokuPuzzle() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * create and initialize the puzzle
     * (init required for applet)
     */
    public void init() {
        puzzlePanel_ = new PuzzlePanel(Data.SAMPLE1);

        this.getContentPane().add( puzzlePanel_ );
    }

    /**
     * start solving the puzzle.
     * called by the browser after init(), if running as an applet
     */
    public void start() {

        puzzlePanel_.repaint();
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

        SudokuPuzzle applet = new SudokuPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Sudoku Puzzle Solver" );
    }
}
