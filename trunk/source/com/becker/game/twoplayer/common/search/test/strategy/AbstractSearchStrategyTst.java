package com.becker.game.twoplayer.common.search.test.strategy;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import junit.framework.TestCase;

/**
 * Verify that all the methods in the SearchStrategy interface work as expected (especially search).
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchStrategyTst extends TestCase {

    protected TwoPlayerController controller;
    protected SearchOptions searchOptions;
    protected ISearchableHelper helper;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createSearchableHelper();
        controller = helper.createController();
        TwoPlayerOptions options = helper.createTwoPlayerGameOptions();
        searchOptions = options.getSearchOptions();
        controller.setOptions(options);
    }

    protected abstract SearchableHelper createSearchableHelper();

    protected abstract SearchStrategyType getSearchStrategyToTest();

    /**
     * Edge case where not searching is actually done. The found move will be the last move.
     */
    public void testZeroLookAheadSearch() {
        searchOptions.setLookAhead(0);
        verifyMove(getExpectedZeroLookAheadMove());
    }

    public void testOneLevelLookAheadSearch() {
        searchOptions.setLookAhead(1);
        verifyMove(getExpectedOneLevelLookAheadMove());
    }

    public void testOneLevelWithQuiescenceSearch() {

        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        verifyMove(getExpectedOneLevelWithQuiescenceMove());
    }

    public void testTwoLevelLookAheadSearch() {
        searchOptions.setLookAhead(2);
        verifyMove(getExpectedTwoLevelLookAheadMove());
    }

    public void testFourLevelLookAheadSearch() {
        searchOptions.setLookAhead(4);
        verifyMove(getExpectedFourLevelLookaheadMove());
    }

    public void testFourLevelBest20PercentSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setPercentageBestMoves(20);
        verifyMove(getExpectedFourLevelBest20PercentMove());
    }

    public void testFourLevelWithQuiescenceLookAheadSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setQuiescence(true);
        verifyMove(getExpectedFourLevelWithQuiescenceMove());
    }

    public void testFourLevelNoAlphaBetaSearch() {
        searchOptions.setLookAhead(4);
        searchOptions.setAlphaBeta(false);
        verifyMove(getExpectedFourLevelNoAlphaBetaMove());
    }

    public void verifyMove(TwoPlayerMove expectedMove) {
        searchOptions.setSearchStrategyMethod(getSearchStrategyToTest());
        controller.restoreFromFile(helper.getDefaultTestFile());
        System.out.println(getSearchStrategyToTest() + ":\nNow comparing expected:" + expectedMove + " with: " + getNextMove());
        assertEquals("We did not get the next move that we expected after searching.",
                expectedMove, getNextMove());
    }

    /**
     * @return the found move should match this for the test to pass.
     */
    protected abstract TwoPlayerMove getExpectedZeroLookAheadMove();
    protected abstract TwoPlayerMove getExpectedOneLevelLookAheadMove();
    protected abstract TwoPlayerMove getExpectedOneLevelWithQuiescenceMove();
    protected abstract TwoPlayerMove getExpectedTwoLevelLookAheadMove();
    protected abstract TwoPlayerMove getExpectedFourLevelLookaheadMove();
    protected abstract TwoPlayerMove getExpectedFourLevelBest20PercentMove();
    protected abstract TwoPlayerMove getExpectedFourLevelWithQuiescenceMove();
    protected abstract TwoPlayerMove getExpectedFourLevelNoAlphaBetaMove();


    /**
     * @return the next move that was found after searching using the strategy and game under test.
     */
    protected TwoPlayerMove getNextMove() {
        SearchStrategy strategy =
                searchOptions.getSearchStrategy(controller.getSearchable(), controller.getDefaultWeights());
        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();
        SearchTreeNode root = new SearchTreeNode(lastMove);

        return strategy.search(lastMove, SearchStrategy.INFINITY, -SearchStrategy.INFINITY, root);
    }
}