/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common;

import com.becker.common.geometry.Location;
import com.becker.game.common.GameContext;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.game.twoplayer.common.search.transposition.ZobristHash;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * For searching two player games
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerSearchable extends AbstractSearchable {

    protected final TwoPlayerBoard board_;
    protected final PlayerList players_;

    /** helps to find the best moves. */
    protected final BestMoveFinder bestMoveFinder_;

    /** Used to generate hashkeys. */
    protected final ZobristHash hash;


    /**
     * Constructor.
     */
    public TwoPlayerSearchable(final TwoPlayerBoard board,  PlayerList players, SearchOptions options) {

        super(board.getMoveList(), options);
        board_ = board;
        players_ = players;

        hash = new ZobristHash(board_);
        bestMoveFinder_ = new BestMoveFinder(getSearchOptions().getBestMovesSearchOptions());
    }

    /**
     * Copy constructor.
     */
    public TwoPlayerSearchable(TwoPlayerSearchable searchable) {

        this(searchable.getBoard().copy(), (PlayerList)searchable.players_.clone(), searchable.options_);
    }

    public TwoPlayerBoard getBoard() {
        return board_;
    }

    /**
     * @param m the move to play.
     */
    public void makeInternalMove( TwoPlayerMove m ) {

        TwoPlayerMove lastMove = (TwoPlayerMove)(moveList_.getLastMove());
        if (moveList_.getNumMoves() > 0) {
            // @@ we hit this a lot in the tests when running through gradle (because assertions are on). Should fix.
            GameContext.log(1, "Should not move twice in a row m=" + m + "\n getLastMove()=" + lastMove + "\n movelist = " + moveList_);
            //assert(lastMove.isPlayer1() != m.isPlayer1()):
            //        "can't go twice in a row m=" + m + "\n getLastMove()=" + lastMove + "\n movelist = " + moveList_;
        }

        getBoard().makeMove( m );

        if (m.isPassingMove())  {
            hash.applyPassingMove();
        } else {
            Location loc = m.getToLocation();
            hash.applyMove(loc, getBoard().getStateIndex(getBoard().getPosition(loc)));
        }
    }

    /**
     * takes back the most recent move.
     * @param m move to undo
     */
    public void undoInternalMove( TwoPlayerMove m ) {
        TwoPlayerMove lastMove = (TwoPlayerMove)moveList_.getLastMove();
        assert m.equals(lastMove) : "The move we are trying to undo ("+m+") in list="
                + moveList_+" was not equal to the last move ("+lastMove+"). all move=" + getBoard().getMoveList();

        Location loc = m.getToLocation();

        if (!m.isPassingMove()) {
            hash.applyMove(loc, getBoard().getStateIndex(getBoard().getPosition(loc)));
        }

        getBoard().undoMove();
    }

    /**
     * Evaluates from player 1's perspective
     * @return an integer value for the worth of the move.
     *  must be between -SearchStrategy.WINNING_VALUE and SearchStrategy.WINNING_VALUE.
     */
    public abstract int worth( TwoPlayerMove lastMove, ParameterArray weights);

    /**
     * given a move, determine whether the game is over.
     * If recordWin is true, then the variables for player1/2HasWon can get set.
     *  sometimes, like when we are looking ahead we do not want to set these.
     * @param lastMove the move to check. If null then return true. This is typically the last move played.
     * @param recordWin if true then the controller state will record wins
     */
    public boolean done( TwoPlayerMove lastMove, boolean recordWin ) {
        // the game can't be over if no moves have been made yet.
        if (moveList_.getNumMoves() == 0) {
            return false;
        }
        if (players_.anyPlayerWon()) {
            GameContext.log(0, "Game over because one of the players has won.");
            return true;
        }
        if (moveList_.getNumMoves() > 0 && lastMove == null) {
            Player currentPlayer = getCurrentPlayer();
            GameContext.log(0, "Game is over because there are no more moves for player " + currentPlayer);
            if (recordWin) {
                currentPlayer.setWon(true);
            }
            return true;
        }

        boolean won = (Math.abs( lastMove.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( lastMove.getValue() >= WINNING_VALUE )
                players_.getPlayer1().setWon(true);
            else
                players_.getPlayer2().setWon(true);
        }
        boolean maxMovesExceeded = moveList_.getNumMoves() >= getBoard().getMaxNumMoves();

        return (maxMovesExceeded || won);
    }

    private Player getCurrentPlayer()  {
        TwoPlayerMove move =  (TwoPlayerMove) moveList_.getLastMove();
        return move.isPlayer1() ? players_.getPlayer2() : players_.getPlayer1();
    }

    /**
     * @return true if the specified move caused one or more opponent pieces to become jeopardized
     */
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights) {
        return false;
    }

    /**
     * @return get a hash key that represents this board state (with negligibly small chance of conflict)
     */
    public HashKey getHashKey() {
        return hash.getKey();
    }
}