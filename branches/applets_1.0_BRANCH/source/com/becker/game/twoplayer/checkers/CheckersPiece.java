package com.becker.game.twoplayer.checkers;

import com.becker.game.common.board.GamePiece;

/**
 * The CheckersPiece describes the physical marker at a location on the board.
 * Its either a King or a Regular piece.
 *
 * @see CheckersBoard
 * @author Barry Becker
 */
public class CheckersPiece extends GamePiece {

    /** the basic kinds of pieces: REGULAR_PIECE, KING. */
    public static final char KING = 'X';

    public CheckersPiece( boolean player1, char type ) {
        super( player1, type);
    }

    /** Copy constructor */
    protected CheckersPiece(CheckersPiece piece) {
        super(piece);
    }

    /**
     *  Create a deep copy of the position
     */
    @Override
    public CheckersPiece copy()  {
        return new CheckersPiece(this);
    }

    /**
     * @return true if the piece has been kinged.
     */
    public boolean isKing() {
        return getType() == KING;
    }

}



