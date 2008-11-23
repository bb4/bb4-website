package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import java.util.*;
import static com.becker.game.twoplayer.go.GoControllerConstants.USE_RELATIVE_GROUP_SCORING;


/**
 * static untility methods to support the GoBoard class.
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
     * @param positions to find bounding box of
     * @return bounding box of set of stones/positions passed in
     */
    static Box findBoundingBox(Set positions)  {
        int rMin = 100000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 100000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.
        Iterator it = positions.iterator();
        GoBoardPosition stone;
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            Iterator it1 = string.getMembers().iterator();

            while ( it1.hasNext() ) {
                stone = (GoBoardPosition) it1.next();
                int row = stone.getRow();
                int col = stone.getCol();
                if ( row < rMin ) rMin = row;
                if ( row > rMax ) rMax = row;
                if ( col < cMin ) cMin = col;
                if ( col > cMax ) cMax = col;
            }
        }

        return new Box(rMin, cMin, rMax, cMax);
    }

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
     * return true if the stones in this list exactly match those in an existing group
     */
    public static boolean groupAlreadyExists( List stones, GoBoard board )
    {
        Iterator gIt = board.getGroups().iterator();
        // first find the group that contains the stones
        while ( gIt.hasNext() ) {
            GoGroup g = (GoGroup) gIt.next();
            if ( GoGroupUtil.exactlyContains(g, stones) )
                return true;
        }
        return false;
    }


    public static int getBadShapeAux( BoardPosition adjacent1, boolean player1 )
    {
        if ( adjacent1.isUnoccupied() || adjacent1.getPiece().isOwnedByPlayer1() == player1 )
            return 1;
        return 0;
    }

    /**
     * @param group
     * @param stone
     * @param threshold
     * @return return true of the stone is greater than threshold weaker than the group.
     */
    static boolean isStoneWeaker(GoGroup group, GoStone stone, float threshold)
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

    /**
     * @return return true of the stone is weaker than the group.
     */
    static boolean isStoneWeaker(GoGroup group, GoStone stone)
    {
        return isStoneWeaker(group, stone, 0);
    }

    /**
     * @return true if the stone is much weaker than the group
     */
    static boolean isStoneMuchWeaker(GoGroup group, GoStone stone)
    {
        boolean weaker = isStoneWeaker(group, stone, DIFFERENCE_THRESHOLD);
        return weaker;
    }

    /**
     * 
     * @param board
     * @return the change in score after updating the empty regions
     */
    static float updateEmptyRegions(GoBoard board) {
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

        List emptyLists = new LinkedList();
        for ( int i = min; i <= rMax; i++ )  {
           for ( int j = min; j <= cMax; j++ ) {
               GoBoardPosition pos = (GoBoardPosition)board.getPosition(i, j);
               if (pos.getString() == null && !pos.isInEye()) {
                   assert pos.isUnoccupied();
                   if (!pos.isVisited()) {

                       // don't go all the way to the borders (until the end of the game),
                       // since otherwise we will likely get only one big empty region.
                       List empties = board.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED,
                                                                    min, rMax,  min, cMax);
                       emptyLists.add(empties);
                       Set nbrs = findOccupiedNeighbors(empties, board);
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
     * @param empties a list of unoccupied positions.
     * @return a list of stones bordering the set of empty board positions.
     */
    public static Set<GoBoardPosition> findOccupiedNeighbors(List empties, GoBoard board)
    {
        Iterator it = empties.iterator();
        Set<GoBoardPosition> allNbrs = new HashSet<GoBoardPosition>();
        while (it.hasNext()) {
            GoBoardPosition empty = (GoBoardPosition)it.next();
            assert (empty.isUnoccupied());
            Set<GoBoardPosition> nbrs = board.getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
            // add these nbrs to the set of all nbrs
            // (dupes automatically culled because HashSets only have unique members)
            allNbrs.addAll(nbrs);
        }
        return allNbrs;
    }


    /**
     * Remove all the groups in groups_ corresponding to the specified list of stones.
     * @param stones
     */
    public static void removeGroupsForListOfStones(List stones, GoBoard board) {
        Iterator mIt = stones.iterator();
        while ( mIt.hasNext() ) {
            GoBoardPosition nbrStone = (GoBoardPosition) mIt.next();
            // In the case where the removed stone was causing an atari in a string in an enemy group,
            // there is a group that does not contain a nbrstone that also needs to be removed here.
            board.getGroups().remove( nbrStone.getGroup() );
        }
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
     * @return a number corresponding to the number of clumps of 4 or empty triangles that this stone is connected to.
     * returns 0 if does not form bad shape at all. Large numbers indicate worse shape.
     * Possible bad shapes are :
     *  SHAPE_EMPTY_TRIANGLE :  X -   ,   SHAPE_CLUMP_OF_4 :  X X
     *                          X X                           X X
     */
    public static int formsBadShape(GoBoardPosition position, GoBoard board)
    {
        GoStone stone = (GoStone)position.getPiece();
        int r = position.getRow();
        int c = position.getCol();

        int severity =
             checkBadShape(stone, r, c,  1,-1, 1, board) +
             checkBadShape(stone, r, c, -1,-1, 1, board) +
             checkBadShape(stone, r, c,  1, 1, 1, board) +
             checkBadShape(stone, r, c, -1, 1, 1, board) +

             checkBadShape(stone, r, c,  1,-1, 2, board) +
             checkBadShape(stone, r, c, -1,-1, 2, board) +
             checkBadShape(stone, r, c,  1, 1, 2, board) +
             checkBadShape(stone, r, c, -1, 1, 2, board) +

             checkBadShape(stone, r, c,  1,-1, 3, board) +
             checkBadShape(stone, r, c, -1,-1, 3, board) +
             checkBadShape(stone, r, c,  1, 1, 3, board) +
             checkBadShape(stone, r, c, -1, 1, 3, board);

        return severity;
    }

    private static int checkBadShape(GoStone stone, int r, int c, int incr, int incc, int type, GoBoard board) {
        boolean player1 = stone.isOwnedByPlayer1();
        if ( board.inBounds( r + incr, c + incc ) ) {
            BoardPosition adjacent1 = board.getPosition( r + incr, c );
            BoardPosition adjacent2 = board.getPosition( r , c + incc);
            BoardPosition diagonal = board.getPosition( r + incr, c + incc);
            // there are 3 cases:
            //       a1 diag    X     XX    X
            //        X a2      XX    X    XX
            switch (type) {
                case 1 :
                    if (adjacent1.isOccupied() && adjacent2.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && adjacent2.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(diagonal, player1);
                    }  break;
                case 2 :
                    if (adjacent1.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(adjacent2, player1);
                    }  break;
                case 3 :
                    if (adjacent2.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent2.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(adjacent1, player1);
                    }  break;
               default : assert false;

            }
        }
        return 0;
    }

    public static String toString(GoBoard board) {

        int rows = board.getNumRows();
        int cols = board.getNumCols();
        StringBuffer buf = new StringBuffer((rows + 2) * (cols + 2));

        buf.append("   ");
        for ( int j = 1; j <= board.getNumCols(); j++ ) {
            buf.append(j % 10);
        }
        buf.append(' ');
        buf.append("\n  ");
        for ( int j = 1; j <= cols + 2; j++ ) {
            buf.append('-');
        }
        buf.append('\n');

        for ( int i = 1; i <= rows; i++ ) {
            buf.append(i / 10);
            buf.append(i % 10);
            buf.append('|');
            for ( int j = 1; j <= cols; j++ ) {
                GoBoardPosition space = (GoBoardPosition) board.getPosition(i, j);
                if ( space.isOccupied() )     {
                    buf.append(space.getPiece().isOwnedByPlayer1()?'X':'O');
                }
                else {
                    buf.append(' ');
                }
            }
            buf.append('|');
            buf.append('\n');
        }
        return buf.toString();
    }

}