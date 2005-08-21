package com.becker.game.twoplayer.common.search;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 *  This strategy class defines the MiniMax search algorithm.
 *
 *  @author Barry Becker
 */
public final class MiniMaxStrategy extends SearchStrategy
{
    // number of moves to consider at the top ply.
    // we use this number to determine how far into the search that we are.
    private int numTopLevelMoves_;

    //Construct the strategy
    public MiniMaxStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * The MiniMax algorithm (with alpha-beta pruning)
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
        List list;   // list of moves to consider
        TwoPlayerMove selectedMove;  // the currently selected move

        // if player 1, then search for a high score, else seach for a low score
        boolean player1 = lastMove.player1;

        if ( depth == 0 || controller_.done( lastMove, false ) ) {
            if ( quiescence_ && depth == 0 )
                return quiescentSearch( lastMove, weights, quiescentDepth, alpha, beta, parent );
            else {
                lastMove.inheritedValue = lastMove.value;
                return lastMove;
            }
        }

        // generate a list of all candidate next moves, and pick the best one
        list = controller_.generateMoves( lastMove, weights, true );
        movesConsidered_ += list.size();
        if (depth == controller_.getLookAhead())
            numTopLevelMoves_ = list.size();

        GameContext.log( 3, "there were " + list.size() + " moves generated." );

        if ( emptyMoveList( list, lastMove ) ) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        int i = 0;
        double selectedValue, bestInheritedValue = Double.MIN_VALUE;
        if ( player1 ) bestInheritedValue = Double.MAX_VALUE;

        TwoPlayerMove bestMove = (TwoPlayerMove) (list.get( 0 ));
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
            selectedMove = search( theMove, weights, depth-1, quiescentDepth, alpha, beta, child );

            controller_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            selectedValue = selectedMove.inheritedValue;
            if ( player1 ) {
                if ( selectedValue < bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.inheritedValue;
                }
            }
            else if ( selectedValue > bestInheritedValue ) {
                bestMove = theMove;
                bestInheritedValue = bestMove.inheritedValue;
            }

            //********* alpha beta pruning ********
            if ( alphaBeta_ ) {
                if ( player1 && (selectedValue < alpha) ) {
                    if ( selectedValue < beta ) {
                        if ( parent != null )
                            showPrunedNodesInTree( list, parent, i, selectedValue, beta, PRUNE_BETA);
                        break; // pruned
                    }
                    else
                        alpha = selectedValue;
                }
                if ( !player1 && (selectedValue > beta) ) {
                    if ( selectedValue > alpha ) {
                        if ( parent != null )
                            showPrunedNodesInTree( list, parent, i, selectedValue, alpha, PRUNE_ALPHA);
                        break; // pruned
                    }
                    else
                        beta = selectedValue;
                }
            }
            //********* end alpha beta pruning *****
        }

        bestMove.selected = true;
        lastMove.inheritedValue = bestMove.inheritedValue;
        return bestMove;
    }

    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange
     */
    private TwoPlayerMove quiescentSearch( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, double alpha, double beta, SearchTreeNode parent )
    {
        lastMove.inheritedValue = lastMove.value;
        if ( depth >= MAX_QUIESCENT_DEPTH) {
            return lastMove;
        }
        if ( controller_.inJeopardy( lastMove, weights, true )) {
            // then search  a little deeper
            return search( lastMove, weights, 1, depth+1, alpha, beta, parent );
        }

        boolean player1 = lastMove.player1;
        if ( player1 ) {
            if ( lastMove.value >= beta )
                return lastMove; // prune
            if ( lastMove.value > alpha )
                alpha = lastMove.value;
        }
        else {
            if ( lastMove.value >= alpha )
                return lastMove; // prune
            if ( lastMove.value > beta )
                beta = lastMove.value;
        }

        // generate those moves that are critically urgent
        // if you generate too many, then you run the risk of an explosion in the search tree
        // these moves should be sorted from most to least urgent
        List list = controller_.generateUrgentMoves( lastMove, weights, true );

        if ( list == null || list.isEmpty() ) {
            return lastMove; // nothing to check
        }

        double bestInheritedValue = Double.MIN_VALUE;
        if ( player1 ) bestInheritedValue = Double.MAX_VALUE;
        TwoPlayerMove bestMove = (TwoPlayerMove) list.get(0);
        movesConsidered_ += list.size();
        Iterator it = list.iterator();
        int i = 0;

        while ( it.hasNext() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) it.next();
            controller_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree( parent, theMove, alpha, beta, i++ );

            TwoPlayerMove selectedMove = quiescentSearch( theMove, weights, depth+1, alpha, beta, child );
            assert selectedMove!=null;

            double selectedValue = selectedMove.inheritedValue;
            if ( player1 ) {
                if ( selectedValue < bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.inheritedValue;
                }
            }
            else if ( selectedValue > bestInheritedValue ) {
                bestMove = theMove;
                bestInheritedValue = bestMove.inheritedValue;
            }

            controller_.undoInternalMove( theMove );
            if ( player1 ) {
                if ( bestMove.inheritedValue >= beta )
                    return bestMove;  // prune
                if ( bestMove.inheritedValue > alpha )
                    alpha = bestMove.inheritedValue;
            }
            else {
                if ( bestMove.inheritedValue >= alpha )
                    return bestMove;  // prune
                if ( bestMove.inheritedValue > beta )
                    beta = bestMove.inheritedValue;
            }
        }
        return bestMove;
    }

}