package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.common.math.MathUtil;
import com.becker.puzzle.sudoku.model.Board;
import com.becker.puzzle.sudoku.model.Cell;
import com.becker.puzzle.sudoku.model.ValuesList;

import java.util.Collections;
import java.util.List;

/**
 * Generate a Sudoku puzzle.
 * Initially created with grandma Becker on Date: Jul 8, 2006
 *
 * @author Barry Becker
 */
public class SudokuGenerator {

    private int size_;
    private int numCells_;
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
        ppanel_ = ppanel;
    }

    public void setDelay(int delay) {
        delay_ = delay;
    }

    public void setRandomSeed(int seed) {
        MathUtil.RANDOM.setSeed(seed);
    }

    /**
     * @return generated random board
     */
    public Board generatePuzzleBoard() {

        // first find a complete solution, b.
        Board board = new Board(size_);
        //System.out.println("initial board=" + board);

        if (ppanel_ != null)  {
            ppanel_.setBoard(board);
        }

        boolean success = generateSolution(board, 0);
        assert success : "We were not able to generate a consistent board "+ board;

        //System.out.println("Initial testBoard solved=" + board);

        // now start removing values until we cannot deduce the final solution from it.
        // for every position (in random order) if we can remove it, do so.
        return generateByRemoving(board);
    }

    /**
     * Recursive method to generate the sudoku board.
     * @param board the currently generated board (may be partial)
     * @return whether or not the current board is consistent.
     */
    protected boolean generateSolution(Board board, int position) {

        // base case of the recursion
        if (position == numCells_)  {
            // board completely solved now
            return true;
        }

        ValuesList shuffledValues = board.getShuffledCellCandidates(position);
        //assert shuffledValues.size() > 0 : "No shuffled values";

        //if (position % 7 == 0 && ppanel_ != null) {
            refresh();
        //}

        Cell cell = board.getCell(position);
        for (int value : shuffledValues) {
            cell.setValue(value);
            //System.out.println("board after setting shuffled value = " + value +" \n"  + board);
            if (generateSolution(board, position + 1)) {
                return true;
            }
            refresh();
            cell.clearValue();
            //System.out.println("BACKTRACKING clearing cell "+position+"=" + cell  + " shuffledValues=" + shuffledValues +" current="+ value);
            refresh();
        }

        return false;
    }

    private void refresh() {
        if (ppanel_!=null)  {
            ppanel_.repaint();
            //ThreadUtil.sleep(50);
        }
    }

    /**
     * Generate a sudoku puzzle that someone can solve.
     * @param board the initially solved puzzle
     * @return same puzzle after removing values in as many cells as possible and still retain consistency.
     */
    private Board generateByRemoving(Board board) {

        if (ppanel_ != null) {
            ppanel_.setBoard(board);
        }

        List positionList = getRandomPositions(size_);
        // we need a solver to verify that we can still deduce the original
        SudokuSolver solver = new SudokuSolver(board);
        solver.setDelay(delay_/20);

        int len = size_ * size_;
        int last = len * len;
        // the first len can be removed without worrying about having an unsolvable puzzle.
        for (int i=0; i < len; i++) {
            int pos = (Integer) positionList.get(i) - 1;
            board.getCell(pos).clearValue();
            //System.out.println(""+positionList.subList(0, i+1) +" cleared");
        }
        //System.out.println("after clearing first " + len + "("+positionList.subList(0, len)+") board="+ board);

        for (int i=len; i < last; i++) {
            int pos = (Integer) positionList.get(i) - 1;
            tryRemovingValue(pos, board, solver);
            //System.out.println("solution with "+positionList.subList(0, i+1) +" cleared" + board);
            //solution.reset();
        }
      
        return board;
    }

    /**
     * @param pos  position to try removing.
     */
    private void tryRemovingValue(int pos, Board board, SudokuSolver solver) {
        Cell cell = board.getCell(pos);
        int value = cell.getValue();
        cell.clearValue();

        if (ppanel_ != null && delay_ > 0) {
            ppanel_.repaint();
        }

        Board copy = new Board(board);  // try to avoid this
        solver.setBoard(copy);
        if (!solver.solvePuzzle(ppanel_)) {
            // put it back since it cannot be solved without this positions value
            cell.setOriginalValue(value);
        }
    }

    /**
     * @param size the base size (fourth root of the number of cells).
     * @return the positions on the board in a random order in a list .
     */
    private ValuesList getRandomPositions(int size) {
        int numPositions = size * size * size * size;
        ValuesList positionList = new ValuesList(numPositions);
        Collections.shuffle(positionList, MathUtil.RANDOM);
        return positionList;
    }

}
