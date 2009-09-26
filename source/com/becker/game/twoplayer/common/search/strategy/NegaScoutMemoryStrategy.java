package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;
import java.util.List;

/**
 *  This strategy class defines the NegaScout with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  See http://people.csail.mit.edu/plaat/mtdf.html
 *  and http://en.wikipedia.org/wiki/Negascout
 *
 *  @author Barry Becker 
 */
public final class NegaScoutMemoryStrategy extends NegaScoutStrategy
{

    /**
     * Construct NegaMax the strategy given a controller interface.
     */
    public NegaScoutMemoryStrategy( Searchable controller, ParameterArray weights )
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
        int bestVal = -SearchStrategy.INFINITY; 
        TwoPlayerMove selectedMove = null;
        TwoPlayerMove bestMove = (TwoPlayerMove) (list.get( 0 ));

        while ( !list.isEmpty() ) {
            if (pauseInterrupted())
                return lastMove;

            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            updatePercentDone(depth, list);

            searchable_.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, alpha, beta, i++ );

            selectedMove = searchInternal( theMove, depth-1, -beta, -alpha, child );

            searchable_.undoInternalMove( theMove );

            if (selectedMove == null) {
                // if this happens it means there isn't any possible move beyond theMove.
                continue;
            }

            int val = - selectedMove.getInheritedValue();
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
        int g = selectedMove.getInheritedValue();
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