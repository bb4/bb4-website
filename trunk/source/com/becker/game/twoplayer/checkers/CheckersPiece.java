package com.becker.game.twoplayer.checkers;

import com.becker.game.common.GamePiece;

/**
 *  the CheckersPiece describes the physical marker at a location on the board.
 *  Its either a King or a Regular piece.
 *
 * @see CheckersBoard
 * @author Barry Becker
 */
public class CheckersPiece extends GamePiece
{

    // the basic kinds of pieces: REGULAR_PIECE, KING.
    public static final char KING = 'X';

    public CheckersPiece( boolean player1, char type )
    {
        super( player1, type);
    }

    /**
     * @return true if the piece has been kinged.
     */
    public boolean isKing() {
        return getType() == KING;
    }

    /**
     *   create a deep copy of the position
     */
    public GamePiece copy()
    {
        CheckersPiece p = new CheckersPiece( ownedByPlayer1_, type_ );
        p.setTransparency( (short) 0 );
        p.setAnnotation( null );
        return p;
    }
}



