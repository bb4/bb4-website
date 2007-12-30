package com.becker.puzzle.redpuzzle;

import com.becker.common.util.Util;
import com.becker.common.Worker;
import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.PuzzleSolver;
import com.becker.puzzle.common.Refreshable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The controller allows the solver to do its thing by providing the PuzzleController api.
 * Originally I had implemented solvers without trying to do concurrency, and those less generic
 * forms still exist, but do not require the PuzzleController api.
 * 
 * The generic solvers (sequential and concurrent) expect the PieceList to represent the state of a board,
 * and the Piece to represent a move. The way a move is applied is simply to add the piece to the 
 * end of the current list.
 *
 * Created on August 11, 2007
 * @author Barry Becker
 */
public class RedPuzzleController extends AbstractPuzzleController<PieceList, Piece> {

    public static final int NUM_PIECES = PieceList.NUM_PIECES;
    private final PieceList SHUFFLED_PIECES = PieceList.getInitialPuzzlePieces();
    
    /**
     * Creates a new instance of RedPuzzleController
     */
    public RedPuzzleController(Refreshable ui) {        
        super(ui);
        algorithm_ = Algorithm.BRUTE_FORCE_ORIGINAL;
    }
 
    public PieceList initialPosition() {
        return new PieceList();  // empty piece list 
    }

    public boolean isGoal(PieceList position) {
        // we have reached our goal if we have 9 pieces that fit
        return (position.size() == NUM_PIECES);
    }

    public List<Piece> legalMoves(PieceList position) {   
        // for each piece that we have not tried yet, see if it fits.
        // if it does, add that to the set of legal next moves.        
        List<Piece> moves = new LinkedList<Piece>();
        for  (int i = 0; i < NUM_PIECES; i++) {
            Piece p = SHUFFLED_PIECES.get(i);
            if (!position.contains(p)) {
                int r = 0; 
                // see if any of the rotations fit.
                while (!position.fits(p) && r < 4) {
                    p = p.rotate();
                    r++;
                }    
                if (r < 4) {             
                    moves.add(p);
                }
            }
        }       
        return moves;
    }

    public PieceList move(PieceList position, Piece move) {
        // To make a move, simple add the piece to the end of our list
        assert position.fits(move) : move  +" does not fit in  "+position;
        return position.add(move);
    }
   
}
