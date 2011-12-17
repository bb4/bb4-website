/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.sudoku.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.sudoku.SudokuGenerator;
import com.becker.puzzle.sudoku.SudokuSolver;
import com.becker.puzzle.sudoku.model.board.Board;

import javax.swing.*;
import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
public final class SudokuPanel extends JPanel
                               implements RepaintListener {

    private SudokuRenderer renderer_;
    private UserInputListener inputListener;

    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    SudokuPanel(int[][] initialData) {
        this(new Board(initialData));
    }

    /**
     * Constructor.
     */
    private SudokuPanel(Board b) {
        renderer_ = new SudokuRenderer(b);
        inputListener = new UserInputListener(renderer_);
        inputListener.addRepaintListener(this);
        addMouseListener(inputListener);
        addKeyListener(inputListener);
    }

    public void setBoard(Board b) {
        renderer_.setBoard(b);
    }

    public void setShowCandidates(boolean show) {
        renderer_.setShowCandidates(show);
        repaint();
    }

    /** Mark the users values as correct or not. */
    public void validatePuzzle() {
        inputListener.validateValues(getSolvedPuzzle());
        inputListener.useCorrectEntriesAsOriginal(getBoard());
        repaint();
    }

    private Board getSolvedPuzzle()  {
        SudokuSolver solver = new SudokuSolver();
        Board boardCopy = new Board(getBoard());
        solver.solvePuzzle(boardCopy);
        return boardCopy;
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData starting values.
     */
    public void reset(int[][] initialData) {
        renderer_.setBoard(new Board(initialData));
        repaint();
    }

    public void startSolving(SudokuSolver solver) {
        boolean solved = solver.solvePuzzle(getBoard(), this);
        showMessage(solved);
        inputListener.clear();
    }

    private void showMessage(boolean solved) {
        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + getBoard().getNumIterations() );
        else
            System.out.println("This puzzle is not solvable!");
    }

    public void generateNewPuzzle(SudokuGenerator generator) {

        inputListener.clear();
        renderer_.setBoard(generator.generatePuzzleBoard());
        repaint();
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
        validatePuzzle();
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        renderer_.render(g, inputListener.getUserEnteredValues(),
                         inputListener.getCurrentCellLocation(), getWidth(), getHeight());
        // without this we do not get key events.
        requestFocus();
    }
}

