package com.becker.game.twoplayer.common.search;


import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;


/**
 *  This strategy class defines the NegaScout search algorithm.
 *  Negascout is very much like negamax except that it uses a 0 sized search window
 * and iterative deepening.
 *  See http://en.wikipedia.org/wiki/Negascout
 *
 * @@ curently identical to negamax
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
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i );

            // recursive call
            selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -newBeta, -alpha, child );

            int val = - (int) selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);


            if ( val > alpha && val < beta && i > 0 && depth > 0 ) {

                child = parent.addChild( theMove, alpha, beta, i );

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
                        showPrunedNodesInTree( list, parent, i, val, beta, PruneType.BETA);
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
}