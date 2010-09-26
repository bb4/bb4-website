package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 * This strategy class defines the MiniMax search algorithm.
 * This is the simplest search strategy to which the other variants are compared.
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
                                       int depth, MoveList list,
                                       int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int selectedValue;
        TwoPlayerMove selectedMove;
        // if player 1, then search for a high score, else search for a low score.
        boolean player1 = lastMove.isPlayer1();
        int bestInheritedValue = player1? SearchStrategy.INFINITY: -SearchStrategy.INFINITY;

        TwoPlayerMove bestMove = (TwoPlayerMove)list.get(0);
        while ( !list.isEmpty() ) {
            if (pauseInterrupted())
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove)list.remove(0);
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
        int val = lastMove.getValue();
        lastMove.setInheritedValue(val);
        if ( depth >= maxQuiescentDepth_ || searchable_.done( lastMove, false )) {
            return lastMove;
        }
        if ( searchable_.inJeopardy( lastMove, weights_,  true )) {
            // then search  a little deeper
            return searchInternal( lastMove,  depth+1, alpha, beta, parent );
        }

        boolean player1 = lastMove.isPlayer1();
        if ( alphaBeta_ ) {
            if ( player1 ) {
                if ( val >= beta )
                    return lastMove; // prune
                if ( val > alpha )
                    alpha = val;
            }
            else {
                if ( val >= alpha )
                    return lastMove; // prune
                if ( val > beta )
                    beta = val;
            }

        }
        MoveList list = searchable_.generateUrgentMoves( lastMove, weights_, true );

        if ( list.isEmpty() ) {
            return lastMove; // nothing to check
        }

        int bestInheritedValue = player1 ?  SearchStrategy.INFINITY : -SearchStrategy.INFINITY;
        TwoPlayerMove bestMove = (TwoPlayerMove)list.get(0);
        movesConsidered_ += list.size();
        int i = 0;

        for (Move m : list) {
            TwoPlayerMove theMove = (TwoPlayerMove) m;
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
            if ( alphaBeta_ ) {
                if ( player1 ) {
                    if ( bestInheritedValue >= beta )
                        return bestMove;  // prune
                    if ( bestInheritedValue > alpha )
                        alpha = bestInheritedValue;
                }
                else {
                    if ( bestInheritedValue >= alpha )
                        return bestMove;  // prune
                    if ( bestMove.getInheritedValue() > beta )
                        beta = bestInheritedValue;
                }
            }
        }
        return bestMove;
    }

    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return true;
    }
}