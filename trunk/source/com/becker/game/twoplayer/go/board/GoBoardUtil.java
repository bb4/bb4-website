package com.becker.game.twoplayer.go.board;

import com.becker.game.common.*;
import java.util.*;
import static com.becker.game.twoplayer.go.GoControllerConstants.USE_RELATIVE_GROUP_SCORING;


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
     * 
     * @param board
     * @return the change in score after updating the empty regions
     */
    public static float updateEmptyRegions(GoBoard board) {
        float diffScore = 0;
        //only do this when the midgame starts, since early on there is always only one connected empty region.
        int edgeOffset = 1;

        if (board.getNumMoves() <= 2 * board.getNumRows())
            return diffScore;
        if (board.getNumMoves() >= board.getTypicalNumMoves() / 4.3)
            edgeOffset = 0;
        int min = 1+edgeOffset;
        int rMax = board.getNumRows()-edgeOffset;
        int cMax = board.getNumCols()-edgeOffset;

        List<List> emptyLists = new LinkedList<List>();
        for ( int i = min; i <= rMax; i++ )  {
           for ( int j = min; j <= cMax; j++ ) {
               GoBoardPosition pos = (GoBoardPosition)board.getPosition(i, j);
               if (pos.getString() == null && !pos.isInEye()) {
                   assert pos.isUnoccupied();
                   if (!pos.isVisited()) {

                       // don't go all the way to the borders (until the end of the game),
                       // since otherwise we will likely get only one big empty region.
                       List<GoBoardPosition> empties = board.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED,
                                                                    min, rMax,  min, cMax);
                       emptyLists.add(empties);
                       Set nbrs = board.findOccupiedNeighbors(empties);
                       float avg = calcAverageScore(nbrs);

                       float score = avg * (float)nbrs.size() / Math.max(1, Math.max(nbrs.size(), empties.size()));
                       assert (score <= 1.0 && score >= -1.0): "score="+score+" avg="+avg;
                       Iterator it = empties.iterator();
                       while (it.hasNext()) {
                           GoBoardPosition p = (GoBoardPosition)it.next();
                           p.setScoreContribution(score);
                           diffScore += score;
                       }
                   }
               }
               else if (pos.isInEye()) {
                   pos.setScoreContribution(pos.getGroup().isOwnedByPlayer1()? 0.1 : -0.1);
               }
           }
        }

        unvisitPositionsInLists(emptyLists);
        return diffScore;
    }

    
    /**
     * @param stones actually the positions containing the stones.
     * @return the average scores of the stones in the list.
     */
    public static float calcAverageScore(Set stones)
    {
        float totalScore = 0;

        for (Object stone : stones) {
            GoBoardPosition p = (GoBoardPosition) stone;
            GoGroup group = p.getString().getGroup();
            if (USE_RELATIVE_GROUP_SCORING) {
                totalScore += group.getRelativeHealth();
            }
            else {
                totalScore += group.getAbsoluteHealth();
            }
        }
        return totalScore/stones.size();
    }
    
    /**
     * @return true if the stone is much weaker than the group
     */
    static boolean isStoneMuchWeaker(GoGroup group, GoStone stone)
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