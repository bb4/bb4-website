package com.becker.game.twoplayer.common.search;


import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;


/**
 *  This strategy class defines the NegaScout search algorithm.
 *  Negascout is very much like minimax or negamax.
 *  See http://en.wikipedia.org/wiki/Negascout
 *
 * @@ curently identical to negamax
 *
 *  @author Barry Becker
 */
public final class NegaScoutStrategy extends SearchStrategy
{
    /** Number of moves to consider at the top ply.
     *  we use this number to determine how far into the search that we are.
     */
    private int numTopLevelMoves_;

    /** Construct NegaMax the strategy given a controller interface.
    */
    public NegaScoutStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * The NegaScout algorithm (with alpha-beta pruning)
     * This method is the crux of the algorithm for 2 player zero sum games with perfect information.
     *
     * @param lastMove the most recent move made by one of the players
     * @param weights coefficient for the evaluation polunomial that indirectly determines the best move
     * @param depth how deep in this local game tree that we are to search
     * @param alpha same as p2best but for the other player. (alpha)
     * @param beta the maximum of the value that it inherits from above and the best move found at this level (beta)
     * @param parent for constructing a ui tree. If null no game tree is constructed
     * @return the chosen move (ie the best move) (may be null if no next move)
     */
    public TwoPlayerMove search( TwoPlayerMove lastMove, ParameterArray weights,
                                       int depth, int quiescentDepth,
                                       int alpha, int beta, SearchTreeNode parent )
    {
        return searchInternal( lastMove, weights, depth, quiescentDepth, -alpha, -beta, parent );
    }

    private TwoPlayerMove searchInternal( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          int oldAlpha, int beta, SearchTreeNode parent )
    {

        if ( depth == 0 || searchable_.done( lastMove, false ) ) {
            if ( quiescence_ && depth == 0 ) {
               return quiescentSearch( lastMove, weights, quiescentDepth, oldAlpha, beta, parent );
            }
            lastMove.setInheritedValue( -lastMove.getValue());
            return lastMove;
        }

        int alpha = oldAlpha;
        int newBeta = beta;

        // generate a list of all candidate next moves, and pick the best one
        List list = searchable_.generateMoves( lastMove, weights, lastMove.isPlayer1() );

        movesConsidered_ += list.size();
        if (depth == searchable_.getLookAhead())
            numTopLevelMoves_ = list.size();

        int i = 0;
        //int bestVal = Integer.MIN_VALUE;
        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove) list.get( 0 );

        while ( !list.isEmpty() ) {
            checkPause();
            if (interrupted_)
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            if (depth == searchable_.getLookAhead())   {
                percentDone_ = 100 * (numTopLevelMoves_-list.size()) / numTopLevelMoves_;
            }

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree( parent, theMove, alpha, beta, i );

            // recursive call
            selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -newBeta, -alpha, child );

            int val = - (int) selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);


            if ( val > alpha && val < beta && i > 0 && depth > 0 ) {

                child = addNodeToTree( parent, theMove, alpha, beta, i );

                // re-search with narrower window
                selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -beta, -val, child );

                val = - (int) selectedMove.getInheritedValue();
            }
            i++;

            searchable_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            alpha = Math.max(alpha, val);
            if (alpha == val)
                bestMove = theMove;
            bestMove.setInheritedValue(alpha);

            if ( alphaBeta_ ) {
                if ( alpha >= beta ) {
                    if ( parent != null && !list.isEmpty() )
                        showPrunedNodesInTree( list, parent, i, val, beta, PRUNE_BETA);
                    bestMove.setSelected(true);
                    return bestMove;
                }
                newBeta = alpha + 1;
            }

        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange
     */
    private TwoPlayerMove quiescentSearch( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int oldAlpha, int beta, SearchTreeNode parent )
    {
        int alpha = oldAlpha;
        int val = (int) lastMove.getValue();
        lastMove.setInheritedValue(val);
        if ( depth >= MAX_QUIESCENT_DEPTH) {
            return lastMove;
        }
        if (searchable_.inJeopardy( lastMove, weights, lastMove.isPlayer1() ) ) {
            // then search  a little deeper
            return search( lastMove, weights, 1, depth+1, alpha, beta, parent );
        }

        if ( alphaBeta_ ) {
            if ( val >= beta )
                return lastMove; // prune
            if ( val > alpha )
                alpha = val;
        }

        // generate those moves that are critically urgent
        // if you generate too many, then you run the risk of an explosion in the search tree
        // these moves should be sorted from most to least urgent
        List list = searchable_.generateUrgentMoves( lastMove, weights, lastMove.isPlayer1() );

        if ( list == null || list.isEmpty() )
            return lastMove; // nothing to check

        double bestVal = Double.MIN_VALUE;
        TwoPlayerMove bestMove = null;
        movesConsidered_ += list.size();
        //GameContext.log( 2, "********* urgent moves = " + list );
        Iterator it = list.iterator();
        int i = 0;

        while ( it.hasNext() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) it.next();
            assert theMove!=null;

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree( parent, theMove, alpha, beta, i++ );

            TwoPlayerMove selectedMove = quiescentSearch( theMove, weights, depth+1, -beta, -alpha, child );
            assert selectedMove!=null;

            val = -(int)selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);

            searchable_.undoInternalMove( theMove );
            if ( val > bestVal ) {
                bestMove = theMove;
                bestVal = val;
            }
            if ( alphaBeta_ ) {
                if ( val >= beta ) {
                    return bestMove;
                }
                if ( val > alpha ) {
                    alpha = val;
                    bestMove = theMove;
                }
            }
        }
        if (bestMove != null) {
            bestMove.setSelected(true);
            lastMove.setInheritedValue(-bestMove.getInheritedValue());
        } else {
            bestMove = lastMove;   // avoid returning null
        }
        return bestMove;
    }

}