package com.becker.game.twoplayer.common;

import com.becker.common.Util;
import com.becker.game.common.*;

/**
 *  This base class describes a change in state from one board
 *  position to the next in a game.
 *
 *  Note: when I first created this class I used a freeList to recycle
 *  old moves and avoid unnecessary object creation. However, while profiling,
 *  I found that this was actually slower than jnot using it.
 *
 *  We could save significant space by removing some of these members,
 *  and reducing the size of the remaining ones. eg toRow, toCol can be byte, value can be float, etc.
 *
 *  This is the only class where I dispense with setter and getter methods because I think efficiency
 *  of access for this class is important when searching 2 player games.
 *
 *  @see com.becker.game.common.Board
 *  @author Barry Becker
 */
public class TwoPlayerMove extends Move
{
    private static final String P1 = GameContext.getLabel("PLAYER1");
    private static final String P2 = GameContext.getLabel("PLAYER2");

    // the position of the move
    protected int toRow_;
    protected int toCol_;

    /**
     * The is the more accurate evaluated value from point of view of p1
     * It gets inherited from its descendants. It would be the real (perfect)
     * value of the position if the game tree is complete (which rarely happens in practice)
     */
    public double inheritedValue;

    /**
     * true if player1 made the move
     */
    public boolean player1;
    /**
     * this is the piece to use on the board. Some games only have one kind of piece .
     */
    public GamePiece piece;
    /**
     * true if this move was generated during quiescent search.
     */
    public boolean urgent;
    /**
     *  if true then this move is a passing move.
     */
    protected boolean isPass_ = false;
    /**
     * make the piece represented by the move see through.
     */
    public short transparency;
    /**
     * if true then in path to selected move.
     */
    public boolean selected;



    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected TwoPlayerMove()
    {
    }

    /**
     * create a move object representing a transition on the board.
     */
    protected TwoPlayerMove( int destinationRow, int destinationCol,
                    double val, int mvNum, GamePiece p )
    {
        toRow_ = destinationRow;
        toCol_ = destinationCol;
        value = val;
        inheritedValue = value;
        moveNumber = mvNum;
        selected = false;
        piece = p;
        if (p!=null)
            player1 = p.isOwnedByPlayer1();
        transparency = 0; // default
        isPass_ = false;
    }

    /**
     * factory method for getting new moves. It uses recycled objects if possible.
     */
    public static TwoPlayerMove createMove( int destinationRow, int destinationCol,
                                   double val, int mvNum, GamePiece piece )
    {
        return new TwoPlayerMove( destinationRow, destinationCol, val, mvNum, piece );
    }

    /**
     * @return  a deep copy.
     */
    public TwoPlayerMove copy()
    {
        TwoPlayerMove cp = this.createMove( toRow_, toCol_, value, moveNumber, piece );
        cp.transparency = this.transparency;
        cp.selected = this.selected;
        cp.urgent = this.urgent;
        return cp;
    }

    public final int getToRow()
    {
        return toRow_;
    }
    public final int getToCol()
    {
        return toCol_;
    }


    /**
     * @param mv  the move to compare to.
     * @return  true if equal.
     */
    public final boolean equals( Object mv )
    {
        return (value == ((TwoPlayerMove) mv).value);
    }

    /**
     * @return  true if the player (or computer) chose to pass this turn.
     */
    public final boolean isPassingMove()
    {
        return isPass_;
    }

    /**
     * return the SGF representation of the move.
     * SGF is a file format that can be used to represent most 2 player games.
     * @@ make this abstract and force adding to subclasses.
     */
    public String getSGFRepresentation()
    {
        GameContext.log(0, "error: not implemented yet" );
        return "";
    }

    public String toString()
    {
        //String s = "pos:"+row+","+ col;
        StringBuffer s = new StringBuffer();
        //s.append("<html>");
        if (piece!=null)
            s.append( piece.isOwnedByPlayer1()? P1 : P2 );

        s.append( " val:" + Util.formatNumber(value) );
        s.append( " inhrtd:" + Util.formatNumber(inheritedValue) );
        s.append( " mv:" + moveNumber );
        if (piece!=null)
            s.append( " piece:" + piece.toString());
        //s.append(" sel:"+selected);
        s.append( "(" + toRow_ + ", " + toCol_ + ")" );
        if (urgent)
            s.append(" urgent!");
        //s.append("</html>");
        return s.toString();
    }
}

