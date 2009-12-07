package com.becker.game.twoplayer.go.board;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Assert certain things are true about the board.
 * Helpful for debugging.
 * 
 * @author Barry Becker
 */
public class BoardValidationUtil {
    
    private BoardValidationUtil() {}
        

    /**
     * 
     * @param seed
     * @param list
     */
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


    /**
     * 
     * @param stones
     */
    public static void confirmUnvisited( List stones )
    {
        Iterator it = stones.iterator();
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            assert !p.isVisited(): p+" in "+stones+" was visited";
        }
    }

    
    /**
     * verify that all the stones on the board are in the boards member list of groups.
     */
    public static void confirmStonesInValidGroups(GoBoard board)
    {
        Set groups = board.getGroups();
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
            BoardDebugUtil.debugPrintGroups( 0, "Confirm stones in valid groups failed. The groups are:",
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
                            BoardDebugUtil.debugPrintGroups( 0, groups );
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
                BoardDebugUtil.debugPrintGroups( 0, "Confirm stones in one group failed. Groups are:", true, true, groups );
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
                 List<GoBoardPosition> g = board.findGroupFromInitialPosition(s);
                 // perhaps we should do something more than check the size.
                if (g.size() != parentGroupStones.size())   {
                    BoardDebugUtil.debugPrintGroups( 0, "Confirm stones in groups they Claim failed. Groups are:", true, true, groups );
                    assert false :
                      BoardDebugUtil.debugPrintListText(0,"Calculated Group (seeded by "+s+"):", g) +"\n is not equal to the expected parent group:\n"+parentGroup;
                }
            }
        }
    }

    public static void confirmNoEmptyStrings(Set groups)
    {
        for (Object g : groups)  {
            for (Object s : ((GoGroup)g).getMembers()) {
                GoString string = (GoString) s;
                assert (string.size() > 0): "There is an empty string in " + string.getGroup();
            }
        }
    }

    static void confirmNoStringsWithEmpties(Set<GoGroup> groups)
    {
        for (GoGroup g : groups)  {
            for (GoString s : g.getMembers()) {
                GoString string = s;
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
            confirmValidStrings(group, board);
        }
    }
    
    
    /**
     * 
     * @param largerGroup
     * @param smallerGroup
     * @return
     */
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
     * go through the groups strings and verify that they are valid (have all nobi connections)
     */
    private static void confirmValidStrings(GoGroup group, GoBoard b )
    {
        Iterator it = group.getMembers().iterator();
        while ( it.hasNext() ) {
            GoString string = (GoString) it.next();
            string.confirmValid( b );
        }
    }

    public static void confirmNoNullMembers(GoGroup group)
    {
        Iterator it = group.getStones().iterator();
        boolean failed = false;
        while (it.hasNext()) {
            GoBoardPosition s = (GoBoardPosition)it.next();
            if (s.getPiece()==null) failed = true;
        }
        if (failed) {
            assert false : "Group contains an empty position: "+group.toString();
        }
    }

}
