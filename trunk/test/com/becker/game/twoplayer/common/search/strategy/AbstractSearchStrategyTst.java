package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameWeights;
import com.becker.game.common.GameWeightsStub;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.SearchableStub;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.*;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.TestCase;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
@SuppressWarnings({"ClassWithTooManyMethods"})
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

    /** @return Describes the way that we should evaluate moves at each ply. */
    protected abstract EvaluationPerspective getEvaluationPerspective();

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
        options.setMaxQuiescentDepth(3);
        return opts;
    }


    /**
     * Edge case where no searching is actually done. The found move will be the root.
     */
    public void testZeroLookAheadSearch() {
        searchOptions.setLookAhead(0);
        verifyResult(new ZeroLevelGameTreeExample(false, getEvaluationPerspective()),
                getZeroLookAheadResult());
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadPlayer1Search() {
        searchOptions.setLookAhead(1);
        verifyResult(new OneLevelGameTreeExample(true, getEvaluationPerspective()),
                getOneLevelLookAheadPlayer1Result());
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadPlayer2Search() {
        searchOptions.setLookAhead(1);
        verifyResult(new OneLevelGameTreeExample(false, getEvaluationPerspective()),
                getOneLevelLookAheadPlayer2Result());
    }

    public void testTwoLevelPlayer1Search() {
        searchOptions.setLookAhead(2);
        verifyResult(new TwoLevelGameTreeExample(true, getEvaluationPerspective()),
                getTwoLevelPlayer1Result());
    }

    public void testTwoLevelPlayer2Search() {
        searchOptions.setLookAhead(2);
        verifyResult(new TwoLevelGameTreeExample(false, getEvaluationPerspective()),
                getTwoLevelPlayer2Result());
    }

    public void testTwoLevelQuiescensePlayer1Search() {
        searchOptions.setLookAhead(2);
        searchOptions.setQuiescence(true);
        verifyResult(new TwoLevelQuiescentExample(true, getEvaluationPerspective()),
                getTwoLevelQuiescensePlayer1Result());
    }

    public void testTwoLevelQuiescensePlayer2Search() {
        searchOptions.setLookAhead(2);
        searchOptions.setQuiescence(true);
        verifyResult(new TwoLevelQuiescentExample(false, getEvaluationPerspective()),
                getTwoLevelQuiescensePlayer2Result());
    }

    public void testTwoLevelQuiescenseABPlayer1Search() {
        searchOptions.setLookAhead(2);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyResult(new TwoLevelQuiescentExample(true, getEvaluationPerspective()),
                getTwoLevelQuiescenseABPlayer1Result());
    }

    public void testTwoLevelQuiescenseABPlayer2Search() {
        searchOptions.setLookAhead(2);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyResult(new TwoLevelQuiescentExample(false, getEvaluationPerspective()),
                getTwoLevelQuiescenseABPlayer2Result());
    }

    public void testPruneTwoLevelWithoutABSearchPlayer1() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(false);
        verifyResult(new AlphaPruneExample(true, getEvaluationPerspective()),
                getPruneTwoLevelWithoutABResultPlayer1());
    }

    public void testPruneTwoLevelWithABSearchPlayer1() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPruneExample(true, getEvaluationPerspective()),
                getPruneTwoLevelWithABSearchPlayer1());
    }

    public void testPruneTwoLevelWithABSearchPlayer2() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPruneExample(false, getEvaluationPerspective()),
                getPruneTwoLevelWithABSearchPlayer2());
    }

    public void testThreeLevelPlayer1Search() {
        verifyResult(new ThreeLevelGameTreeExample(true, getEvaluationPerspective()),
                getThreeLevelPlayer1Result());
    }

    public void testThreeLevelPlayer2Search() {
        verifyResult(new ThreeLevelGameTreeExample(false, getEvaluationPerspective()),
                getThreeLevelPlayer2Result());
    }

    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new ThreeLevelGameTreeExample(false, getEvaluationPerspective()),
                getThreeLevelWithABResult());
    }

    public void testFourLevelSearchPlayer1() {
        searchOptions.setLookAhead(4);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getFourLevelPlayer1Result());
    }

    public void testFourLevelSearchPlayer2() {
        searchOptions.setLookAhead(4);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getFourLevelPlayer2Result());
    }

    public void testFourLevelABSearchPlayer1() {
        searchOptions.setLookAhead(4);
        searchOptions.setAlphaBeta(true);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getFourLevelABPlayer1Result());
    }

    public void testFourLevelABSearchPlayer2() {
        searchOptions.setLookAhead(4);
        searchOptions.setAlphaBeta(true);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getFourLevelABPlayer2Result());
    }

    // the following results are for minimax and negamax. Other algorithms may be slightly different (better)

    protected SearchResult getZeroLookAheadResult() {
        return new SearchResult(TwoPlayerMoveStub.ROOT_ID, 6, 0);
    }

    protected SearchResult getOneLevelLookAheadPlayer1Result() {
        return new SearchResult("1", -2, 2);
    }
    protected SearchResult getOneLevelLookAheadPlayer2Result() {
        return new SearchResult("0", -8, 2);
    }
    protected SearchResult getTwoLevelPlayer1Result() {
        return new SearchResult("1", 2, 6);
    }
    protected SearchResult getTwoLevelPlayer2Result() {
        return new SearchResult("0", 7, 6);
    }

    protected SearchResult getTwoLevelQuiescensePlayer1Result() {
        return new SearchResult("0", 3, 12);
    }
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("1", 4, 12);
    }
    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 3, 12);
    }
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("1", 4, 11);
    }

    protected SearchResult getPruneTwoLevelWithoutABResultPlayer1() {
        return new SearchResult("0", 5, 6);
    }
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 5, 5);
    }
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 6);
    }
    protected SearchResult getThreeLevelPlayer1Result() {
        return new SearchResult("0", -4, 14);
    }
    protected SearchResult getThreeLevelPlayer2Result() {
        return new SearchResult("0", -5, 14);
    }
    protected SearchResult getThreeLevelWithABResult() {
        return new SearchResult( "0", -5, 13);
    }
    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 27, 30);
    }
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("1", 14, 30);
    }
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 27, 18);
    }
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 14, 26);
    }

    /**
     * Verify move that was found using search strategy under test.
     * @param example  game tree to use
     * @param expectedSearchResult
     */
    protected void verifyResult(GameTreeExample example, SearchResult expectedSearchResult) {

        SearchStrategy searchStrategy = createSearchStrategy();
        TwoPlayerMoveStub foundMove =
                (TwoPlayerMoveStub)searchStrategy.search(example.getInitialMove(), null);

        String prefix = searchStrategy.getClass().getName();

        int inheritedValue = determineInheritedValue(foundMove.getInheritedValue(), example);

        SearchResult actualResult =
                new SearchResult(foundMove.getId(), inheritedValue, searchStrategy.getNumMovesConsidered());

        assertEquals(prefix + " Unexpected search result", expectedSearchResult, actualResult);
    }

    /**
     * This does not need to be as complicated as I once thought.
     * @return  adjusted inherited value
     */
    private int determineInheritedValue(int value, GameTreeExample example) {

        if (getEvaluationPerspective() == EvaluationPerspective.CURRENT_PLAYER)
        {
            return   example.getInitialMove().isPlayer1() ? -value : value;
        }
        return value;
    }
}