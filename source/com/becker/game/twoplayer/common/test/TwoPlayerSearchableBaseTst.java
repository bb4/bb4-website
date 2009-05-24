package com.becker.game.twoplayer.common.test;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import junit.framework.*;
import com.becker.game.twoplayer.common.search.test.SearchableBaseTst;


/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 * @author Barry Becker
 */
public abstract class TwoPlayerSearchableBaseTst extends SearchableBaseTst {


    //private static final String PREFIX = "board/";

    private static final int DEBUG_LEVEL = 2;

    private static final int DEFAULT_LOOKAHEAD = 2;
    private static final int DEFAULT_BEST_PERCENTAGE = 100;



    /**
     * common initialization for all go test cases.
     * Override setOptionOverides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        searchable = ((TwoPlayerController)controller).getSearchable();

        TwoPlayerOptions options = createTwoPlayerGameOptions();
        options.setLookAhead(DEFAULT_LOOKAHEAD);
        options.setAlphaBeta(true);
        options.setPercentageBestMoves(DEFAULT_BEST_PERCENTAGE);
        options.setQuiescence(false);

         controller.setOptions(options);

        // this will load the resources for the specified game.
        //GameContext.loadGameResources("go");
        GameContext.setDebugMode(DEBUG_LEVEL);
        //controller_ = new GoController(getBoardSize(), getBoardSize(), 0);
    }


    /**
     * Create the game options
     */
    protected abstract TwoPlayerOptions createTwoPlayerGameOptions();


    private TwoPlayerOptions getTwoPlayerOptions()
    {
        return  (TwoPlayerOptions)controller.getOptions();
    }

    /** verify that we can retrieve the lookahead value. */
    public void testLookaheadValue() {

        System.out.println("in test lookahead");
        Assert.assertEquals("Unexpected lookahead value.", DEFAULT_LOOKAHEAD, searchable.getLookAhead());
        getTwoPlayerOptions().setLookAhead(7);
        Assert.assertEquals("Unexpected lookahead value.", 7, searchable.getLookAhead());
    }

    /** verify that we can retrieve the lookahead value. */
    public void testAlphaBetaValue() {

        Assert.assertEquals("Unexpected alphabeta value.", true, searchable.getAlphaBeta());
        getTwoPlayerOptions().setAlphaBeta(false);
        Assert.assertEquals("Unexpected alphabeta value.", false, searchable.getAlphaBeta());
    }

    /** verify that we can retrieve the quiescence value. */
    public void testQuiescenceValue()  {
        Assert.assertEquals("Unexpected quiessence value.", false, searchable.getQuiescence());
        getTwoPlayerOptions().setQuiescence(true);
        Assert.assertEquals("Unexpected quiessence value.", true, searchable.getQuiescence());
    }


    public void testFindCaptures5() {
        verifyCaptures("findCaptures5", 10, 2, 11);
    }

    private void verifyCaptures(String file, int row, int col, int numCaptures) {
/*
        restore(PREFIX  + file);

        GoMove move = new GoMove(row, col, 0, new GoStone(true));

        GoBoard board = (GoBoard)controller_.getBoard();

        int numWhiteStonesBefore = board.getNumStones(false);

        controller_.makeMove(move);

        int numWhiteStonesAfter = board.getNumStones(false);

        int actualNumCaptures = move.getNumCaptures();

        Assert.assertTrue("move.captures=" + actualNumCaptures + " expected "+numCaptures,
                              actualNumCaptures == numCaptures);
        int diffWhite = numWhiteStonesBefore - numWhiteStonesAfter;
        Assert.assertTrue("diff in num white stones ("+ diffWhite + ") not = numcaptures ("+numCaptures+')', diffWhite == numCaptures);

        controller_.undoLastMove();
        // verify that all the captured stones get restored to the board
        numWhiteStonesAfter = board.getNumStones(false);
        Assert.assertTrue("numWhiteStonesBefore="+numWhiteStonesBefore +" not equal numWhiteStonesAfter="+numWhiteStonesAfter,
                          numWhiteStonesBefore == numWhiteStonesAfter );
 */
    }



}
