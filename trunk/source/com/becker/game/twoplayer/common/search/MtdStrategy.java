package com.becker.game.twoplayer.common.search;

import com.becker.game.twoplayer.common.*;
import com.becker.optimization.*;

/**
 *  This strategy class defines the MTD search algorithm.
 *  See http://home.tiscali.nl/askeplaat/mtdf.html
 *
 *  @author Barry Becker  2/07
 */
public final class MtdStrategy extends SearchStrategy
{
    // number of moves to consider at the top ply.
    // we use this number to determine how far into the search that we are.
    private int numTopLevelMoves_;

    private SearchStrategy searchWithMemory_;

    /** Construct NegaMax the strategy given a controller interface.
    */
    public MtdStrategy( Searchable controller )
    {
        super( controller );
    }

    /**
     * The NegaMax algorithm (with alpha-beta pruning)
     * This method is the crux of all 2 player zero sum games with perfect information
     *
     * @param lastMove the most recent move made by one of the players
     * @param weights coefficient for the evaluation polunomial that indirectly determines the best move
     * @param depth how deep in this local game tree that we are to search
     * @param alpha same as p2best but for the other player. (alpha)
     * @param beta the maximum of the value that it inherits from above and the best move found at this level (beta)
     * @param parent for constructing a ui tree. If null no game tree is constructed
     * @return the chosen move (ie the best move) (may be null if no next move)
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
        int upperBound = Integer.MAX_VALUE;
        int lowerBound = Integer.MIN_VALUE;
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
            g = (int) selectedMove.getInheritedValue();

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