package com.becker.puzzle.sudoku;


import java.io.*;

/**
 * This does the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class PuzzleSolver {


    // count the number of times we have tried to place a piece.
    private int numIterations_ = 0;


    /**
     * Constructor
     */
    public PuzzleSolver() {
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param board
     * @return
     */
    public  boolean solvePuzzle( Board board) {
        return solvePuzzle(board, null);
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param puzzlePanel
     * @return
     */
    protected  boolean solvePuzzle(PuzzlePanel puzzlePanel) {
        return solvePuzzle(puzzlePanel.getBoard(), puzzlePanel);
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param board
     * @return
     */
    private boolean solvePuzzle( Board board, PuzzlePanel puzzlePanel) {
        boolean solved = false;

        do {
            // find missing row and column numbers
            board.updateCellCandidates();
            refresh(puzzlePanel);
            pause(500);
            board.checkAndSetUniqueValues();

            refresh(puzzlePanel);
            pause(1000);
            numIterations_++;
            solved = board.solved();

        } while (!solved && numIterations_ < 100);


        refresh(puzzlePanel);

        // if we get here and solved is not true, we did not find a puzzlePanel
        return solved;
    }

    private static void pause() {
        System.out.println("in pause");

        //Scanner scanner = new Scanner(System.in);
        //String nextString = scanner.next();

        try {
            int c = System.in.read(); // pause till keypressed
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done pause");
    }

    private static void pause(int millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
      }


    /**
     * @return  the number of pieces we have tried to fit so far.
     */
    public int getNumIterations() {
        return numIterations_;
    }


    private static void refresh(PuzzlePanel puzzlePanel) {
        if (puzzlePanel == null)
            return;
        puzzlePanel.repaint();
        try {
           Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
