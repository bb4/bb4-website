package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 *  describes a change in state from one board
 *  position to the next in a Go game.
 *
 *  @see GoBoard
 *  @author Barry Becker
 */
public final class GoMove extends TwoPlayerMove
{

    // a linked list of the pieces that were captured with this move
    // null if there were no captures.
    public CaptureList captureList = null;

    /**
     * Constructor. This should never be called directly
     * instead call the factory method so we recycle objects.
     * use createMove to get moves, and dispose to recycle them
     */
    public GoMove( int destinationRow, int destinationCol,
            CaptureList captures,
            double val, int mvNum, GoStone stone )
    {
        super( (byte)destinationRow, (byte)destinationCol, val, mvNum, stone );
        captureList = captures;
    }

    /**
     * factory method for getting new moves.
     *  it uses recycled objects if possible.
     */
    public static GoMove createMove(
            int destinationRow, int destinationCol,
            CaptureList captures,
            double val, int mvNum, GoStone stone )
    {
        GoMove m = new GoMove( (byte)destinationRow, (byte)destinationCol,
                captures, val, mvNum, stone );
        return m;
    }

    /**
     * factory method for creating a passing move
     */
    public static GoMove createPassMove( double val, int mvNum, boolean player1)
    {
        GoMove m = createMove( 0,  0, null, val, mvNum, null );
        m.isPass_ = true;
        m.player1 = player1;
        return m;
    }


    /**
     *  make a deep copy of the move object
     */
    public final TwoPlayerMove copy()
    {
        CaptureList newList = null;
        if ( captureList != null ) {
            // then make a deep copy
            GameContext.log( 0, "******* GoMove: this is the capturelist we are copying:" + captureList.toString() );
            newList = captureList.copy();
        }
        GoMove cp = createMove( toRow_, toCol_,  newList, value, moveNumber, (piece==null)?null:(GoStone)piece.copy() );
        cp.player1 = player1;
        cp.selected = this.selected;
        cp.transparency = this.transparency;
        return cp;
    }

    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    public final String getSGFRepresentation()
    {
        // passes are not represented in SGF - so just skip it if the piece is null.
        if (piece == null)
             return "[]";
        StringBuffer buf = new StringBuffer("");
        char player = 'W';
        if ( piece.isOwnedByPlayer1() )
            player = 'B';
        buf.append( ';' );
        buf.append( player );
        buf.append( '[' );
        buf.append( (char) ('a' + toCol_ - 1) );
        buf.append( (char) ('a' + toRow_ - 1) );
        buf.append( ']' );
        buf.append( '\n' );
        return buf.toString();
    }

    public String toString()
    {
        String s = super.toString();
        if ( captureList != null ) {
            s += "num captured="+captureList.size();
        }
        return s;
    }

}



