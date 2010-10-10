package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.util.Util;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.ISearchableHelper;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.Progress;
import com.becker.game.twoplayer.common.search.SearchableHelper;
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

    protected abstract SearchableHelper createSearchableHelper();

    protected abstract SearchStrategyType getSearchStrategyToTest();

    /**
     * Edge case where no searching is actually done. The found move will be the last move.
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
    }

    public void verifyMoves(String desc, ExpectedMoveMatrix expectedMoves) {
        searchOptions.setSearchStrategyMethod(getSearchStrategyToTest());

        System.out.println(desc + " " + getSearchStrategyToTest());
        long time = System.currentTimeMillis();
        for (Progress prog : Progress.values()) {
            verifyMove(prog, true, expectedMoves, desc);
            verifyMove(prog, false, expectedMoves, desc);
        }
        double elapsed = (float)(System.currentTimeMillis() - time) / 1000.0;
        assertTrue("Took too long: " + elapsed, elapsed < 1.0);
        System.out.println("TOTAL TIME = " + Util.formatNumber( elapsed));
    }

    public void verifyMove(Progress prog, boolean player1, ExpectedMoveMatrix expectedMoves, String desc) {

        controller.restoreFromFile(helper.getTestFile(prog, player1));

        MoveInfo expectedNext = expectedMoves.getExpectedMove(prog, player1);

        SearchStrategy strategy = createSearchStrategy();
        TwoPlayerMove nextMove = searchForNextMove(strategy);
        long numMoves = strategy.getNumMovesConsidered();

        String info = getSearchStrategyToTest() + " " + desc + " "  + prog + " player1=" + player1;
        //System.out.print(info);
        //System.out.println("    new MoveInfo(" + nextMove.getConstructorString() + " " + numMoves + "),"  );

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
     * do the search for the next move.
     * @return the next move that was found after searching using the strategy and game under test.
     */
    protected TwoPlayerMove searchForNextMove(SearchStrategy strategy) {

        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();

        SearchTreeNode root = new SearchTreeNode(lastMove);
        TwoPlayerMove nextMove =
               strategy.search(lastMove, root);

        if (searchOptions.getLookAhead() > 0) {
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