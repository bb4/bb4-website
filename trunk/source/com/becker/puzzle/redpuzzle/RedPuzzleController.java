package com.becker.puzzle.redpuzzle;

import com.becker.common.Util;
import com.becker.common.Worker;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.PuzzleSolver;
import com.becker.puzzle.common.Refreshable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The controller allows the solver to do its thing by providing the PuzzleController api.
 * Originally I had implmented solvers without trying to do concurrency, and those less generic
 * forms still exist, but do not require the PuzzleController api.
 * 
 * The generic solvers (sequential and concurrent) expect the PieceList to represent the state of a board,
 * and the Piece to represent a move. The way a move is applied is simply to add the piece to the 
 * end of the current list.
 *
 * Created on August 11, 2007
 * @author Barry Becker
 */
public class RedPuzzleController implements PuzzleController<PieceList, Piece> {

    public static final int NUM_PIECES = PieceList.NUM_PIECES;
    
    /** the viewer that can show the curent state. */
    private final Refreshable ui_;

    /** default solver. */
    private Algorithm algorithm_ = Algorithm.BRUTE_FORCE_ORIGINAL;
    
    private final PieceList SHUFFLED_PIECES = PieceList.getInitialPuzzlePieces();
    
    /**
     * Creates a new instance of RedPuzzleController
     */
    public RedPuzzleController(Refreshable ui) {        
        ui_ = ui;
    }
        
    /**
     * There are different approaches we can take to solving the red puzzle.
     *
     * @param alg
     */
    public void setAlgorithm(Algorithm algorithm) {
        algorithm_ = algorithm;
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
        for  (int i=0; i<NUM_PIECES; i++) {
            Piece p = SHUFFLED_PIECES.get(i);
            if (!position.contains(p)) {
                int r = 0; 
                // see if any of the rotations fit.
                while (!position.fits(p) && r < 4) {
                    p = p.rotate();
                    r++;
                }    
                if (r < 4) {             
                    assert(position.fits(p));
                    if (p.getNumber() == 9) {
                        System.out.println("adding move "+ p +" at position="+position);
                    }
                    moves.add(p);
                }
            }
        }       
        System.out.println("current fitted pieces ="+position.size());
        System.out.println("moves = "+moves);
        return moves;
    }

    public PieceList move(PieceList position, Piece move) {
        // To make a move, simple add the piece to the end of our list
        assert position.fits(move) : move  +" does not fit in  "+position;
        return position.add(move);
    }
    
    /**
     *Check all board symmetries to be sure it has or has not been seen.
     *If it was never seen before add it.
     *Must be synchronized because some solvers use concurrency.
     */
    public synchronized boolean alreadySeen(PieceList position, Set<PieceList> seen) {       
        
        boolean visited = true;      
         if (!seen.contains(position)) {
              visited = false; 
              seen.add(position);         
         }
        return visited;
    }    
    
    
    public void startSolving() {             

        // Use either concurrent or sequential solver strategy
        final PuzzleSolver<PieceList, Piece> solver = algorithm_.createSolver(this, ui_);

        Worker worker = new Worker()  {
     
            public Object construct()  {
                
                long t = System.currentTimeMillis(); 
                 
                // this does all the heavy work of solving it.   
                List<Piece> path = null;
                try {
                    path = solver.solve();            
                } catch (InterruptedException e) {
                    assert false: "Thread interrupted. " + e.getMessage();
                }

                float time = (float)((System.currentTimeMillis() - t))/1000.0f;
                if (path != null) {
                    System.out.println("solved in " + time + " seconds.");
                    if (solver instanceof RedPuzzleSolver)
                        System.out.println( "The final solution is shown. the number of iterations was:" +
                                ((RedPuzzleSolver)solver).getNumIterations() );
                } else {
                    System.out.println( "This puzzle was not solved!" ); 
                }
                System.out.flush();
                return null;
            }

            public void finished() {}
        };

        worker.start();  
    }    

}
