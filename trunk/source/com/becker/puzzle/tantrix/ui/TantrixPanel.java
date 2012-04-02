// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.TantrixSolver;
import com.becker.puzzle.sudoku.ui.RepaintListener;
import com.becker.puzzle.tantrix.model.Board;
import com.becker.puzzle.tantrix.ui.TantrixBoardRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public final class TantrixPanel extends JPanel
                               implements RepaintListener {

    private TantrixBoardRenderer renderer_;

    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    TantrixPanel(int[][] initialData) {
        this(new Board(initialData));
    }

    /**
     * Constructor.
     */
    private TantrixPanel(Board b) {
        renderer_ = new TantrixBoardRenderer(b);
    }

    public void setBoard(Board b) {
        renderer_.setBoard(b);
    }


    private Board getSolvedPuzzle()  {
        /*
        TantrixSolver solver = new TantrixSolver();
        Board boardCopy = new Board(getBoard());
        solver.solvePuzzle(boardCopy);
        return boardCopy;    */
        return renderer_.getBoard();
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData starting values.
     */
    public void reset(int[][] initialData) {
        renderer_.setBoard(new Board(initialData));
        repaint();
    }

    public void startSolving(TantrixSolver solver) {
        //boolean solved = solver.solvePuzzle(getBoard(), this);
        //showMessage(solved);
    }


    private void showMessage(boolean solved) {
        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:");
                 //   + getBoard().getNumIterations() );
        else
            System.out.println("This puzzle is not solvable!");
    }

    public Board getBoard() {
        return renderer_.getBoard();
    }

    public void valueEntered() {
        repaint();
    }

    public void cellSelected(Location location) {
        repaint();
    }

    public void requestValidation() {
        //validatePuzzle();
    }
    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        renderer_.render(g, getWidth(), getHeight());
        // without this we do not get key events.
        requestFocus();
    }
}

