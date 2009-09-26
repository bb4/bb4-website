package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;


/**
 *  This strategy class defines the NegaScout search algorithm.
 * (also known as principal variation search /PVS)
 *  Negascout is very much like negamax except that it uses a 0 sized search window
 *  and iterative deepening.
 *  See http://en.wikipedia.org/wiki/Negascout
 *
 *  psudo code:<pre>
 *  function negascout(node, depth, α, β)
 *     if node is a terminal node or depth = 0 {
 *         return the heuristic value of node
 *     }
 *     b = β                                      (* initial window is (-β, -α) *)
 *     foreach child of node {
 *        a = -negascout (child, depth-1, -b, -α)
 *        if a>α
 *             α = a
 *         if α≥β
 *             return α                          (* Beta cut-off *)
 *         if α≥b                                  (* check if null-window failed high*)
 *            α = -negascout(child, depth-1, -β, -α)  (* full re-search *)
 *            if α≥β
 *                return α                        (* Beta cut-off *)
 *         b = α+1                              (* set new null window *)
 *   }
 *    return α
 *  </pre>
 *  @author Barry Becker
 */
public class NegaScoutStrategy extends NegaMaxStrategy
{

    /** 
     * Construct NegaScout strategy given a controller interface.
     * @inheritDoc
     */
    public NegaScoutStrategy( Searchable controller, ParameterArray weights )
    {
        super( controller, weights );
    }

    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove,
                                       int depth,  List<? extends TwoPlayerMove> list,
                                       int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int bestVal = -SearchStrategy.INFINITY;
        int newBeta = beta;

        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove) list.get( 0 );

        while ( !list.isEmpty() ) {
            if (pauseInterrupted())
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            updatePercentDone(depth, list);
            
            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i );
   
            // search with minimal search window
            selectedMove = searchInternal( theMove, depth-1, -newBeta, -alpha, child );

            searchable_.undoInternalMove( theMove );
            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            int val = - selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);

            if (val > alpha) {
                alpha = val;
            }
            if (alpha >= beta) {
                theMove.setInheritedValue(alpha);
                bestMove = theMove;
                break;
            }
            if (alpha >= newBeta) {
                  // re-search with narrower window (typical alpha beta search).
                  searchable_.makeInternalMove( theMove );
                  selectedMove = searchInternal( theMove, depth-1, -beta, -val, child );
                  searchable_.undoInternalMove( theMove );

                  val = - selectedMove.getInheritedValue();
                  theMove.setInheritedValue(val);
                  if (val >= beta) {
                      theMove.setInheritedValue(val);
                      bestMove = theMove;
                      break;
                 }
            }
            i++;
            newBeta = alpha + 1;
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }
}