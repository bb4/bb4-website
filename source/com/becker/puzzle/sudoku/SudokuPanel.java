package com.becker.puzzle.sudoku;

import javax.swing.*;
import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
public final class SudokuPanel extends JPanel
{

    private Board board_;

    private SudokuRenderer renderer_;

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
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData
     */
    public void reset(int[][] initialData) {
        board_ = new Board(initialData);
        repaint();
    }

    public void setBoard(Board b) {
        board_ = b;
    }

    public void setDelay(int delay) {
        delay_ = delay;
    }

    public void startSolving() {
        SudokuSolver solver = new SudokuSolver();
        solver.setDelay(delay_);
        boolean solved = solver.solvePuzzle(this);

        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + board_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
    }


    public void generateNewPuzzle() {
        SudokuGenerator generator = new SudokuGenerator(board_.getBaseSize());
        generator.setDelay(delay_);
        board_ = generator.generatePuzzleBoard(this);
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

