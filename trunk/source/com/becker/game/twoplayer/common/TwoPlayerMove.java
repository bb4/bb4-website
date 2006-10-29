package com.becker.game.twoplayer.common;

import com.becker.game.common.*;
import com.becker.common.*;

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
    protected byte toRow_;
    protected byte toCol_;

    /**
     * The is the more accurate evaluated value from point of view of p1
     * It gets inherited from its descendants. It would be the real (perfect)
     * value of the position if the game tree is complete (which rarely happens in practice)
     */
    private double inheritedValue_;

    /**
     * true if player1 made the move
     */
    private boolean player1_;
    /**
     * this is the piece to use on the board. Some games only have one kind of piece .
     */
    private GamePiece piece_;
    /**
     * true if this move was generated during quiescent search.
     * @@ should not be in this class.
     */
    private boolean urgent_;
    /**
     *  if true then this move is a passing move.
     */
    protected boolean isPass_ = false;
    /**
     * make the piece represented by the move see through.
     */
    private short transparency_;
    /**
     * if true then in path to selected move.
     */
    private boolean selected_;


    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected TwoPlayerMove()
    {}

    /**
     * create a move object representing a transition on the board.
     */
    protected TwoPlayerMove( byte destinationRow, byte destinationCol,
                    double val, GamePiece p )
    {
        toRow_ = destinationRow;
        toCol_ = destinationCol;
        setValue(val);
        inheritedValue_ = getValue();
        selected_ = false;
        piece_ = p;
        if (p!=null)
            player1_ = p.isOwnedByPlayer1();
        transparency_ = 0; // default
        isPass_ = false;
    }

    /**
     * factory method for getting new moves. It uses recycled objects if possible.
     */
    public static TwoPlayerMove createMove( int destinationRow, int destinationCol,
                                   double val, GamePiece piece )
    {
        return new TwoPlayerMove( (byte)destinationRow, (byte)destinationCol, val, piece );
    }

    /**
     * @return  a deep copy.
     */
    public TwoPlayerMove copy()
    {
        TwoPlayerMove cp = createMove( toRow_, toCol_, getValue(),  piece_ );
        cp.transparency_ = transparency_;
        cp.selected_ = selected_;
        cp.urgent_ = urgent_;
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
     * @return  true if values are equal.
     */
    public final boolean equals( Object mv )
    {
        return (getValue() == ((TwoPlayerMove) mv).getValue());
    }

    /**
     * @return  true if the player (or computer) chose to pass this turn.
     */
    public final boolean isPassingMove()
    {
        return isPass_;
    }

    public double getInheritedValue() {
        return inheritedValue_;
    }

    public void setInheritedValue(double inheritedValue) {
        this.inheritedValue_ = inheritedValue;
    }

    public boolean isPlayer1() {
        return player1_;
    }

    public void setPlayer1(boolean player1) {
        this.player1_ = player1;
    }

    public GamePiece getPiece() {
        return piece_;
    }

    public void setPiece(GamePiece piece) {
        this.piece_ = piece;
    }

    public boolean isUrgent() {
        return urgent_;
    }

    public void setUrgent(boolean urgent) {
        this.urgent_ = urgent;
    }

    public short getTransparency() {
        return transparency_;
    }

    public void setTransparency(short transparency) {
        this.transparency_ = transparency;
    }

    public boolean isSelected() {
        return selected_;
    }

    public void setSelected(boolean selected) {
        this.selected_ = selected;
    }


    public String toString() {
        StringBuffer s = new StringBuffer();
        if (piece_!=null)
            s.append( piece_.isOwnedByPlayer1()? P1 : P2 );

        s.append( " val:" + Util.formatNumber(getValue()) );
        s.append( " inhrtd:" + Util.formatNumber(inheritedValue_) );
        if (piece_!=null)
            s.append( " piece:" + piece_.toString());
        //s.append(" sel:"+selected);
        s.append( "(" + toRow_ + ", " + toCol_ + ')' );
        if (urgent_)
            s.append(" urgent!");
        return s.toString();
    }
}

