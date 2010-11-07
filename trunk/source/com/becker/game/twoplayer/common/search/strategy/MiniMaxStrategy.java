package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
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
    public MiniMaxStrategy(Searchable controller, ParameterArray weights) {
        super(controller, weights);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                         SearchWindow window, SearchTreeNode parent) {
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

            TwoPlayerMove theMove = getNextMove(list);
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, window, i++);

            // recursive call
            selectedMove = searchInternal( theMove, depth-1, window.copy(), child );

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

                if (alphaBeta_ && pruneAtCurrnentNode(window, selectedValue, player1)) {
                    showPrunedNodesInTree(list, parent, i, selectedValue, window);
                    break;
                }
            }
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Note: The SearchWindow may be adjusted as a side effect.
     * @return  whether or not we should prune the current subtree.
     */
    private boolean pruneAtCurrnentNode(SearchWindow window, int selectedValue, boolean player1) {
        if ( player1 && (selectedValue < window.alpha) ) {
            if ( selectedValue < window.beta ) {
                return true;
            }
            else {
                window.alpha = selectedValue;
            }
        }
        if ( !player1 && (selectedValue > window.beta) ) {
            if ( selectedValue > window.alpha ) {
                return true;
            }
            else {
                window.beta = selectedValue;
            }
        }
        return false;
    }

    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return true;
    }
}