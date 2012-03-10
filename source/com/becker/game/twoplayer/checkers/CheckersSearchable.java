/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.checkers;

import com.becker.game.common.MoveList;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerSearchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Defines how the computer should play checkers.
 *
 * @author Barry Becker
 */
public class CheckersSearchable extends TwoPlayerSearchable {

    /**
     * Constructor
     */
    public CheckersSearchable(TwoPlayerBoard board, PlayerList players) {
        super(board, players);
    }

    public CheckersSearchable(CheckersSearchable searchable) {
        super(searchable);
    }

    public CheckersSearchable copy() {
        return new CheckersSearchable(this);
    }

    /**
     * Generate all possible next moves
     */
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {

        MoveGenerator generator = new MoveGenerator(this, weights);
        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        MoveList moveList = generator.generateMoves(lastMove);

        return bestMoveFinder_.getBestMoves( player1, moveList);
    }

    /**
     * given a move determine whether the game is over.
     * If we are at maxMoves, the one with a greater value of pieces wins.
     * If the count of pieces is the same, then it is a draw.
     *
     * @param move the move to check. If null that implies there was no last move because we are out of moves.
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over.
     */
    @Override
    public boolean done( TwoPlayerMove move, boolean recordWin ) {
        if (move == null)  {
            System.out.println("done because move is null");
            return true;
        }

        boolean won = (Math.abs( move.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( move.isPlayer1() )
                players_.getPlayer1().setWon(true);
            else
                players_.getPlayer2().setWon(true);
        }

        if ( getNumMoves() >= getBoard().getMaxNumMoves() ) {
            System.out.println("getNumMoves()="+getNumMoves() + " getBoard().getMaxNumMoves()=" + getBoard().getMaxNumMoves());
            won = true;
            if ( recordWin ) {
                if ( Math.abs( move.getValue() ) >= 0 )
                    players_.getPlayer1().setWon(true);
                else
                    players_.getPlayer2().setWon(true);
            }
        }
        return (won);
    }

    /**
     * lastMove not used.
     * @return the value of the current board position
     */
    @Override
    public int worth( TwoPlayerMove lastMove, ParameterArray weights ) {
        return new BoardEvaluator(board_, weights).calculateWorth();
    }

    /**
     * @@ quiescent search not yet implemented for checkers
     * Probably we should return all moves that capture opponent pieces.
     *
     * @return list of urgent moves
     */
    public MoveList generateUrgentMoves(
            TwoPlayerMove lastMove, ParameterArray weights) {
        return new MoveList();
    }
}
