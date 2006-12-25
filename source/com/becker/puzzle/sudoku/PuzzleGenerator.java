package com.becker.puzzle.sudoku;

import java.util.*;


/**
 * @author Barry Becker Date: Jul 8, 2006
 */
public class PuzzleGenerator {

    private int size_;
    private int numCells_;
    private Random random_;

    /**
     * Constructor
     */
    public PuzzleGenerator(int baseSize) {
        size_ = baseSize;
        numCells_ = (int) Math.pow(size_, 4);
        random_ = new Random();
    }

    /**
     * Set this seed value if you want to get repeatable results.
     * Same behavior for same seed.
     * @param seed
     */
    public void setRandomSeed(int seed)
    {
        random_ = new Random(seed);
    }

    /**
     * @return generated a random board
     */
    public Board generatePuzzleBoard(PuzzlePanel ppanel) {

        // first find a complete solution, b.
        Board b = new Board(size_);
        boolean success = generateSolution(b, 0);
        assert success;

        Board test = new Board(b);

        if (ppanel != null) {
            ppanel.setBoard(test);
            ppanel.repaint();
        }

        // now start removing values until we cannot deduce the final solution from it.
        // for every position (in random order) if we can remove it, do so.
        Board generatedBoard = generateByRemoving(test, ppanel);

        return generatedBoard;
    }


    /**
     * Recursive method to generate the sudoku board.
     * @param board
     * @param position
     * @return the currently generated board
     */
    private boolean generateSolution(Board board, int position) {

        // base case of the recursion
        if (position == numCells_)  {
            return true;  // board complete now
        }

        List candidates = board.findCellCandidates(position);
        Collections.shuffle(candidates, random_);

        Iterator it = candidates.iterator();
        Cell c = board.getCell(position);
        while (it.hasNext()) {
            c.setValue((Integer) it.next());
            if (generateSolution(board, position + 1)) {
                return true;
            }
        }
        c.clearValue();
        return false;
    }

    private Board generateByRemoving(Board solution, PuzzlePanel ppanel) {

        List positionList = getRandomPositions(size_);
        // we need a solver to verify that we can still deduce the original
        PuzzleSolver solver = new PuzzleSolver();

        int len = size_ * size_;
        int last = len * len;
        // the first len can be removed without worrying about having an unsolvable puzzle.
        for (int i=0; i < len; i++) {
            int pos = (Integer) positionList.get(i);
            solution.getCell(pos).clearValue();
        }

        //assert(solver.solvePuzzle(solution, ppanel));
        //solution.reset();

        int givens = 0;
        for (int i=len; i < last; i++) {
            int pos = (Integer) positionList.get(i);
            Cell c = solution.getCell(pos);
            int value = c.getValue();
            c.clearValue();
            if (!solver.solvePuzzle(solution, null)) {
                // put it back since it cannot be solved without this positions value
                c.setOriginalValue(value);
                givens++;
            }
            solution.reset();
            if (ppanel != null)
                ppanel.repaint();
        }
        //System.out.println("Num givens =" + givens);

        return solution;
    }

    /**
     * @param size the base size (fourth root of the number of cells.
     * @return the poistions on the board in a random order in a list
     */
    private List getRandomPositions(int size) {
        int numPositions = size * size * size * size;
        List positionList = new ArrayList(numPositions);
        for (int i=0; i < numPositions; i++) {
            positionList.add(i);
        }
        Collections.shuffle(positionList, random_);
        return positionList;
    }

}