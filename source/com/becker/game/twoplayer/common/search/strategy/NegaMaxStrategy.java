package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.math.Range;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 *  This strategy class defines the NegaMax search algorithm.
 *  Negamax is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *  @author Barry Becker
 */
public class NegaMaxStrategy extends AbstractSearchStrategy
{
    /**
     * Construct NegaMax the strategy given a controller interface.
     * @inheritDoc
     */
    public NegaMaxStrategy( Searchable controller, ParameterArray weights )
    {
        super( controller , weights);
    }

    /**
     * @inheritDoc
     */
    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {
        // need to negate alpha and beta on initial call.
        Range window = getOptions().getInitialSearchWindow();
        return searchInternal( lastMove, lookAhead_, (int)window.getMax(), (int)window.getMin(), parent );
    }


    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove,
                                       int depth, MoveList list,
                                       int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove)list.get( 0 );

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = (TwoPlayerMove)list.remove(0);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++);

            selectedMove = searchInternal( theMove, depth-1, -beta, -alpha, child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = selectedValue;
                }
                if ( alphaBeta_ ) {
                    if ( bestInheritedValue > alpha ) {
                        alpha = bestInheritedValue;
                        bestMove = theMove;
                    }
                    if ( alpha >= beta ) {
                        showPrunedNodesInTree( list, parent, i, selectedValue, beta, PruneType.BETA);
                        break;
                    }
                }
            }
        }
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange.
     */
    @Override
    protected TwoPlayerMove quiescentSearch( TwoPlayerMove lastMove,
                                          int depth, int oldAlpha, int beta, SearchTreeNode parent )
    {
        int alpha = oldAlpha;
        int val = lastMove.getValue();
        lastMove.setInheritedValue(val);
        if ( depth >= maxQuiescentDepth_) {
            return lastMove;
        }
        if (searchable_.inJeopardy( lastMove, weights_, true)) {
            // then search a little deeper
            return searchInternal( lastMove, depth+1, -alpha, -beta, parent );
        }

        if ( alphaBeta_ ) {
            if ( val >= beta )
                return lastMove; // prune
            if ( val > alpha )
                alpha = val;
        }

        MoveList list = searchable_.generateUrgentMoves( lastMove, weights_, true);

        if (list.isEmpty())
            return lastMove; // nothing to check

        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove bestMove = null;
        movesConsidered_ += list.size();
        GameContext.log( 2, "********* urgent moves = " + list );
        int i = 0;

        for (Move m : list) {
            TwoPlayerMove theMove = (TwoPlayerMove) m;
            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++ );

            TwoPlayerMove selectedMove = quiescentSearch( theMove, depth+1, beta, alpha, child );   // neg a/b?
            assert selectedMove!=null;

            int selectedValue = -selectedMove.getInheritedValue();
            theMove.setInheritedValue(val);

            searchable_.undoInternalMove( theMove );
            if ( selectedValue > bestInheritedValue ) {
                bestMove = theMove;
                bestInheritedValue = selectedValue;
            }
            if ( alphaBeta_ ) {
                if ( selectedValue >= beta ) {
                    return bestMove;
                }
                if ( selectedValue > alpha ) {
                    alpha = selectedValue;
                }
            }
        }
        assert (bestMove != null);
        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }

    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return !lastMove.isPlayer1();
    }
}