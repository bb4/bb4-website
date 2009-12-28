package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 * This strategy class defines the MiniMax search algorithm.
 * This is the simplest search strategy to which the other variants are compared.
 *
 *  @author Barry Becker
 */
public final class MiniMaxStrategy extends AbstractSearchStrategy
{
    /**
     * Constructor for the strategy.
     * @inheritDoc
     */
    public MiniMaxStrategy( Searchable controller, ParameterArray weights )
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
        int selectedValue;
        TwoPlayerMove selectedMove;  // the currently selected move
        // if player 1, then search for a high score, else search for a low score.
        boolean player1 = lastMove.isPlayer1();
        int bestInheritedValue = player1? SearchStrategy.INFINITY: -SearchStrategy.INFINITY;

        TwoPlayerMove bestMove = list.get( 0 );
        while ( !list.isEmpty() ) {
            if (pauseInterrupted())
                return lastMove;

            TwoPlayerMove theMove = list.remove(0);
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++);

            // recursive call
            selectedMove = searchInternal( theMove, depth-1, alpha, beta, child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove != null) {
                selectedValue = selectedMove.getInheritedValue();
                if ( player1 ) {
                    if ( selectedValue < bestInheritedValue ) {
                        bestMove = theMove;
                        bestInheritedValue = bestMove.getInheritedValue();
                    }
                }
                else if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.getInheritedValue();
                }

                if ( alphaBeta_ ) {
                    if ( player1 && (selectedValue < alpha) ) {
                        if ( selectedValue < beta ) {
                            showPrunedNodesInTree( list, parent, i, selectedValue, beta, PruneType.BETA);
                            break; // pruned
                        }
                        else
                            alpha = selectedValue;
                    }
                    if ( !player1 && (selectedValue > beta) ) {
                        if ( selectedValue > alpha ) {
                            showPrunedNodesInTree( list, parent, i, selectedValue, alpha, PruneType.ALPHA);
                            break; // pruned
                        }
                        else
                            beta = selectedValue;
                    }
                }
            }
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }


    /**
     * This continues the search in situations where the board position is not stable.
     * For example, perhaps we are in the middle of a piece exchange
     */
    @Override
    protected TwoPlayerMove quiescentSearch( TwoPlayerMove lastMove,
                                             int depth, int oldAlpha, int oldBeta, SearchTreeNode parent )
    {
        int alpha = oldAlpha;
        int beta = oldBeta;
        lastMove.setInheritedValue(lastMove.getValue());
        if ( depth >= maxQuiescentDepth_ || searchable_.done( lastMove, false )) {
            return lastMove;
        }
        if ( searchable_.inJeopardy( lastMove, weights_,  true )) {
            // then search  a little deeper
            return searchInternal( lastMove,  depth+1, alpha, beta, parent );
        }

        boolean player1 = lastMove.isPlayer1();
        if ( player1 ) {
            if ( lastMove.getValue() >= beta )
                return lastMove; // prune
            if ( lastMove.getValue() > alpha )
                alpha = lastMove.getValue();
        }
        else {
            if ( lastMove.getValue() >= alpha )
                return lastMove; // prune
            if ( lastMove.getValue() > beta )
                beta = lastMove.getValue();
        }

        // generate those moves that are critically urgent
        // if you generate too many, then you run the risk of an explosion in the search tree
        // these moves should be sorted from most to least urgent.
        List<? extends TwoPlayerMove> list =
                searchable_.generateUrgentMoves( lastMove, weights_, fromPlayer1sPerspective(lastMove) );

        if ( list.isEmpty() ) {
            return lastMove; // nothing to check
        }

        double bestInheritedValue = -SearchStrategy.INFINITY;
        if ( player1 ) bestInheritedValue = SearchStrategy.INFINITY;
        TwoPlayerMove bestMove = list.get(0);
        movesConsidered_ += list.size();
        int i = 0;

        for (TwoPlayerMove theMove : list) {
            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent,  theMove, alpha, beta, i++ );

            TwoPlayerMove selectedMove = quiescentSearch( theMove, depth+1, alpha, beta, child );
            assert selectedMove!=null;

            int selectedValue = selectedMove.getInheritedValue();
            if ( player1 ) {
                if ( selectedValue < bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.getInheritedValue();
                }
            }
            else if ( selectedValue > bestInheritedValue ) {
                bestMove = theMove;
                bestInheritedValue = bestMove.getInheritedValue();
            }

            searchable_.undoInternalMove( theMove );
            if ( player1 ) {
                if ( bestMove.getInheritedValue() >= beta )
                    return bestMove;  // prune
                if ( bestMove.getInheritedValue() > alpha )
                    alpha = bestMove.getInheritedValue();
            }
            else {
                if ( bestMove.getInheritedValue() >= alpha )
                    return bestMove;  // prune
                if ( bestMove.getInheritedValue() > beta )
                    beta = bestMove.getInheritedValue();
            }
        }
        return bestMove;
    }


    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return true;
    }
}