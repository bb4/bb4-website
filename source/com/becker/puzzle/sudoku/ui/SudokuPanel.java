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
    private SudokuSolver solver_;
    private int delay_;

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
        solver_ = new SudokuSolver(board_);
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData starting values.
     */
    public void reset(int[][] initialData) {
        board_ = new Board(initialData);
        repaint();
    }

    public void setBoard(Board b) {
        board_ = b;
        solver_.setBoard(b);
    }

    public void setDelay(int delay) {
        delay_ = delay;
        solver_.setDelay(delay_);
    }

    public void startSolving() {

        boolean solved = solver_.solvePuzzle(this);
        showMessage(solved);
    }

    private void showMessage(boolean solved) {
        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + board_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
    }

    public void generateNewPuzzle(int size) {
        SudokuGenerator generator = new SudokuGenerator(size, this);
        generator.setDelay(delay_);
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

