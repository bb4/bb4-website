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
    //private double totalScoreContribution_ = 0.0;
    private double positionalScore_ = 0.0;
    private double badShapeScore_ = 0.0;

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
    public GamePiece copy()
    {
        GoStone stone = new GoStone( ownedByPlayer1_, health_);
        stone.setTransparency( (short) 0 );
        stone.setAnnotation( null );
        return stone;
    }

    public void setPositionalScore(double s) {
        positionalScore_ = s;
    }

    /**
     * return score corresponding to how good this position is relative to the edge.
     *
    public double getPositionalScore() {
        return positionalScore_;
    } */

    public void setBadShapeScore(double s) {
       badShapeScore_ = s;
    }

    /**
     * return score corresponding to the bad shapiness of this position.
     *
    public double getBadShapeScore() {
        return badShapeScore_;
    } */


    public void setHealth( float health )
    {
        health_ = health;
    }

    public float getHealth()
    {
        return health_;
    }

    public String getLabel() {
        return this.isOwnedByPlayer1() ? "B" : "W";
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
    public void setDead(boolean dead)
    {
        isDead_ = dead;
    }

    /**
     * @return a deep copy of this stone
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     *  print more compactly than super class.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer( "" );
        //sb.append( type_ );
        sb.append( ownedByPlayer1_ ? 'B' : 'W' );
        if (GameContext.getDebugMode() > 2)  {
            sb.append("(pos:"+Util.formatNumber(positionalScore_));
            sb.append("+shp:"+Util.formatNumber(badShapeScore_)+')');
        }
        return sb.toString();
    }
}



