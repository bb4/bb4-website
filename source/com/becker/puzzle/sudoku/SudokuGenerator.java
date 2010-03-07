package com.becker.puzzle.sudoku;

import com.becker.common.util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Generate a Sudoku puzzle.
 *
 * @author Barry Becker Date: Jul 8, 2006
 */
public class SudokuGenerator {

    private int size_;
    private int numCells_;
    private Random random_;
    private int delay_;
    SudokuPanel ppanel_;

    /**
     * Constructor
     * @param baseSize 4, 9, or 16
     * @param ppanel renders the puzzle. May be null if you do not want to see animation.
     */
    public SudokuGenerator(int baseSize, SudokuPanel ppanel) {
        size_ = baseSize;
        numCells_ = (int) Math.pow(size_, 4);
        random_ = new Random();
        ppanel_ = ppanel;
    }

    public void setDelay(int delay) {
        delay_ = delay;
    }

    /**
     * @return generated random board
     */
    public Board generatePuzzleBoard() {

        // first find a complete solution, b.
        Board b = new Board(size_);
        System.out.println("gen initial solution");

        if (ppanel_ != null)
            ppanel_.setBoard(b);

        boolean success = generateSolution(b, 0);
        System.out.println("done gen initial solution");
        assert success;

        Board test = new Board(b);

        // now start removing values until we cannot deduce the final solution from it.
        // for every position (in random order) if we can remove it, do so.
        return generateByRemoving(test);
    }

    /**
     * Recursive method to generate the sudoku board.
     * @return the currently generated board (may be partial)
     */
    private boolean generateSolution(Board board, int position) {

        // base case of the recursion
        if (position == numCells_)  {
            return true;  // board complete now
        }

        List<Integer> candidates = board.getCellCandidates(position);

        if (position % 7 == 0) {
            ppanel_.repaint();
        }

        Cell c = board.getCell(position);
        for (int candidate : candidates) {
            c.setValue(candidate);
            if (generateSolution(board, position + 1)) {
                return true;
            }
        }
        c.clearValue();
        return false;
    }

    private Board generateByRemoving(Board solution) {

        if (ppanel_ != null)
            ppanel_.setBoard(solution);

        List positionList = getRandomPositions(size_);
        // we need a solver to verify that we can still deduce the original
        SudokuSolver solver = new SudokuSolver();
        solver.setDelay(delay_/20);

        int len = size_ * size_;
        int last = len * len;
        // the first len can be removed without worrying about having an unsolvable puzzle.
        for (int i=0; i < len; i++) {
            int pos = (Integer) positionList.get(i);
            solution.getCell(pos).clearValue();
        }

        for (int i=len; i < last; i++) {
            int pos = (Integer) positionList.get(i);
            tryRemovingValue(pos, solution, solver);
            solution.reset();
        }
      
        return solution;
    }

    /**
     * @param pos  position to try removing.
     */
    private void tryRemovingValue(int pos, Board solution, SudokuSolver solver) {
        Cell c = solution.getCell(pos);
        int value = c.getValue();
        c.clearValue();

        if (ppanel_ != null && delay_ > 0) {
            ppanel_.repaint();
        }

        if (!solver.solvePuzzle(solution, null)) {
            // put it back since it cannot be solved without this positions value
            c.setOriginalValue(value);
        }
    }

    /**
     * @param size the base size (fourth root of the number of cells).
     * @return the poistions on the board in a random order in a list
     */
    private List getRandomPositions(int size) {
        int numPositions = size * size * size * size;
        List<Integer> positionList = new ArrayList<Integer>(numPositions);
        for (int i=0; i < numPositions; i++) {
            positionList.add(i);
        }
        Collections.shuffle(positionList, random_);
        return positionList;
    }

}
