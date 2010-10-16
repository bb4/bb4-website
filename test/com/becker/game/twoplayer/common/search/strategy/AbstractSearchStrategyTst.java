package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameWeights;
import com.becker.game.common.GameWeightsStub;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.SearchableStub;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer1Example;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer2Example;
import com.becker.game.twoplayer.common.search.examples.GameTreeExample;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.TestCase;

/**
 * Test minimax strategy independent of any particular game implementation.
 * @@ create base class and create tests for other strategies.
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

    protected abstract SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights);

    protected abstract boolean negateInheritedValue();

    /**
     * @return default search options for all games
     */
    public TwoPlayerOptions createTwoPlayerGameOptions() {
        TwoPlayerOptions opts =  new TwoPlayerOptions();
        SearchOptions options = opts.getSearchOptions();
        options.setLookAhead(3);
        options.setAlphaBeta(false);
        options.setPercentageBestMoves(100);
        options.setQuiescence(false);
        return opts;
    }


    /**
     * Edge case where no searching is actually done. There will be no found move.
     */
    public void testZeroLookAheadSearch() {

        searchOptions.setLookAhead(0);
        verifyResult(new SimpleGameTreeExample(), TwoPlayerMoveStub.ROOT_ID, 6, 0);
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadSearch() {
        searchOptions.setLookAhead(1);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 2);
    }

    public void testOneLevelWithQuiescenceAndABSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 2);
    }

    public void testOneLevelWithQuiescenceSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 2);
    }

    public void testTwoLevelSearch() {
        searchOptions.setLookAhead(2);
        verifyResult(new SimpleGameTreeExample(), "0", 7, 6);
    }

    public void testThreeLevelSearch() {
        verifyResult(new SimpleGameTreeExample(), "0", -5, 14);
    }

    /** best percentage ignore by base search algorithm. Only used when generating moves. */
    public void testThreeLevelBest20PercentSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setPercentageBestMoves(20);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 14);
    }

    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 14);
    }

    public void testPruneTwoLevelWithoutABSearch() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(false);
        verifyResult(new AlphaPrunePlayer1Example(), "0", -5, 6);
    }

    public void testPruneTwoLevelWithABSearchPlayer1() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPrunePlayer1Example(), "0", -5, 5);
    }

    public void testPruneTwoLevelWithABSearchPlayer2() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPrunePlayer2Example(), "1", -5, 5);
    }



    /**
     * Verify move that was found using search strategy under test.
     * @param example  game tree to use
     * @param expectedId
     * @param expectedInheritedValue
     * @param expectedConsideredMoves
     */
    protected void verifyResult(GameTreeExample example, String expectedId, int expectedInheritedValue, int expectedConsideredMoves) {

        SearchStrategy searchStrategy = createSearchStrategy();
        TwoPlayerMoveStub foundMove =
                (TwoPlayerMoveStub)searchStrategy.search(example.getInitialMove(), null);


        String prefix = searchStrategy.getClass().getName() + ": Unexpected ";

        assertEquals(prefix + "found move. got:" + foundMove,
                expectedId, foundMove.getId());

        int inheritedValue = negateInheritedValue() ? -foundMove.getInheritedValue() : foundMove.getInheritedValue();
        assertEquals(prefix + "inherited value for found move",
                expectedInheritedValue, inheritedValue);
        
        assertEquals(prefix + "number of searched nodes.",
                expectedConsideredMoves, searchStrategy.getNumMovesConsidered());
    }
}