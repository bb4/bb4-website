package com.becker.game.twoplayer.common.search;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 *  This strategy class defines the NegaMax search algorithm.
 *  Negamax is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *
 *  @author Barry Becker
 */
public final class NegaMaxStrategy extends SearchStrategy
{
    // number of moves to consider at the top ply.
    // we use this number to determine how far into the search that we are.
    private int numTopLevelMoves_;

    /** Construct NegaMax the strategy given a controller interface.
    */
    public NegaMaxStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * The NegaMax algorithm (with alpha-beta pruning)
     * This method is the crux of all 2 player zero sum games with perfect information
     *
     * @param lastMove the most recent move made by one of the players
     * @param weights coefficient for the evaluation polunomial that indirectly determines the best move
     * @param depth how deep in this local game tree that we are to search
     * @param alpha same as p2best but for the other player. (alpha)
     * @param beta the maximum of the value that it inherits from above and the best move found at this level (beta)
     * @param parent for constructing a ui tree. If null no game tree is constructed
     * @return the chosen move (ie the best move) (may be null if no next move)
     */
    public final TwoPlayerMove search( TwoPlayerMove lastMove, ParameterArray weights,
                                       int depth, int quiescentDepth,
                                       double alpha, double beta, SearchTreeNode parent )
    {
        return searchInternal( lastMove, weights, depth, quiescentDepth, -alpha, -beta, parent );
    }

    private TwoPlayerMove searchInternal( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          double alpha, double beta, SearchTreeNode parent )
    {

        if ( depth == 0 || controller_.done( lastMove, false ) ) {
            if ( quiescence_ && depth == 0 )
                return quiescentSearch( lastMove, weights, quiescentDepth, alpha, beta, parent );
            else {
                lastMove.inheritedValue = -lastMove.value;     //??
                return lastMove;
            }
        }

        // generate a list of all candidate next moves, and pick the best one
        List list = controller_.generateMoves( lastMove, weights, lastMove.player1 );
        movesConsidered_ += list.size();
        if (depth == controller_.getLookAhead())
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList( list, lastMove ) ) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        int i = 0;
        double bestVal = Double.MIN_VALUE;
        TwoPlayerMove selectedMove, bestMove = (TwoPlayerMove) (list.get( 0 ));
        while ( !list.isEmpty() ) {
            checkPause();
            if (interrupted_)
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            if (depth == controller_.getLookAhead())   {
                percentDone_ = 100 * (numTopLevelMoves_-list.size()) / numTopLevelMoves_;
            }

            controller_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree( parent, theMove, alpha, beta, i++ );

            // recursive call
            selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -beta, -alpha, child );

            controller_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            double val = -selectedMove.inheritedValue;
            theMove.inheritedValue = val;

            if ( val > bestVal ) {
                bestMove = theMove;
                bestVal = val;
            }
            if ( alphaBeta_ ) {
                if ( val >= beta ) {
                    if ( parent != null && !list.isEmpty() )
                        showPrunedNodesInTree( list, parent, i, val, beta, PRUNE_BETA);
                    return bestMove;
                }
                if ( val > alpha ) {
                    alpha = val;
                    bestMove = theMove;
                }
            }
        }

        bestMove.selected = true;
        lastMove.inheritedValue = -bestMove.inheritedValue;
        return bestMove;
    }

    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange
     */
    protected final TwoPlayerMove quiescentSearch( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, double alpha, double beta, SearchTreeNode parent )
    {
        double val = lastMove.value;
        lastMove.inheritedValue = val;
        if ( depth >= MAX_QUIESCENT_DEPTH) {
            return lastMove;
        }
        if (controller_.inJeopardy( lastMove, weights, lastMove.player1 ) ) {
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
        List list = controller_.generateUrgentMoves( lastMove, weights, lastMove.player1 );

        if ( list == null || list.isEmpty() )
            return lastMove; // nothing to check

        double bestVal = Double.MIN_VALUE;
        TwoPlayerMove bestMove = null;
        movesConsidered_ += list.size();
        GameContext.log( 2, "********* urgent moves = " + list );
        Iterator it = list.iterator();
        int i = 0;

        while ( it.hasNext() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) it.next();
            assert theMove!=null;

            controller_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree( parent, theMove, alpha, beta, i++ );

            TwoPlayerMove selectedMove = quiescentSearch( theMove, weights, depth+1, -beta, -alpha, child );
            assert selectedMove!=null;

            val = -selectedMove.inheritedValue;
            theMove.inheritedValue = val;

            controller_.undoInternalMove( theMove );
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
            bestMove.selected = true;
            lastMove.inheritedValue = -bestMove.inheritedValue;
        } else {
            bestMove = lastMove;   // avoid returning null
        }
        return bestMove;
    }

}