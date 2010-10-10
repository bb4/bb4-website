package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.GameWeights;
import com.becker.game.common.GameWeightsStub;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.common.search.examples.AbstractGameTreeExample;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import junit.framework.TestCase;

/**
 * Test minimax strategy independent of any particular game implementation.
 * @@ create base class and create tests for other strategies.
 * @author Barry Becker
 */
public class MiniMaxStrategyTest extends TestCase {

    protected SearchOptions searchOptions;
    protected AbstractGameTreeExample exampleGame;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TwoPlayerOptions options = createTwoPlayerGameOptions();
        searchOptions = options.getSearchOptions();


        exampleGame = new SimpleGameTreeExample();
    }

    protected SearchStrategy createSearchStrategy() {
        Searchable searchable = new SearchableStub(searchOptions);
        GameWeights weights = new GameWeightsStub();
        return new MiniMaxStrategy(searchable, weights.getDefaultWeights());
    }

    /**
     * @return default search options for all games
     */
    public TwoPlayerOptions createTwoPlayerGameOptions() {
        TwoPlayerOptions opts =  new TwoPlayerOptions();
        SearchOptions options = opts.getSearchOptions();
        options.setLookAhead(2);
        options.setAlphaBeta(true);
        options.setPercentageBestMoves(100);
        options.setQuiescence(false);
        return opts;
    }


    /**
     * Edge case where no searching is actually done. There will be no found move.
     */
    public void testZeroLookAheadSearch() {

        searchOptions.setLookAhead(0);
        verifyResult(TwoPlayerMoveStub.ROOT_ID, 0);
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadSearch() {
        searchOptions.setLookAhead(1);
        verifyResult("0", 2);
    }


    public void testOneLevelWithQuiescenceSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        verifyResult("0", 2);
    }

    public void testTwoLevelSearch() {
        searchOptions.setLookAhead(2);
        verifyResult("0", 6);
    }


    protected void verifyResult(String expectedId, int expectedConsideredMoves) {

        SearchStrategy searchStrategy = createSearchStrategy();
        TwoPlayerMoveStub foundMove =
                (TwoPlayerMoveStub)searchStrategy.search(exampleGame.getInitialMove(), null);

        assertEquals("Unexpected found ahead move",
                expectedId, foundMove.getId());
        assertEquals("Unexpected number of searched nodes.",
                expectedConsideredMoves, searchStrategy.getNumMovesConsidered());
    }
    /*
    public void testOneLevelWithQuiescenceAndABSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyMoves("OneLevelWithQuiescenceAndAB", getExpectedOneLevelWithQuiescenceAndABMoves());
    }

    public void testTwoLevelLookAheadSearch() {
        searchOptions.setLookAhead(2);
        verifyMoves("TwoLevelLookAhead", getExpectedTwoLevelLookAheadMoves());
    }

    public void testFourLevelLookAheadSearch() {
        searchOptions.setLookAhead(4);
        verifyMoves("FourLevelLookAhead", getExpectedFourLevelLookaheadMoves());
    }

    public void testFourLevelBest20PercentSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setPercentageBestMoves(20);
        verifyMoves("FourLevelBest20Percent", getExpectedFourLevelBest20PercentMoves());
    }

    public void testTwoLevelWithQuiescenceLookAheadSearch() {
        searchOptions.setLookAhead(2);
        searchOptions.setQuiescence(true);
        verifyMoves("TwoLevelWithQuiescence", getExpectedTwoLevelWithQuiescenceMoves());
    }

    public void testTwoLevelWithQuiescenceAndABSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyMoves("TwoLevelWithQuiescenceAndAB", getExpectedTwoLevelWithQuiescenceAndABMoves());
    }

    public void testThreeLevelWithQuiescenceLookAheadSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setQuiescence(true);
        verifyMoves("ThreeLevelWithQuiescence", getExpectedThreeLevelWithQuiescenceMoves());
    }

    public void testFourLevelWithQuiescenceLookAheadSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setQuiescence(true);
        verifyMoves("FourLevelWithQuiescence", getExpectedFourLevelWithQuiescenceMoves());
    }

    public void testFourLevelNoAlphaBetaSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setAlphaBeta(false);
        verifyMoves("FourLevelNoAlphaBeta", getExpectedFourLevelNoAlphaBetaMoves());
    } */
}