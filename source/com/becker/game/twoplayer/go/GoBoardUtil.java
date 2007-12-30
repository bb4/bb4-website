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
    static final float DIFFERENCE_THRESHOLD = 0.7f;

    private GoBoardUtil() {
    }

    /**
     * Get an adjacent neighbor stones restricted to the desired type.
     *
     * @param nbrStone   the neighbor to check
     * @param friendOwnedByP1  type of the center stone (can't use center.owner since center may be unnoccupied)
     * @param nbrs  hashset of the ngbors matching the criteria.
     * @param neighborType  one of NEIGHBOR_ANY, NEIGHBOR_ENEMY_ONLY, or NEIGHBOR_FRIENDLY_ONLY
     */
    static void getNobiNeighbor( GoBoardPosition nbrStone, boolean friendOwnedByP1, Set nbrs, NeighborType neighborType )
    {

        boolean correctNeighborType = true;
        switch (neighborType) {
            case ANY:
                correctNeighborType = true;
                break;
            case OCCUPIED:
                // note friendOwnedByP1 is intentionally ignored
                correctNeighborType = nbrStone.isOccupied();
                break;
            case ENEMY: // the opposite color
                if (nbrStone.isUnoccupied())
                    return;
                GoStone st = (GoStone)nbrStone.getPiece();
                correctNeighborType =  st.isOwnedByPlayer1() != friendOwnedByP1;
                break;
            case FRIEND: // the same color
                if (nbrStone.isUnoccupied())
                    return;
                correctNeighborType = (nbrStone.getPiece().isOwnedByPlayer1() == friendOwnedByP1);
                break;
            default : assert false: "unknown or unsupported neighbor type:"+neighborType;
        }
        if (correctNeighborType ) {
            nbrs.add( nbrStone );
        }
    }

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
        while ( it.hasNext() )
            unvisitPositions( (List) it.next() );
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


    // ------------------ Debugging methods below this point ------------------------

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     * @param stones list of stones to print
     */
    public static void debugPrintList( int logLevel, String title, Collection stones)
      {
           GameContext.log(logLevel, debugPrintListText(logLevel, title, stones));
      }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     * @param stones list of stones to print
     */
    private static String debugPrintListText( int logLevel, String title, Collection stones)
    {
        if (stones == null)
            return "";
        StringBuffer buf = new StringBuffer(title+'\n');
        if (logLevel <= GameContext.getDebugMode())  {
            Iterator it = stones.iterator();
            while (it.hasNext()) {
                GoBoardPosition stone = (GoBoardPosition)it.next();
                buf.append( stone.toString() +", ");
            }
        }
        return buf.substring(0, buf.length() - 2);
    }

    public static void debugPrintList( int logLevel, String title, List stones)
    {
        if (stones == null)
            return;
        GameContext.log(logLevel, debugPrintListText(logLevel, title, stones));
    }


    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    private static void debugPrintGroups( int logLevel, Set groups)
    {
        debugPrintGroups( logLevel,  "---The groups currently on the board are:", true, true, groups);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public static void debugPrintGroups( int logLevel, String title, boolean showBlack, boolean showWhite, Set groups)
    {
        if (logLevel <= GameContext.getDebugMode())  {
            GameContext.log( logLevel, title );
            GameContext.log( logLevel, getGroupsText(showBlack, showWhite, groups));
            GameContext.log( logLevel, "----" );
        }
    }


    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public static String getGroupsText(Set groups )
    {
        return getGroupsText(true, true, groups);
    }

    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public static String getGroupsText(boolean showBlack, boolean showWhite, Set groups)
    {
        StringBuffer groupText = new StringBuffer( "" );
        StringBuffer blackGroupsText = new StringBuffer(showBlack? "The black groups are :\n" : "" );
        StringBuffer whiteGroupsText = new StringBuffer((showBlack?"\n":"") + (showWhite? "The white groups are :\n" : ""));

        Iterator it = groups.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            if ( group.isOwnedByPlayer1() && (showBlack)) {
                //blackGroupsText.append( "black group owner ="+ group.isOwnedByPlayer1());
                blackGroupsText.append( group );
            }
            else if ( !group.isOwnedByPlayer1()  && showWhite) {
                //whiteGroupsText.append( "white group owner ="+ group.isOwnedByPlayer1());
                whiteGroupsText.append( group );
            }
        }
        groupText.append( blackGroupsText );
        groupText.append( whiteGroupsText );

        return groupText.toString();
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



    // ------------------ Confirmation (debug) methods below this point -------------

    static void confirmNoDupes( GoBoardPosition seed, List list )
    {
        Object[] stoneArray = list.toArray();

        for ( int i = 0; i < stoneArray.length; i++ ) {
            GoBoardPosition st = (GoBoardPosition) stoneArray[i];
            // make sure that this stone is not a dupe of another in the list
            for ( int j = i + 1; j < stoneArray.length; j++ ) {
                assert (!st.equals(stoneArray[j])): "found a dupe=" + st + " in " + list + "]n the seed = " + seed ;
            }
        }
    }


    public static void confirmUnvisited( List stones )
    {
        Iterator it = stones.iterator();
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            assert !p.isVisited(): p+" in "+stones+" was visited";
        }
    }


    public static int getBadShapeAux( BoardPosition adjacent1, boolean player1 )
    {
        if ( adjacent1.isUnoccupied() || adjacent1.getPiece().isOwnedByPlayer1() == player1 )
            return 1;
        return 0;
    }


    /**
     * verify that all the stones on the board are in the boards member list of groups.
     */
    public static void confirmStonesInValidGroups(Set groups, GoBoard board)
    {
        for ( int i = 1; i <= board.getNumRows(); i++ )
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition) board.getPosition( i, j );
                if ( space.isOccupied() )
                    confirmStoneInValidGroup( space, groups );
            }
    }

    /**
     * @param stone verify that this stone has a valid string and a group in the board's member list.
     */
    private static void confirmStoneInValidGroup( GoBoardPosition stone, Set groups )
    {
        GoString str = stone.getString();
        //boolean b = stone.getPiece().isOwnedByPlayer1();
        assert ( str!=null) : stone + " does not belong to any string!" ;
        GoGroup g = str.getGroup();
        boolean valid = false;
        Iterator gIt = groups.iterator();
        GoGroup g1;
        while ( !valid && gIt.hasNext() ) {
            g1 = (GoGroup) gIt.next();
            valid = g.equals(g1);
        }
        if ( !valid ) {
            debugPrintGroups( 0, "Confirm stones in valid groups failed. The groups are:",
                    g.isOwnedByPlayer1(), !g.isOwnedByPlayer1(), groups);
            assert false :
                   "Error: This " + stone + " does not belong to a valid group: " +
                    g + " \nThe valid groups are:" + groups;
        }
    }



    /**
     * verify that all the stones are marked unvisited.
     */
    public static void confirmAllUnvisited(GoBoard board)
    {
        GoBoardPosition stone = areAllUnvisited(board);
        if (stone != null)
           assert false : stone + " is marked visited";
    }


    /**
     * verify that all the stones are marked unvisited.
     */
    private static GoBoardPosition areAllUnvisited(GoBoard board)
    {
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                GoBoardPosition stone = (GoBoardPosition) board.getPosition( i, j );
                if (stone.isVisited())
                    return stone;
            }
        }
        return null;
    }


    /**
     * for every stone one the board verify that it belongs to exactly one group
    */
    public static void confirmAllStonesInUniqueGroups(Set groups)
    {
        Iterator grIt = groups.iterator();
        while ( grIt.hasNext() ) {  // for each group on the board
            GoGroup g = (GoGroup) grIt.next();
            confirmStonesInOneGroup( g, groups );
        }
    }

    /**
     * confirm that the stones in this group are not contained in any other group.
     */
    public static void confirmStonesInOneGroup( GoGroup group, Set groups)
    {
        Iterator strIt = group.getMembers().iterator();
        while ( strIt.hasNext() ) {  // foir each string in the group
            GoString string1 = (GoString) strIt.next();
            Iterator grIt = groups.iterator();
            while ( grIt.hasNext() ) {  // for each group on the board
                GoGroup g = (GoGroup) grIt.next();
                if ( !g.equals(group) ) {
                    Iterator it = g.getMembers().iterator();
                    while ( it.hasNext() ) {   // fro each string in that group
                        GoString s = (GoString) it.next();
                        if ( string1.equals(s) ) {
                            debugPrintGroups( 0, groups );
                            assert false: "ERROR: " + s + " contained by 2 groups" ;
                        }
                        confirmStoneInStringAlsoInGroup(s, g, groups);

                    }
                }
            }
        }
    }

    private static void confirmStoneInStringAlsoInGroup(GoString str, GoGroup group, Set groups) {
        //make sure that every stone in the string belongs in this group
        Iterator stoneIt = str.getMembers().iterator();
        while ( stoneIt.hasNext() ) {
            GoBoardPosition st1 = (GoBoardPosition) stoneIt.next();
            if ( st1.getGroup() != null && !group.equals(st1.getGroup()) ) {
                debugPrintGroups( 0, "Confirm stones in one group failed. Groups are:", true, true, groups );
                assert false:
                       st1 + " does not just belong to " + st1.getGroup()
                        + " as its ancestry indicates. It also belongs to " + group;
            }
        }
    }


    /**
     * For every stone in every group verify that the group determined from using that stone as a seed
     * matches the group that is claims by ancestry.
     * (expesnsive to check)
     */
    public static void confirmAllStonesInGroupsClaimed(Set groups, GoBoard board)
    {
        Iterator grIt = groups.iterator();
        while ( grIt.hasNext() ) {  // for each group on the board
            GoGroup parentGroup = (GoGroup) grIt.next();
            // for eash stone in that group
            Set parentGroupStones = parentGroup.getStones();
            Iterator sit = parentGroupStones.iterator();
            while ( sit.hasNext() ) {   // fro each string in that group
                 GoBoardPosition s = (GoBoardPosition) sit.next();
                 // compute the group from this stone and confirm it matches the parent group
                 List g = board.findGroupFromInitialPosition(s);
                 // perhaps we should do something more than check the size.
                if (g.size() != parentGroupStones.size())   {
                    debugPrintGroups( 0, "Confirm stones in groups they Claim failed. Groups are:", true, true, groups );
                    assert false :
                      debugPrintListText(0,"Calculated Group (seeded by "+s+"):",g) +"\n is not equal to the expected parent group:\n"+parentGroup;
                }
            }
        }
    }

    static void confirmNoEmptyStrings(Set groups)
    {
        for (Object g : groups)  {
            for (Object s : ((GoGroup)g).getMembers()) {
                GoString string = (GoString) s;
                assert (string.size() > 0): "There is an empty string in " + string.getGroup();
            }
        }
    }

    static void confirmNoStringsWithEmpties(Set groups)
    {
        for (Object g : groups)  {
            for (Object s : ((GoGroup)g).getMembers()) {
                GoString string = (GoString) s;
                assert (!string.areAnyBlank()): "There is a string with unoccupied positions: " + string;
            }
        }
    }

    /**
     *  confirm that all the strings in a group have nobi connections.
     */
    static void confirmGroupsHaveValidStrings(Set groups, GoBoard board)
    {
        Iterator it = groups.iterator();
        while ( it.hasNext() ) {
            GoGroup group = (GoGroup) it.next();
            GoGroupUtil.confirmValidStrings(group, board);
        }
    }

    static boolean confirmStoneListContains(List largerGroup, List smallerGroup)
    {
        Iterator smallIt = smallerGroup.iterator();
        while (smallIt.hasNext()) {
            GoBoardPosition smallPos = (GoBoardPosition)smallIt.next();
            boolean found = false;
            Iterator largeIt = largerGroup.iterator();
            while (largeIt.hasNext() && !found) {
                GoBoardPosition largePos = (GoBoardPosition)largeIt.next();
                if (largePos.getRow() == smallPos.getRow() && largePos.getCol() == smallPos.getCol())
                    found = true;
            }
            if (!found)
                return false;
        }
        return true;
    }

    private static final float MIN_THRESH = 0.2f;
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
        if (group.isOwnedByPlayer1() && groupHealth < MIN_THRESH) {
            groupHealth = MIN_THRESH;
        } else if (!group.isOwnedByPlayer1() && groupHealth > -MIN_THRESH) {
            groupHealth = -MIN_THRESH;
        }
        float stoneHealth = stone.getHealth();
        if (stone.isOwnedByPlayer1())  {
            assert (!group.isOwnedByPlayer1());
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
    public static Set findOccupiedNeighbors(List empties, GoBoard board)
    {
        Iterator it = empties.iterator();
        Set allNbrs = new HashSet();
        while (it.hasNext()) {
            GoBoardPosition empty = (GoBoardPosition)it.next();
            assert (empty.isUnoccupied());
            Set nbrs = board.getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
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


    public static Set findStringNeighbors(GoBoardPosition stone, GoBoard board ) {
        Set stringNbrs = new HashSet();
        List nobiNbrs = new LinkedList();
        board.pushStringNeighbors(stone, false, nobiNbrs, false);

        // add strings only once
        for (Object nn : nobiNbrs) {
            GoBoardPosition nbr = (GoBoardPosition)nn;
            stringNbrs.add(nbr.getString());
        }
        return stringNbrs;
    }


    static void printNobiNeighborsOf(GoBoardPosition stone, GoBoard board)
    {
        int row = stone.getRow();
        int col = stone.getCol();
        GameContext.log(0,  "Nobi Neigbors of "+stone+" are : " );
        if ( row > 1 )
            System.out.println( board.getPosition(row - 1, col ) );
        if ( row + 1 <= board.getNumRows() )
            System.out.println( board.getPosition(row + 1, col) );
        if ( col > 1 )
            System.out.println( board.getPosition(row, col-1) );
        if ( col + 1 <= board.getNumCols() )
            System.out.println( board.getPosition(row, col+1) );
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
            if (USE_RELATIVE_GROUP_SCORING)
                totalScore += group.getRelativeHealth();
            else
                totalScore += group.getAbsoluteHealth();
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