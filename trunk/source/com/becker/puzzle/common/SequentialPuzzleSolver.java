package com.becker.puzzle.common;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SequentialPuzzleSolver
 * <p/>
 * Sequential puzzle solver.
 * Performs a depth first search on the state space.
 *
 * @author Brian Goetz, Tim Peierls, Barry Becker
 */
public class SequentialPuzzleSolver <P, M> implements PuzzleSolver<P, M> {
    private final PuzzleController<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();
    private final Refreshable<P, M> ui;
    private long numTries = 0;
    private long startTime;

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
                
                // don't necessarily refresh everytime as that would put too much load on the processor
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
