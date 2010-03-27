package com.becker.game.twoplayer.common.search.test.strategy;

import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.Progress;
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
        verifyMoves("ZeroLookAhead", getExpectedZeroLookAheadMoves());
    }

    public void testOneLevelLookAheadSearch() {
        searchOptions.setLookAhead(1);
        verifyMoves("OneLevelLookAhead", getExpectedOneLevelLookAheadMoves());
    }

    public void testOneLevelWithQuiescenceSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        verifyMoves("OneLevelWithQuiescence", getExpectedOneLevelWithQuiescenceMoves());
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
    } 

    public void verifyMoves(String desc, ExpectedMoveMatrix expectedMoves) {
        searchOptions.setSearchStrategyMethod(getSearchStrategyToTest());

        for (Progress prog : Progress.values()) {
            verifyMove(prog, true, expectedMoves, desc);
            verifyMove(prog, false, expectedMoves, desc);
        }
    }

    public void verifyMove(Progress prog, boolean player1, ExpectedMoveMatrix expectedMoves, String desc) {

        controller.restoreFromFile(helper.getTestFile(prog, player1));

        TwoPlayerMove expectedNextMove =  expectedMoves.getExpectedMove(prog, player1);
        TwoPlayerMove nextMove = getNextMove();
        String info = getSearchStrategyToTest() + " " + desc + " "  + prog + " player1=" + player1;
        //System.out.print(info);
        //System.out.println("   " + nextMove.getConstructorString() );

        assertEquals(info +"\nWe did not get the next move that we expected after searching.",
                expectedNextMove, getNextMove());
    }

    /**
     * @return the found move should match this for the test to pass.
     */
    protected abstract ExpectedMoveMatrix getExpectedZeroLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves();
    protected abstract ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves();


    /**
     * @return the next move that was found after searching using the strategy and game under test.
     */
    protected TwoPlayerMove getNextMove() {
        SearchStrategy strategy =
                searchOptions.getSearchStrategy(controller.getSearchable(), controller.getDefaultWeights());
        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();

        SearchTreeNode root = new SearchTreeNode(lastMove);
        TwoPlayerMove nextMove =
               strategy.search(lastMove, SearchStrategy.INFINITY, -SearchStrategy.INFINITY, root);

        if (searchOptions.getLookAhead() > 0) {
            assertTrue("The last move (" + lastMove + ") was the same player as the next move (" + nextMove + ")",
                    lastMove.isPlayer1() != nextMove.isPlayer1());
        }
        return nextMove;
    }
}