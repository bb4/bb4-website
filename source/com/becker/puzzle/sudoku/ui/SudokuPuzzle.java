/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.sudoku.ui;

import com.becker.puzzle.sudoku.Data;
import com.becker.puzzle.sudoku.SudokuController;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Sudoku Puzzle UI.
 * This program can generate and solve Sudoku puzzles.
 *
 * @author Barry becker
 */
public final class SudokuPuzzle extends JApplet {

    private SudokuController controller_;
    private TopControlPanel topControls_;
    private SudokuPanel puzzlePanel_;


    /**
     * Construct the application and set the look and feel.
     */
    public SudokuPuzzle() {
        GUIUtil.setCustomLookAndFeel();
    }

    /**
     * Create and initialize the puzzle.
     * (init required for applet)
     */
    @Override
    public void init() {
        puzzlePanel_ = new SudokuPanel(Data.HARDEST_9);
        controller_ = new SudokuController(puzzlePanel_);
        topControls_ = new TopControlPanel(controller_);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(topControls_, BorderLayout.NORTH);
        panel.add(puzzlePanel_, BorderLayout.CENTER);
        getContentPane().add(panel);
    }

    /**
     * Called by the browser after init(), if running as an applet.
     */
    @Override
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getContentPane().repaint();
            }
        });
    }

    /**
     * use this to run as an application instead of an applet.
     */
    public static void main( String[] args )  {

        SudokuPuzzle applet = new SudokuPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet( applet, "Sudoku Puzzle Solver" );
    }
}
