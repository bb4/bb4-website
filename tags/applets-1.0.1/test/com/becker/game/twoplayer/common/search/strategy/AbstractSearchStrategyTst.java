/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameWeights;
import com.becker.game.common.GameWeightsStub;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.SearchableStub;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.*;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.transposition.TranspositionTable;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.TestCase;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchStrategyTst extends TestCase {

    protected SearchOptions searchOptions;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TwoPlayerOptions options = createTwoPlayerGameOptions();
        searchOptions = options.getSearchOptions();
    }

    protected SearchStrategy createSearchStrategy() {

        Searchable searchable = new SearchableStub(searchOptions);
        GameWeights weights = new GameWeightsStub();
        return createSearchStrategy(searchable, weights.getDefaultWeights());
    }

    /** @return the Search strategy to test. */
    protected abstract SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights);

    /**
     * @return default search options for all games
     */
    public TwoPlayerOptions createTwoPlayerGameOptions() {
        TwoPlayerOptions opts =  new TwoPlayerOptions();
        opts.getSearchOptions().getBestMovesSearchOptions().setPercentageBestMoves(100);
        opts.getSearchOptions().getBestMovesSearchOptions().setPercentLessThanBestThresh(0);
        return opts;
    }

    /** @return Describes the way that we should evaluate moves at each ply. */
    protected abstract EvaluationPerspective getEvaluationPerspective();

    /**
     * Verify move that was found using search strategy under test.
     * @param example  game tree to use
     * @param expectedSearchResult collection of info regarding the search result.
     */
    protected void verifyResult(GameTreeExample example, SearchResult expectedSearchResult) {

        SearchStrategy searchStrategy = createSearchStrategy();
        TwoPlayerMoveStub foundMove =
                (TwoPlayerMoveStub)searchStrategy.search(example.getInitialMove(), null);

        String prefix = searchStrategy.getClass().getName();

        int inheritedValue = determineInheritedValue(foundMove.getInheritedValue(), example);

        SearchResult actualResult =
                new SearchResult(foundMove.getId(), inheritedValue, searchStrategy.getNumMovesConsidered());

        if (searchStrategy instanceof MemorySearchStrategy) {
            TranspositionTable table = ((MemorySearchStrategy) searchStrategy ).getTranspositionTable();
            System.out.println("table=" + table);
        }
        assertEquals(prefix + " Unexpected search result", expectedSearchResult, actualResult);
    }

    /**
     * This does not need to be as complicated as I once thought.
     * @return  adjusted inherited value
     */
    protected int determineInheritedValue(int value, GameTreeExample example) {

        if (getEvaluationPerspective() == EvaluationPerspective.CURRENT_PLAYER) {
            return   example.getInitialMove().isPlayer1() ? -value : value;
        }
        return value;
    }
}