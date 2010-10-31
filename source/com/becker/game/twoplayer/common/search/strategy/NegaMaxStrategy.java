package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.math.Range;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;

/**
 *  This strategy class defines the NegaMax search algorithm.
 *  Negamax is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *  @author Barry Becker
 */
public class NegaMaxStrategy extends AbstractSearchStrategy {
    /**
     * Construct NegaMax the strategy given a controller interface.
     * @inheritDoc
     */
    public NegaMaxStrategy( Searchable controller, ParameterArray weights ) {
        super( controller , weights);
    }

    /**
     * @inheritDoc
     */
    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {
        // need to negate alpha and beta on initial call.
        Range window = getOptions().getInitialSearchWindow();
        return searchInternal( lastMove, lookAhead_, (int)window.getMax(), (int)window.getMin(), parent);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                       int alpha, int beta, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove)list.get( 0 );

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++);

            selectedMove = searchInternal( theMove, depth-1, -beta, -Math.max(alpha, bestInheritedValue), child );

            searchable_.undoInternalMove( theMove );

            int selectedValue = -selectedMove.getInheritedValue();
            theMove.setInheritedValue( selectedValue );

            if ( selectedValue > bestInheritedValue ) {
                bestMove = theMove;
                bestInheritedValue = selectedValue;
                if ( alphaBeta_ ) {
                    if (bestInheritedValue >= beta) {
                        System.out.println("pruning because bestInheritedValue=" + bestInheritedValue+" > "+ beta);
                        break;
                    }
                }
            }
        }
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return !lastMove.isPlayer1();
    }
}