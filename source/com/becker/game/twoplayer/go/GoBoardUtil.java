package com.becker.game.twoplayer.go;


import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.Board;
import com.becker.game.common.Box;
import com.becker.common.Assert;

import java.util.*;


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
     * @@ make this a game parameter .9 - 1.8 that can be optimized.
     */
    static final float DIFFERENCE_THRESHOLD = .8f;

    private GoBoardUtil() {
    }

    /**
     * Get an adjacent neighbor stone restricted to the desired type.
     *
     * @param nbrStone   the neighbor to check
     * @param friendOwnedByP1  type of the center stone (can't use center.owner since center may be unnoccupied)
     * @param nbrs  hashset of the ngbors matching the criteria.
     * @param neighborType  one of NEIGHBOR_ANY, NEIGHBOR_ENEMY_ONLY, or NEIGHBOR_FRIENDLY_ONLY
     */
    static void getNobiNeighbor( GoBoardPosition nbrStone, boolean friendOwnedByP1, Set nbrs, NeighborType neighborType )
    {
        if (nbrStone.isUnoccupied()) return;

        boolean correctNeighborType = true;
        switch (neighborType) {
            case ANY:
                correctNeighborType = true;
                break;
            case OCCUPIED:
                correctNeighborType = nbrStone.getPiece()!=null;
                break;
            case ENEMY: // the opposite color
                GoStone st = (GoStone)nbrStone.getPiece();
                correctNeighborType = st.isOwnedByPlayer1() != friendOwnedByP1;
                break;
            case FRIEND: // the same color
                correctNeighborType = (nbrStone.getPiece().isOwnedByPlayer1() == friendOwnedByP1);
                break;
            default : assert false: "unknown or unsupported neighbor type:"+neighborType;
        }
        if (correctNeighborType ) {
            // might happen if the stone belongs to an eye instead of a string.
            assert  nbrStone.getString() != null: "this stone does not belong to a string:" + nbrStone;
            nbrs.add( nbrStone );
        }
    }

    /**
     * @param positions to find bounding box of
     * @return boundin box of set of stones/positions passed in
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
            unvisitPositionsInList( (List) it.next() );
    }

    /**
     * set the visited flag back to false for a set of stones
     */
    public static void unvisitPositionsInList( List positions )
    {
        Iterator it = positions.iterator();
        // return the stone to the unvisited state
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            s.setVisited( false );
        }
    }


    public static void unvisitAll(Board board)
    {
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                GoBoardPosition pos = (GoBoardPosition) board.getPosition( i, j );
                pos.setVisited(false);
            }
        }
    }

    static List getVisitedSpaces(GoBoard board)
    {
        List list = new ArrayList(10);
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                GoBoardPosition stone = (GoBoardPosition) board.getPosition( i, j );
                if (stone.isVisited())
                    list.add(stone);
            }
        }
         return list;
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
        return buf.substring(0, buf.length()-2).toString();
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
    private static String getGroupsText(boolean showBlack, boolean showWhite, Set groups)
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
        Assert.notNull( str, stone + " does not belong to any string!") ;
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
            Assert.exception(
                   "Error: This " + stone + " does not belong to a valid group: " + g + " \nThe valid groups are:" + groups);
        }
    }



    /**
     * verify that all the stones are marked unvisited.
     */
    public static void confirmAllUnvisited(GoBoard board)
    {
        GoBoardPosition stone = areAllUnvisited(board);
        if (stone != null)
           Assert.exception(stone + " is marked visited" );
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
                        //make sure that every stone in the string belongs in this group
                        Iterator stoneIt = s.getMembers().iterator();
                        while ( stoneIt.hasNext() ) {
                            GoBoardPosition st1 = (GoBoardPosition) stoneIt.next();
                            if ( !g.equals(st1.getGroup()) ) {
                                debugPrintGroups( 0, "Confirm stones in one group failed. Groups are:", true, true, groups );
                                assert false:
                                       st1 + " does not just belong to " + st1.getGroup()
                                        + " as its ancestry indicates. It also belongs to " + g;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * For every stone in every group verify that the group determined from using that stone as a seed
     * matches the group that is claims by ancestry.
     * (expesnsive to check)
     */
    public static void confirmAllStonesAreInGroupsTheyClaim(Set groups, GoBoard board)
    {
        Iterator grIt = groups.iterator();
        while ( grIt.hasNext() ) {  // for each group on the board
            GoGroup parentGroup = (GoGroup) grIt.next();
            // for eash stone in that group
            List parentGroupStones = parentGroup.getStones();
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
            group.confirmValidStrings( board );
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

    /**
     * @param group
     * @param stone
     * @param threshold
     * @return return true of the stone is greater than threshold weaker than the group.
     */
    static boolean isStoneWeaker(GoGroup group, GoStone stone, float threshold)
    {
        float groupHealth = group.getAbsoluteHealth();
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
     * @return return true of the stone is greater than threshold weaker than the group.
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
}