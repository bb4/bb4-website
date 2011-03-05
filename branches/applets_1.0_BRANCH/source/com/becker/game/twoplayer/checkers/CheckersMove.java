package com.becker.game.twoplayer.checkers;

import com.becker.common.Location;
import com.becker.game.common.board.CaptureList;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 *  Describes a change in state from one board
 *  position to the next in a checkers game.
 *
 *  @see CheckersBoard
 *  @author Barry Becker
 */
public class CheckersMove extends TwoPlayerMove {

    /** the position that the piece is moving from */
    private Location fromLocation_;

    /** True if the piece just got kinged as a result of this move. */
    public boolean kinged;

    /**
     * a linked list of the pieces that were captured with this move
     * Usually this is null (if no captures) or 1, but could be more.
     */
    public CaptureList captureList = null;

    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private CheckersMove( Location origin, Location destination,
                          CaptureList captures, int val, GamePiece piece)  {
        super( destination, val,  piece );
        fromLocation_ = origin;
        kinged = false;
        captureList = captures;
    }

    /**
     * Factory method for getting new moves.
     * used to use recycled objects, but did not increase performance, so I removed it.
     * @return new checkers move
     */
    public static CheckersMove createMove( Location origin, Location destination,
                                           CaptureList captures, int val, GamePiece piece ) {

        CheckersMove m = new CheckersMove( origin, destination, captures, val, piece );

        if ( (piece.getType() == CheckersPiece.REGULAR_PIECE) &&
            ((piece.isOwnedByPlayer1() && m.getToRow() == CheckersController.NUM_ROWS) ||
             (!piece.isOwnedByPlayer1() && m.getToRow() == 1)) ) {
            m.kinged = true;
            m.setPiece(new CheckersPiece(m.isPlayer1(), CheckersPiece.KING));
            m.setPiece(piece);

        }
        return m;
    }

    /**
     * Copy constructor
     */
    protected CheckersMove(CheckersMove move) {
        super(move); 
        fromLocation_ = move.fromLocation_;
        kinged = move.kinged;
        if (move.captureList != null)
            captureList = move.captureList.copy();
    }

    /**
     * make a deep copy.
     */
    @Override
    public CheckersMove copy() {
        return new CheckersMove(this);
    }

    public void setToLocation(Location toPos) {
        toLocation_ = toPos;
    }
    
    public int getFromRow() {
        return fromLocation_.getRow();
    }

    public int getFromCol() {
        return fromLocation_.getCol();
    }

    public void removeCaptures( CheckersBoard b )  {
        if ( captureList != null )
            captureList.removeFromBoard( b );
    }

    public void restoreCaptures( CheckersBoard b ) {
        if ( captureList != null )
            captureList.restoreOnBoard( b );
    }

    public String toString()
    {
        String s = super.toString();
        if ( kinged )
            s += " (the piece was just kinged)";
        if ( captureList != null ) {
            s += captureList.toString();
        }
        s += " (" + fromLocation_ + ")->(" + toLocation_ + ")";
        return s;
    }
}


