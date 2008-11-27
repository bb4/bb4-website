package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.common.*;
import java.util.*;


/**
 * static utility methods to support the GoBoard class.
 *
 * @see GoBoard
 * @author Barry Becker.
 */
public final class GoBoardUtil
{
    /**
     * an opponent stone must be at least this much more unhealthy to be considered part of an eye.
     * if its not that much weaker then we don't really have an eye.
     * @@ make this a game parameter .6 - 1.8 that can be optimized.
     */
    private static final float DIFFERENCE_THRESHOLD = 0.7f;
    
    /** used to determine if a stone is dead or alive. */
    private static final float MIN_LIFE_THRESH = 0.3f;

    private GoBoardUtil() {}

    
    /**
     * set the visited flag back to false for a list of lists of stones
     */
    public static void unvisitPositionsInLists( List lists )
    {
        Iterator it = lists.iterator();
        while ( it.hasNext() ) {
            unvisitPositions( (List) it.next() );
        }
    }

    /**
     * set the visited flag back to false for a set of stones
     */
    public static void unvisitPositions( Collection positions )
    {
        Iterator it = positions.iterator();
        // return the stone to the unvisited state
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            s.setVisited( false );
        }
    }
    
    /**
     * @return true if the stone is much weaker than the group
     */
    public static boolean isStoneMuchWeaker(GoGroup group, GoStone stone)
    {
        boolean weaker = isStoneWeakerThanGroup(group, stone, DIFFERENCE_THRESHOLD);
        return weaker;
    }

    /**
     * @param group
     * @param stone
     * @param threshold
     * @return return true of the stone is greater than threshold weaker than the group.
     */
    private static boolean isStoneWeakerThanGroup(GoGroup group, GoStone stone, float threshold)
    {
        float groupHealth = group.getAbsoluteHealth();
        // for purposes of determining relative weakness. Don't allow the outer group to go out of its living range.
        if (group.isOwnedByPlayer1() &&  groupHealth < MIN_LIFE_THRESH) {
            groupHealth = MIN_LIFE_THRESH;
        } else if (!group.isOwnedByPlayer1() && groupHealth > -MIN_LIFE_THRESH) {
            groupHealth = -MIN_LIFE_THRESH;
        }
        float stoneHealth = stone.getHealth();
        if (stone.isOwnedByPlayer1())  {
            assert (!group.isOwnedByPlayer1());
            //System.out.println("-" + groupHealth +" - "+ stoneHealth +" = "+ (-groupHealth - stoneHealth) );
            return (-groupHealth - stoneHealth > threshold);
        }
        else {
            assert (group.isOwnedByPlayer1());
            return ( groupHealth + stoneHealth > threshold);
        }
    }

}