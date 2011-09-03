package com.becker.puzzle.common.solver;

import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.Refreshable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sequential puzzle solver.
 * Performs a depth first search on the state space.
 *
 * @author Brian Goetz, Tim Peierls
 * @author Barry Becker
 */
public class SequentialPuzzleSolver<P, M> implements PuzzleSolver<P, M> {
    private final PuzzleController<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();
    private final Refreshable<P, M> ui;
    private long numTries = 0;
    private long startTime;

    /**
     *
     * @param puzzle the puzzle to solve
     * @param ui the thing that can show its current state.
     */
    public SequentialPuzzleSolver(PuzzleController<P, M> puzzle, Refreshable<P, M> ui) {
        this.puzzle = puzzle;
        this.ui = ui;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        startTime =  System.currentTimeMillis(); 
        return search(new PuzzleNode<P, M>(pos, null, null));
    }

    private List<M> search(PuzzleNode<P, M> node) {
        if (!puzzle.alreadySeen(node.position, seen)) {       
            if (puzzle.isGoal(node.position)) {  
                List<M> path = node.asMoveList();
                long elapsedTime = System.currentTimeMillis() - startTime;             
                ui.finalRefresh(path, node.position, numTries, elapsedTime);
                return path;
            }
            List<M> moves = puzzle.legalMoves(node.position);
            for (M move : moves) {
                P position = puzzle.move(node.position, move);
                
                // don't necessarily refresh every time as that would put too much load on the processor
                if (ui != null)                  
                    ui.refresh(position, numTries);             
                
                PuzzleNode<P, M> child = new PuzzleNode<P, M>(position, move, node);
                numTries++;
                List<M> result = search(child);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
}
