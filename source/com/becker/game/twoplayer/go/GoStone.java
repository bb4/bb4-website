package com.becker.game.twoplayer.go;

import com.becker.common.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.GamePiece;

/**
 * A GoStone describes the physical marker at a location on the board.
 * It is either a black or white stone.
 *
 * @see GoBoardPosition
 * @author Barry Becker
 */
public final class GoStone extends GamePiece implements GoMember
{

    // The health is a number representing the influence of player1(black).
    // A living black stone has a positive health, while a black stone in poor health
    // has a negative health. The reverse is true for white.
    // the range  is (-1.0 to 1.0)
    private float health_;

    // if true then the stone is dead and implicitly removed from the board.
    // this can only get set to true at the very end of the game when both players have passed.
    private boolean isDead_;

    // these vars are for storing debug information about this stone contribution to the overall board state worth value
    // package protected.
    double totalScoreContribution = 0.0;
    double positionalScore = 0.0;
    double badShapeScore = 0.0;

    /**
     * create a new go stone.
     * @param player1 true if owned by player1.
     */
    public GoStone( boolean player1)
    {
        super( player1, REGULAR_PIECE);
        health_ = 0.0f;
    }

    /**
     * create a new go stone.
     * @param player1 true if owned by player1.
     * @param health health of the group that this stone belongs to.
     */
    public GoStone( boolean player1, float health)
    {
        super( player1, REGULAR_PIECE);
        health_ = health;
    }

    /**
     * create a deep copy of this stone
     */
    public final GamePiece copy()
    {
        GoStone stone = new GoStone( ownedByPlayer1_, health_);
        stone.setTransparency( (short) 0 );
        stone.setAnnotation( null );
        return stone;
    }

    /**
     * copy all fields from another stone to this one.
     */
    public final void copy( GoStone stone )
    {
        ownedByPlayer1_ = stone.isOwnedByPlayer1();
        type_ =  REGULAR_PIECE;
        health_ = stone.getHealth();
    }

    public void setHealth( float health )
    {
        health_ = health;
    }

    public final float getHealth()
    {
        return health_;
    }


    /**
     * @return true if the stone is dead.
     */
    public boolean isDead()
    {
        return isDead_;
    }

    /**
     * set the dead state of the stone to true.
     * It will now be rendered differently.
     */
    public final void setDead()
    {
        isDead_ = true;
    }

    /**
     * @return a deep copy of this stone
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();
        return clone;
    }

    /**
     *  print more compactly than super class
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer( "" );
        //sb.append( type_ );
        sb.append( ownedByPlayer1_ ? 'B' : 'W' );
        if (GameContext.getDebugMode()>2)  {
            sb.append("(pos:"+Util.formatNumber(positionalScore));
            sb.append("+shp:"+Util.formatNumber(badShapeScore)+")");
        }
        return sb.toString();
    }
}



