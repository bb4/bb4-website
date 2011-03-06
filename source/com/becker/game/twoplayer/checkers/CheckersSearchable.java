package com.becker.game.twoplayer.checkers;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerSearchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Defines how the computer should play checkers.
 * Extract moveEvaluator.
 *
 * @author Barry Becker
 */
public class CheckersSearchable extends TwoPlayerSearchable {

    /**
     * Constructor
     */
    public CheckersSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        super(board, players, options);
    }

    public CheckersSearchable(CheckersSearchable searchable) {
        super(searchable);
    }

    public CheckersSearchable copy() {
        return new CheckersSearchable(this);
    }

    /**
     *  generate all possible next moves
     */
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {

        MoveList moveList = new MoveList();
        int j, row,col;

        boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());
        MoveGenerator generator = new MoveGenerator(this, moveList, weights);

        // scan through the board positions. For each each piece of the current player's,
        // add all the moves that it can make.
        for ( row = 1; row <= CheckersController.NUM_ROWS; row++ ) {
            int odd = row % 2;
            for ( j = 1; j <= 4; j++ ) {
                col = 2 * j - odd;
                BoardPosition p = board_.getPosition( row, col );
                if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() == player1 ) {
                    generator.addMoves( p, lastMove);
                }
            }
        }
        return bestMoveFinder_.getBestMoves( player1, moveList);
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
    @Override
    public boolean done( TwoPlayerMove m, boolean recordWin ) {
        if (m == null)
            return true;

        boolean won = (Math.abs( m.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( m.isPlayer1() )
                players_.getPlayer1().setWon(true);
            else
                players_.getPlayer2().setWon(true);
        }
        if ( getNumMoves() >= getBoard().getMaxNumMoves() ) {
            won = true;
            if ( recordWin ) {
                if ( Math.abs( m.getValue() ) >= 0 )
                    players_.getPlayer1().setWon(true);
                else
                    players_.getPlayer2().setWon(true);
            }
        }
        return (won);
    }

    /**
     *  The primary way of computing the score for checkers is to just add up the pieces
     *  Kings should count more heavily. How much more is determined by the weights.
     *  We also give a slight bonus for advancement of non-kings to incent them to
     *  become kings.
     *  note: lastMove is not used
     *  @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth( Move lastMove, ParameterArray weights ) {
        int row, col, odd;
        float posScore = 0;
        float negScore = 0;

        for ( row = 1; row <= CheckersController.NUM_ROWS; row++ ) {
            odd = row % 2;
            for ( int j = 1; j <= 4; j++ ) {
                col = 2 * j - odd;
                BoardPosition p = getBoard().getPosition( row, col );
                if ( p.isOccupied() ) {
                    CheckersPiece piece = (CheckersPiece) p.getPiece();
                    boolean isPlayer1 = piece.isOwnedByPlayer1();
                    int advancement = isPlayer1? row : CheckersController.NUM_ROWS - row;
                    int pieceScore = calcPieceScore(piece.isKing(), advancement, weights);
                    if (isPlayer1)
                        posScore += pieceScore;
                    else
                        negScore -= pieceScore;
                }
            }
        }
        if ( posScore == 0 ) {
            // then there are no more of player 1's pieces
            return -WINNING_VALUE;
        }
        if ( negScore == 0 ) {
            // then there is no more of player 2's pieces
            return WINNING_VALUE;
        }
        return (int)(posScore + negScore);
    }


    /**
     *
     * @return the score for a particular piece.
     */
    private int calcPieceScore(boolean isKing, int advancement, ParameterArray weights) {
        int score = 0;
        if (isKing) {
               score += weights.get(CheckersWeights.KINGED_WEIGHT_INDEX).getValue();
        }
        else { // REGULAR_PIECE
               score += weights.get(CheckersWeights.PIECE_WEIGHT_INDEX).getValue();
               score += weights.get(CheckersWeights.ADVANCEMENT_WEIGHT_INDEX).getValue() * advancement;
        }
        return score;
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
