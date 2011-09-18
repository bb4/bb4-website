package com.becker.puzzle.sudoku;

import com.becker.puzzle.sudoku.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
public final class SudokuPanel extends JPanel
                               implements MouseListener  {
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
        //this.addMouseListener(this);
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
    }

    public void startSolving() {

        solver_.setDelay(delay_);
        boolean solved = solver_.solvePuzzle(this);

        showMessage(solved);
    }

    /*
    public void doIteration() {

        boolean solved = solver_.doIteration();
        this.repaint();

        if (solved || board_.getNumIterations() > 20) {
            showMessage(solved);
        }
    }*/

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


    public void mouseClicked(MouseEvent e) {
       //doIteration();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

