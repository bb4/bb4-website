package com.becker.game.twoplayer.common;

import com.becker.game.common.*;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.search.transposition.ZobristHash;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * For searching two player games
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerSearchable implements Searchable {

    protected TwoPlayerBoard board_;
    protected SearchOptions options_;
    protected PlayerList players_;
    protected MoveList moveList_;

    /** Used to generate hashkeys. */
    ZobristHash hash;

    /** helps to find the best moves. */
    protected BestMoveFinder bestMoveFinder_;


    public TwoPlayerSearchable(TwoPlayerBoard board,  PlayerList players, SearchOptions options) {
        board_ = board;
        moveList_ = board.getMoveList();
        players_ = players;
        options_ = options;

        hash =  new ZobristHash(board_);
        bestMoveFinder_ = new BestMoveFinder(getSearchOptions().getBestMovesSearchOptions());
    }

    public TwoPlayerSearchable(TwoPlayerSearchable searchable) {
        this((TwoPlayerBoard)searchable.getBoard().copy(), (PlayerList)searchable.players_.clone(), searchable.options_);
    }

    public TwoPlayerBoard getBoard() {
        return board_;
    }

    public int getNumMoves() {
        return moveList_.getNumMoves();
    }

    public MoveList getMoveList() {
        return moveList_;
    }

    public SearchOptions getSearchOptions() {
        return options_;
    }

    protected GameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }

    /**
     * By default just return the good set of statically evaluated moves.
     * Probably want to changes this so all games implement without static evaluation.
     */
    public MoveList generatePossibleMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return generateMoves(lastMove, weights, player1sPerspective);
    }

    /**
     * @param m the move to play.
     */
    public final void makeInternalMove( TwoPlayerMove m )
    {
        TwoPlayerBoard b = board_;
        TwoPlayerMove lastMove = (TwoPlayerMove)(moveList_.getLastMove());
        if (moveList_.getNumMoves() > 0) {
            assert(lastMove.isPlayer1() != m.isPlayer1()):
                    "can't go twice in a row m="+m+" getLastMove()="+ lastMove + " movelist = " + moveList_;
        }

        board_.makeMove( m );

        BoardPosition pos = b.getPosition(m.getToLocation());
        //assert pos != null : "pos was null at " + m.getToLocation() + " pass="+  m.isPassingMove();
        hash.applyMove(m, b.getStateIndex(pos));
    }

    /**
     * takes back the most recent move.
     * @param m  move to undo
     */
    public final void undoInternalMove( TwoPlayerMove m ) {
        TwoPlayerMove lastMove = (TwoPlayerMove)moveList_.getLastMove();
        assert m.equals(lastMove) : "The move we are trying to undo ("+m+") in list="
                + moveList_+" was not equal to the last move ("+lastMove+"). all move=" + board_.getMoveList();
        hash.applyMove(m, board_.getStateIndex(board_.getPosition(m.getToLocation())));
        board_.undoMove();
    }


    /**
     * Evaluates from player 1's perspective
     * @return an integer value for the worth of the move.
     *  must be between -SearchStrategy.WINNING_VALUE and SearchStrategy.WINNING_VALUE.
     */
    public abstract int worth( Move lastMove, ParameterArray weights );


    /**
     *  Statically evaluate a boards state to compute the value of the last move
     *  from player1's perspective.
     *  This function is a key function that must be created for each type of game added.
     *  If evaluating from player 1's perpective, then good moves for p1 are given a positive score.
     *  If evaluating from player 2's perpective, then good moves for p2 are given a positive score.
     *
     *  @param lastMove  the last move made
     *  @param weights  the polynomial weights to use in the polynomial evaluation function
     *  @param player1sPerspective if true, evaluate the board from p1's perspective, else p2's.
     *  @return the worth of the board from the specified players point of view
     */
    public final int worth( Move lastMove, ParameterArray weights, boolean player1sPerspective ) {

        int value = worth( lastMove, weights );
        return (player1sPerspective) ? value : -value;
    }

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
        if (moveList_.getNumMoves() > 0 && lastMove == null) {
            GameContext.log(0, "Game is over because there are no more moves");
            return true;
        }
        if (players_.anyPlayerWon()) {
            GameContext.log(0, "Game over because one of the players has won.");
            return true;
        }

        boolean won = (Math.abs( lastMove.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( lastMove.getValue() >= WINNING_VALUE )
                players_.getPlayer1().setWon(true);
            else
                players_.getPlayer2().setWon(true);
        }
        boolean maxMovesExceeded = moveList_.getNumMoves() >= board_.getMaxNumMoves();

        return (maxMovesExceeded || won);
    }

    /**
     * @return true if the specified move caused one or more opponent pieces to become jeopardized
     */
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
        return false;
    }

    /**
     * @return get a hash key that represents this board state (with negligibly small chance of conflict)
     */
    public Long getHashKey() {
        return hash.getKey();
    }
}