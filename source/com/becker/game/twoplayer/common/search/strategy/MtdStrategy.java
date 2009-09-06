package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.*;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.twoplayer.common.*;

/**
 * Memory enhanced Test Driver search.
 *  This strategy class defines the MTD search algorithm.
 *  See http://home.tiscali.nl/askeplaat/mtdf.html
 *
 *  @author Barry Becker  2/07
 */
public final class MtdStrategy extends AbstractSearchStrategy
{

    /** The "memory" search strategy to use. Must use memory/cache to avoid researching overhead. */
    private AbstractSearchStrategy searchWithMemory_;

    /**
     * Construct NegaMax the strategy given a controller interface.
     * @param controller
    */
    public MtdStrategy( Searchable controller )
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

        searchWithMemory_ =  new NegaScoutMemoryStrategy(searchable_);

        TwoPlayerMove selectedMove = searchInternal( lastMove, weights, depth, quiescentDepth, alpha, parent);
        return (selectedMove != null)? selectedMove : lastMove;
    }


    private TwoPlayerMove searchInternal( TwoPlayerMove lastMove, ParameterArray weights,
                                          int depth, int quiescentDepth,
                                          int f,  SearchTreeNode parent )
    {
        int g = f;
        int upperBound = SearchStrategy.INFINITY;
        int lowerBound =-SearchStrategy.INFINITY;
        int beta;

        TwoPlayerMove selectedMove = null;
        while (lowerBound < upperBound)  {
            if (g == lowerBound) {
                beta = g + 1;
            }
            else {
                beta = g;
            }
            selectedMove = searchWithMemory_.search(lastMove, weights, depth, quiescentDepth, beta -1, beta, parent);
            g = selectedMove.getInheritedValue();

            if (g < beta) {
                upperBound = g;
            }
            else {
                lowerBound = g;
            }
        }
        return selectedMove;
    }

}