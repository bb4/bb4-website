package com.becker.game.twoplayer.checkers;

import com.becker.game.common.MoveList;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;


import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Defines how the computer should play checkers.
 *
 * @author Barry Becker
 */
public class CheckersController extends TwoPlayerController {

    // the checkers board must be 8*8
    public static final int NUM_ROWS = CheckersBoard.SIZE;
    public static final int NUM_COLS = CheckersBoard.SIZE;


    /**
     *  Constructor.
     */
    public CheckersController() {
        initializeData();
        board_ = new CheckersBoard();
    }

    /**
     * this gets the checkers specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new CheckersWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new CheckersOptions();
    }

    /**
     * The computer makes the first move in the game.
     */
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        MoveList moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights(), true );

        assert (!moveList.isEmpty());
        makeMove( moveList.getRandomMove() );

        player1sTurn_ = false;
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin()  {
        if (!getPlayers().anyPlayerWon())
             return 0;
        return getSearchable().worth(getLastMove(), weights_.getDefaultWeights());
    }

    /**
     * given a move determine whether the game is over.
     * If we are at maxMoves, the one with a greater value of pieces wins.
     * If the count of pieces is the same, then it is a draw.
     *
     * @param m the move to check
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over.
     */
    public boolean done( TwoPlayerMove m, boolean recordWin ) {
        if (m == null)
            return true;

        boolean won = (Math.abs( m.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( m.isPlayer1() )
                getPlayers().getPlayer1().setWon(true);
            else
                getPlayers().getPlayer2().setWon(true);
        }
        if ( getNumMoves() >= board_.getMaxNumMoves() ) {
            won = true;
            if ( recordWin ) {
                if ( Math.abs( m.getValue() ) >= 0 )
                    getPlayers().getPlayer1().setWon(true);
                else
                    getPlayers().getPlayer2().setWon(true);
            }
        }
        return (won);
    }

    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        return new CheckersSearchable(board, players, options);
    }
}
