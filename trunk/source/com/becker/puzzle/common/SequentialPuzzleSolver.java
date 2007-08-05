package com.becker.puzzle.common;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SequentialPuzzleSolver
 * <p/>
 * Sequential puzzle solver
 *
 * @author Brian Goetz and Tim Peierls
 */
public class SequentialPuzzleSolver <P, M> implements PuzzleSolver<P, M> {
    private final PuzzleController<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();
    private final Refreshable<P, M> ui;
    private long numTries = 0;

    public SequentialPuzzleSolver(PuzzleController<P, M> puzzle, Refreshable<P, M> ui) {
        this.puzzle = puzzle;
        this.ui = ui;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new PuzzleNode<P, M>(pos, null, null));
    }

    private List<M> search(PuzzleNode<P, M> node) {
        if (!puzzle.alreadySeen(node.pos, seen)) {       
            if (puzzle.isGoal(node.pos)) {  
                List<M> path = node.asMoveList();
                ui.finalRefresh(path, node.pos, numTries);
                return path;
            }
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                
                // don't refresh everytime as that would put too much load on the processor'
                if ( ui!=null)                  
                    ui.refresh(pos, numTries);             
                
                PuzzleNode<P, M> child = new PuzzleNode<P, M>(pos, move, node);
                numTries++;
                List<M> result = search(child);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
}
