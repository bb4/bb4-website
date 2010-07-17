package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
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
    private static final float DIFFERENCE_THRESHOLD = 0.6f;
    
    /** used to determine if a stone is dead or alive. */
    private static final float MIN_LIFE_THRESH = 0.2f;

    private GoBoardUtil() {}

    
    /**
     * set the visited flag back to false for a list of lists of stones
     */
    public static void unvisitPositionsInLists( List<List<GoBoardPosition>> lists )
    {
        for (List<GoBoardPosition> list : lists) {
            unvisitPositions( list );
        }
    }

    /**
     * set the visited flag back to false for a set of stones
     */
    public static void unvisitPositions( Collection<GoBoardPosition> positions )
    {
        // return the stone to the unvisited state
        for (GoBoardPosition position : positions) {
            position.setVisited(false);
        }
    }
    
    /**
     * @return true if the stone is much weaker than the group
     */
    public static boolean isStoneMuchWeaker(IGoGroup group, GoStone stone)
    {
        return isStoneWeakerThanGroup(group, stone, DIFFERENCE_THRESHOLD);
    }

    /**
     * @param group
     * @param stone
     * @param threshold
     * @return return true of the stone is greater than threshold weaker than the group.
     */
    private static boolean isStoneWeakerThanGroup(IGoGroup group, GoStone stone, float threshold)
    {
        float groupHealth = group.getAbsoluteHealth();

        // for purposes of determining relative weakness. Don't allow the outer group to go out of its living range.
        if (group.isOwnedByPlayer1() &&  groupHealth < MIN_LIFE_THRESH) {
            groupHealth = MIN_LIFE_THRESH;
        } else if (!group.isOwnedByPlayer1() && groupHealth > -MIN_LIFE_THRESH) {
            groupHealth = -MIN_LIFE_THRESH;
        }
        float stoneHealth = stone.getHealth();
        boolean muchWeaker;
        if (stone.isOwnedByPlayer1())  {
            assert (!group.isOwnedByPlayer1());

            muchWeaker = (-groupHealth - stoneHealth > threshold);
        }
        else {
            assert (group.isOwnedByPlayer1());
            muchWeaker = (groupHealth + stoneHealth > threshold);
        }

        return muchWeaker;
    }

}