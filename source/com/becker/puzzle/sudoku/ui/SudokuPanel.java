package com.becker.puzzle.sudoku.ui;

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
public final class SudokuPanel extends JPanel {

    private Board board_;
    private SudokuRenderer renderer_;

    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    SudokuPanel(int[][] initialData) {
        this(new Board(initialData));
        renderer_ = new SudokuRenderer();
    }

    /**
     * Constructor.
     */
    SudokuPanel(Board b) {
        board_ = b;
    }

    public void setBoard(Board b) {
        board_ = b;
    }


    public void setShowCandidates(boolean show) {
        renderer_.setShowCandidates(show);
        repaint();
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData starting values.
     */
    public void reset(int[][] initialData) {
        board_ = new Board(initialData);
        repaint();
    }

    public void startSolving(SudokuSolver solver) {
        boolean solved = solver.solvePuzzle(board_, this);
        showMessage(solved);
    }

    private void showMessage(boolean solved) {
        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + board_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" );
    }

    public void generateNewPuzzle(SudokuGenerator generator) {

        board_ = generator.generatePuzzleBoard();
        repaint();
    }

    public Board getBoard() {
        return board_;
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        renderer_.render(g, board_, "", this.getWidth(), this.getHeight());
    }
}

