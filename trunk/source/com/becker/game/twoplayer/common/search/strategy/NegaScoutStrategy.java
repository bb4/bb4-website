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
 * and iterative deepening.
 *  See http://en.wikipedia.org/wiki/Negascout
 *
 *  @author Barry Becker
 */
public final class NegaScoutStrategy extends NegaMaxStrategy
{

    /** 
     * Construct NegaScout strategy given a controller interface.
     */
    public NegaScoutStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * @inheritDoc
     */
    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, ParameterArray weights,
                                       int depth, int quiescentDepth,
                                       int alpha, int beta, SearchTreeNode parent )
    {
        return searchInternal( lastMove, weights, depth, quiescentDepth, -alpha, -beta, parent );
    }

    private TwoPlayerMove searchInternal( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          int alpha, int beta, SearchTreeNode parent )
    {

        if ( depth == 0 || searchable_.done( lastMove, false ) ) {
            if ( quiescence_ && depth == 0 ) {
               return quiescentSearch( lastMove, weights, quiescentDepth, alpha, beta, parent );
            }
            lastMove.setInheritedValue( lastMove.getValue());
            return lastMove;
        }

        // generate a list of all candidate next moves, and pick the best one
        List list = searchable_.generateMoves( lastMove, weights, lastMove.isPlayer1() );

        movesConsidered_ += list.size();
        if (depth == searchable_.getLookAhead())
            numTopLevelMoves_ = list.size();

        int i = 0;
        int bestVal = -SearchStrategy.INFINITY;
        int newBeta = beta;

        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove) list.get( 0 );

        while ( !list.isEmpty() ) {
            checkPause();
            if (interrupted_)
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            updatePercentDone(depth, list);
            
            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i );

            // search with minimal search window
            selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -newBeta, -alpha, child );

            int val = - selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);

            if (val > bestVal) {  // then minimal search window failed
                if (newBeta == beta || depth <= 2) {       
                    bestVal = val;
                } else {
                    // re-search with narrower window (typical alpha beta search).
                    selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -beta, -val, child );

                    bestVal = - selectedMove.getInheritedValue();
                    theMove.setInheritedValue(bestVal);
                }
                bestMove = theMove;
            }
            i++;

            searchable_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }
    
            if ( alphaBeta_ ) {
                if (bestVal > alpha)
                    alpha = bestVal;
                if ( alpha >= beta ) {
                    showPrunedNodesInTree( list, parent, i, val, beta, PruneType.BETA);

                    bestMove.setInheritedValue(alpha);
                    break;  // prune
                }
                newBeta = alpha + 1;
            }
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }

}