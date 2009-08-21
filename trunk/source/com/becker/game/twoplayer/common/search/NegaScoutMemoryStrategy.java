package com.becker.game.twoplayer.common.search;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;
import java.util.List;

/**
 *  This strategy class defines the NegaScout with memory search algorithm.
 *  Negascout is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *  This version stores the values of moves that have already been searched.
 *  See http://home.tiscali.nl/askeplaat/mtdf.html
 *  and http://en.wikipedia.org/wiki/Negascout
 *
 *  @author Barry Becker  2/07
 */
public final class NegaScoutMemoryStrategy extends NegaMaxStrategy
{

    /**
     * Construct NegaMax the strategy given a controller interface.
     */
    public NegaScoutMemoryStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * @inheritDoc
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
        int alpha = oldAlpha;
        if ( depth == 0 || searchable_.done( lastMove, false ) ) {
            if ( quiescence_ && depth == 0 )
                return quiescentSearch( lastMove, weights, quiescentDepth, alpha, beta, parent );
            else {
                lastMove.setInheritedValue(-lastMove.getValue());
                return lastMove;
            }
        }

        // generate a list of all candidate next moves, and pick the best one
        List list = searchable_.generateMoves( lastMove, weights, lastMove.isPlayer1() );
        movesConsidered_ += list.size();
        if (depth == searchable_.getLookAhead())
            numTopLevelMoves_ = list.size();

        if ( emptyMoveList( list, lastMove ) ) {
            // if there are no possible next moves, return null (we hit the end of the game).
            return null;
        }

        int i = 0;
        int bestVal = Integer.MIN_VALUE;
        TwoPlayerMove selectedMove = null;
        TwoPlayerMove bestMove = (TwoPlayerMove) (list.get( 0 ));

        while ( !list.isEmpty() ) {
            checkPause();
            if (interrupted_)
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            if (depth == searchable_.getLookAhead())   {
                percentDone_ = 100 * (numTopLevelMoves_-list.size()) / numTopLevelMoves_;
            }

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++ );

            // recursive call
            selectedMove = searchInternal( theMove, weights, depth-1, quiescentDepth, -beta, -alpha, child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            int val = - (int) selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);

            if ( val > bestVal ) {
                bestMove = theMove;
                bestVal = val;
            }
            if ( alphaBeta_ ) {
                if ( val >= beta ) {
                    showPrunedNodesInTree( list, parent, i, val, beta, PruneType.BETA);
                    return bestMove;
                }
                if ( val > alpha ) {
                    alpha = val;
                    bestMove = theMove;
                }
            }
        }

        /* Transposition table storing of bounds. Fail low result implies an upper bound */
        int g = (int) selectedMove.getInheritedValue();
        if (selectedMove.getInheritedValue() <= alpha) {
            //n.upperbound := g;
            //store n.upperbound;
        }
        /* Found an accurate minimax value - will not occur if called with zero window */
        if (selectedMove.getInheritedValue() >  alpha &&  selectedMove.getInheritedValue() < beta) {

            //n.lowerbound := g;
            //n.upperbound := g;
            //store n.lowerbound, n.upperbound;
        }
        /* Fail high result implies a lower bound */
        if (selectedMove.getInheritedValue() >= beta) {
            //n.lowerbound := g;
            //store n.lowerbound;
        }


        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }
}