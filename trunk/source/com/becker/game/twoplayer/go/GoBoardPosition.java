package com.becker.game.twoplayer.go;

import com.becker.common.Util;
import com.becker.game.common.BoardPosition;


/**
 * The GoBoardPosition describes the physical marker at a location on the board.
 * It can be empty or occupied. If occupied, then it is  either black or white and has a string owner.
 * A GoBoardPosition may have an eye owner if it is part of a group's eye.
 *
 * @see GoBoard
 * @author Barry Becker
 */
public final class GoBoardPosition extends BoardPosition implements GoMember
{

    // the string (connected set of stones) to which this stone belongs.
    private GoString string_;
    // if non-null then this position belongs to an eye string.
    // The group owner of the eye is different than the owner of the string.
    private GoEye eye_;

    // when true the stone has been visited already during a search.
    // This is a temporary state that is used for some traversal operations.
    private boolean visited_;

    // the amount this position contributes to the overall score.
    public double scoreContribution = 0.0;


    /**
     * create a new go stone.
     * @param row location.
     * @param col location.
     * @param string the string that this stone belongs to.
     * @param stone the stone at this position if there is one (use null if no stone).
     */
    public GoBoardPosition( int row, int col, GoString string, GoStone stone)
    {
        super( row, col, stone );
        string_ = string;
        eye_ = null;
        //assert ( string == null || string_.size() > 0 ): "Error creating board position at "+row+","+col+" String = "+string;
        visited_ = false;
    }

    /**
     * create a deep copy of this position.
     */
    public final BoardPosition copy()
    {
        GoBoardPosition pos = new GoBoardPosition( row_, col_, string_, (GoStone)piece_);
         return pos;
    }

    /**
     * copy all fields from another stone to this one.
     */
    public final void copy( GoBoardPosition pos )
    {
        super.copy(pos);
        setString( pos.getString() );
        setEye(pos.getEye());
        setVisited(pos.isVisited()); //??
    }

    /**
     * @param string  the string owner we are assignign to this stone.
     */
    public void setString( GoString string )
    {
        string_ = string;
    }

    /**
     * @return  the string owner for this stone.
     * There may be none if its blank and part of an eye, in that case null is returned.
     */
    public final GoString getString()
    {
        return string_;
    }

    /**
     * @return the group owner.
     * There is only one group owner that has the same ownership (color) as this stone.
     * The stone may also belong to to an eye in an opponent group, however.
     */
    public final GoGroup getGroup()
    {
       if (string_ != null)
           return string_.getGroup();
       else
           return null;
    }

    /**
     * @param eye  the eye owner this space is to be assigned to
     */
    public void setEye( GoEye eye )
    {
        eye_ = eye;
    }

    /**
     * @return  the eye that this space belongs to. May be null if no eye owner.
     */
    public final GoEye getEye()
    {
        return eye_;
    }

    /**
     *
     * @return  true if the string this stone belongs to is in atari
     */
    public boolean isInAtari(GoBoard b)
    {
       return (getString()!=null && getString().getLiberties(b).size()==1);
    }


    public void setVisited( boolean visited )
    {
        visited_ = visited;
    }

    public final boolean isVisited()
    {
        return visited_;
    }

    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();
        return clone;
    }

    /**
     * @return  true if this position is part of an eye.
     */
    public final boolean isInEye()
    {
        return eye_!=null;
    }

    /**
     * @return a string representation of the go board position
     */
    public String toString()
    {
        return super.toString()+ " s:"+Util.formatNumber(scoreContribution);
    }
}



