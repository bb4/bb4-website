package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.search.*;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;

/**
 * Memory enhanced Test Driver search.
 *  This strategy class defines the MTD search algorithm.
 *  See http://home.tiscali.nl/askeplaat/mtdf.html
 *
 *  @author Barry Becker  2/07
 */
public final class MtdStrategy implements SearchStrategy
{

    /** The "memory" search strategy to use. Must use memory/cache to avoid researching overhead. */
    private AbstractSearchStrategy searchWithMemory_;

    private Searchable searchable_;
    private ParameterArray weights_;
    /**
     * Construct NegaMax the strategy given a controller interface.
    */
    public MtdStrategy( Searchable controller, ParameterArray weights )
    {
        searchable_ = controller;
        weights_ = weights;
    }

    /**
     * @inheritDoc
     */
    public TwoPlayerMove search( TwoPlayerMove lastMove,
                                                    int alpha, int beta, SearchTreeNode parent )
    {

        searchWithMemory_ =  new NegaScoutMemoryStrategy(searchable_, weights_);

        TwoPlayerMove selectedMove = searchInternal( lastMove, 0, alpha, parent);
        return (selectedMove != null)? selectedMove : lastMove;
    }


    private TwoPlayerMove searchInternal( TwoPlayerMove lastMove, 
                                          int depth, int f,  SearchTreeNode parent )
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
            selectedMove = searchWithMemory_.search(lastMove, beta -1, beta, parent);
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


    public final long getNumMovesConsidered()
    {
        return searchWithMemory_.getNumMovesConsidered();
    }

    public final int getPercentDone()
    {
        return  searchWithMemory_.getPercentDone();
    }

    /**
     * Set an optional ui component that will update when the search tree is modified.
     * @param listener listener
     */
    public void setGameTreeEventListener(GameTreeViewable listener) {
         searchWithMemory_.setGameTreeEventListener(listener);
    }

    public void pause()
    {
       searchWithMemory_.pause();
    }


    public final boolean isPaused()
    {
        return searchWithMemory_.isPaused();
    }


    public void continueProcessing()
    {
       searchWithMemory_.continueProcessing();
    }

}