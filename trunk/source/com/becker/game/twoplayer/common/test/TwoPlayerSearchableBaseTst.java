package com.becker.game.twoplayer.common.test;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import junit.framework.*;
import com.becker.game.twoplayer.common.search.test.SearchableBaseTst;
import com.becker.optimization.parameter.ParameterArray;
import java.util.List;


/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 * @author Barry Becker
 */
public abstract class TwoPlayerSearchableBaseTst extends SearchableBaseTst {

    private static final int DEFAULT_DEBUG_LEVEL = 2;

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
        GameContext.setDebugMode(getDebugLevel());
        //controller_ = new GoController(getBoardSize(), getBoardSize(), 0);
    }


    /**
     * Create the game options
     */
    protected abstract TwoPlayerOptions createTwoPlayerGameOptions();

    /**
     * @return an initial move by player one.
     */
    protected abstract TwoPlayerMove createInitialMove();


    protected int getDebugLevel() {
        return DEFAULT_DEBUG_LEVEL;
    }

    protected TwoPlayerOptions getTwoPlayerOptions()  {
        return  (TwoPlayerOptions)controller.getOptions();
    }

    protected TwoPlayerController getTwoPlayerController() {
        return (TwoPlayerController)controller;
    }

    /** verify that we can retrieve the lookahead value. */
    public void testLookaheadValue() {

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


    /**verify that we are not done if we are at the very start of the game.  */
    public void testDoneBeforeAnyMovesMade() {

        Assert.assertFalse("We cannot be done if no moves have been made yet. ",
                searchable.done(null, false));
    }

    /** If next move is null, and we have at least one move made, then done will be true.  */
    public void testDoneNullAfterFirstMove() {

        controller.computerMovesFirst();
        Assert.assertTrue("We expect to be done if our next move is null and at least one move has been made. ",
                searchable.done(null, false));
    }

    /** Verify not done after first move..  */
    public void testDoneStartGame() {
        Assert.assertFalse("We don't expect to be done after making the very first move. ", searchable.done(createInitialMove(), false));
    }

    /**  load a game in the middle and verify that a legal midgame move doesn't return true.  */
    public void testDoneMidGame() {
        Assert.assertFalse(false);
    }

    /**load a game at the last move and verify that the next move results in done == true  */
    public void testDoneEndGame() {
        Assert.assertFalse(false);
    }

    /**  
     * Verify that we generate a reasonable list of moves to try next.
     * check that we can generate a list of initial moves and do not fail when the last move is null.
     */
   public void testGenerateMovesBeforeFirstMove() {
 
       List moves = searchable.generateMoves(null, getTwoPlayerController().getComputerWeights().getPlayer1Weights(), true);
       Assert.assertTrue("We expect the move list to be non-null very start of the game.", moves!= null);
       // usually we have a special way to generate the first move (see computerMovesFirst).
       // probably need to have game specific result here.
       int exp = getExpectedNumGeneratedMovesBeforeFirstMove();
       Assert.assertEquals("Unexpected number of generated moves before the first move has been played: " +moves.size(),  exp, moves.size() );
   }

   protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 0;
   }

   /**
     * Verify that we generate a reasonable list of moves to try next.
     * check that we can generate a list of initial moves and do not fail after the very first move.
     */
   public void testGenerateMovesAfterFirstMove() {
       controller.computerMovesFirst();
       ParameterArray wts = getTwoPlayerController().getComputerWeights().getPlayer1Weights();
       TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();
       List moves = searchable.generateMoves(lastMove, wts, true);

       Assert.assertTrue("We expect the move list to be non-null very start of the game.", moves!= null);
       Assert.assertTrue("We expected some valid next moves at the very start of the game.",  moves.size() > 0);
   }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
   public void testGenerateMovesMidGame() {
       Assert.assertFalse(false);
   }

    /** Load a game at the end and verify that there are no valid next moves. */
   public void testGenerateMovesEndGame() {
       Assert.assertFalse(false);
   }
   
    /**  Verify that we generate a correct list of urgent moves.  */
    public void  testGenerateUrgentMoves() {
        // there should not be any urgen moves at the very start of the gamel
         List moves = searchable.generateUrgentMoves(null, getTwoPlayerController().getComputerWeights().getPlayer1Weights(), true);
         Assert.assertTrue("We expected move list to be non-null.", moves!= null );
         Assert.assertTrue("We expected no urgent moves at the start of the game.",  moves.size() == 0);

         // load a typical game in the middle and verify that there are no urgent next moves.

         // load a critical game in the middle and verify that there are urgent next moves.

         // load a game at the end and verify that there are no urgent next moves.
    }

    /**  Verify that we can detect when a player is in jeopardy. */
    public void testInJeopardy() {
        boolean actualInJeopardy =
                searchable.inJeopardy(null, getTwoPlayerController().getComputerWeights().getPlayer1Weights(), true);
        Assert.assertFalse("We don't expect anything to be in jeopardy at the very start of the game.", actualInJeopardy);

        actualInJeopardy =
                searchable.inJeopardy(createInitialMove(), getTwoPlayerController().getComputerWeights().getPlayer2Weights(), false);
        Assert.assertFalse("We don't expect anything to be in jeopardy at the very start of the game.", actualInJeopardy);

        // load a typical game in the middle and verify a move that does not put anything in jeopardy.

        // load a critical game in the middle and verify a move that does put the other player in jeopardy.

    }

}
