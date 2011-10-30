/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search.strategy.integration;

import com.becker.common.format.FormatUtil;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.ISearchableHelper;
import com.becker.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.becker.game.twoplayer.common.search.options.BruteSearchOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.Progress;
import com.becker.game.twoplayer.common.search.SearchableHelper;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import junit.framework.TestCase;

/**
 * Verify that all the methods in the SearchStrategy interface work as expected (especially search).
 * Derived test classes will exercise these methods for specific game instances.
 * Note that these are really integration tests and not unit tests.
 *
 * These classes are called by specific game implementations.
 *
 * @author Barry Becker
 */
public abstract class AbstractStrategyTst extends TestCase {

    protected TwoPlayerController controller;
    protected SearchOptions searchOptions;
    protected ISearchableHelper helper;

    protected static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    protected static final GamePiece PLAYER2_PIECE = new GamePiece(false);


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createSearchableHelper();
        controller = helper.createController();
        TwoPlayerOptions options = helper.createTwoPlayerGameOptions();
        searchOptions = options.getSearchOptions();
        controller.setOptions(options);
    }

    protected BruteSearchOptions getBruteSearchOptions() {
        return searchOptions.getBruteSearchOptions();
    }

    protected BestMovesSearchOptions getBestMovesOptions() {
        return searchOptions.getBestMovesSearchOptions();
    }

    protected abstract SearchableHelper createSearchableHelper();

    protected abstract SearchStrategyType getSearchStrategyToTest();

    /**
     * Edge case where no searching is actually done. The found move will be the last move.
     */
    public void testZeroLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(0);
        verifyMoves("ZeroLookAhead", getExpectedZeroLookAheadMoves());
    }

    public void testOneLevelLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(1);
        verifyMoves("OneLevelLookAhead", getExpectedOneLevelLookAheadMoves());
    }

    public void testOneLevelWithQuiescenceSearch() {
        getBruteSearchOptions().setLookAhead(1);
        getBruteSearchOptions().setQuiescence(true);
        verifyMoves("OneLevelWithQuiescence", getExpectedOneLevelWithQuiescenceMoves());
    }

    public void testOneLevelWithQuiescenceAndABSearch() {
        getBruteSearchOptions().setLookAhead(1);
        getBruteSearchOptions().setQuiescence(true);
        getBruteSearchOptions().setAlphaBeta(true);
        verifyMoves("OneLevelWithQuiescenceAndAB", getExpectedOneLevelWithQuiescenceAndABMoves());
    }

    public void testTwoLevelLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(2);
        verifyMoves("TwoLevelLookAhead", getExpectedTwoLevelLookAheadMoves());
    }

    public void testFourLevelLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(4);
        verifyMoves("FourLevelLookAhead", getExpectedFourLevelLookaheadMoves());
    }

    public void testFourLevelBest20PercentSearch() {
        getBruteSearchOptions().setLookAhead(4);
        getBestMovesOptions().setPercentageBestMoves(20);
        verifyMoves("FourLevelBest20Percent", getExpectedFourLevelBest20PercentMoves());
    }

    public void testTwoLevelWithQuiescenceLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(2);
        getBruteSearchOptions().setQuiescence(true);
        verifyMoves("TwoLevelWithQuiescence", getExpectedTwoLevelWithQuiescenceMoves());
    }

    public void testTwoLevelWithQuiescenceAndABSearch() {
        getBruteSearchOptions().setLookAhead(1);
        getBruteSearchOptions().setQuiescence(true);
        getBruteSearchOptions().setAlphaBeta(true);
        verifyMoves("TwoLevelWithQuiescenceAndAB", getExpectedTwoLevelWithQuiescenceAndABMoves());
    }

    public void testThreeLevelWithQuiescenceLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(3);
        getBruteSearchOptions().setQuiescence(true);
        verifyMoves("ThreeLevelWithQuiescence", getExpectedThreeLevelWithQuiescenceMoves());
    }

    public void testFourLevelWithQuiescenceLookAheadSearch() {
        getBruteSearchOptions().setLookAhead(4);
        getBruteSearchOptions().setQuiescence(true);
        verifyMoves("FourLevelWithQuiescence", getExpectedFourLevelWithQuiescenceMoves());
    }

    public void testFourLevelNoAlphaBetaSearch() {
        getBruteSearchOptions().setLookAhead(4);
        getBruteSearchOptions().setAlphaBeta(false);
        verifyMoves("FourLevelNoAlphaBeta", getExpectedFourLevelNoAlphaBetaMoves());
    }

    public void verifyMoves(String desc, ExpectedMoveMatrix expectedMoves) {
        searchOptions.setSearchStrategyMethod(getSearchStrategyToTest());

        System.out.println(desc + " " + getSearchStrategyToTest());
        long time = System.currentTimeMillis();
        for (Progress prog : Progress.values()) {
            verifyMove(prog, true, expectedMoves, desc);
            verifyMove(prog, false, expectedMoves, desc);
        }
        // an extra check to make sure the calculation does not take too long.
        double elapsed = (float)(System.currentTimeMillis() - time) / 1000.0;
        assertTrue("Took too long: " + elapsed, elapsed < 1.0);
        System.out.println("TOTAL TIME = " + FormatUtil.formatNumber(elapsed));
    }

    public void verifyMove(Progress prog, boolean player1, ExpectedMoveMatrix expectedMoves, String desc) {

        controller.restoreFromFile(helper.getTestFile(prog, player1));

        MoveInfo expectedNext = expectedMoves.getExpectedMove(prog, player1);

        SearchStrategy strategy = createSearchStrategy();
        TwoPlayerMove nextMove = searchForNextMove(strategy);
        long numMoves = strategy.getNumMovesConsidered();

        String info = getSearchStrategyToTest() + " " + desc + " "  + prog + " player1=" + player1;
        System.out.print(info);
        System.out.println("    new MoveInfo(" + nextMove.getConstructorString() + " " + numMoves + "),"  );

        if (expectedNext.hasMovesConsidered()) {
            assertEquals("Unexpected number of moves considered.",
                expectedNext.getNumMovesConsidered(), numMoves);
        }

        assertEquals(info +"\nWe did not get the next move that we expected after searching.",
                expectedNext.getMove(), nextMove);
    }

    /**
     * @return the found move should match this for the test to pass.
     */
    protected abstract ExpectedMoveMatrix getExpectedZeroLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceAndABMoves();
    protected abstract ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves();
    protected abstract ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceAndABMoves();
    protected abstract ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves();
    protected abstract ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves();


    /**
     * Do the search for the next move.
     * @return the next move that was found after searching using the strategy and game under test.
     */
    protected TwoPlayerMove searchForNextMove(SearchStrategy strategy) {

        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();

        SearchTreeNode root = new SearchTreeNode(lastMove);
        TwoPlayerMove nextMove =
               strategy.search(lastMove, root);

        if (searchOptions.getBruteSearchOptions().getLookAhead() > 0) {
            assertTrue("The last move (" + lastMove + ") was the same player as the next move (" + nextMove + ")",
                    lastMove.isPlayer1() != nextMove.isPlayer1());
        }
        return nextMove;
    }

    /**
     * @return the next move that was found after searching using the strategy and game under test.
     */
    protected SearchStrategy createSearchStrategy() {
        return searchOptions.getSearchStrategy(controller.getSearchable(),
                                               controller.getComputerWeights().getDefaultWeights());

    }
}