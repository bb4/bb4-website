package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.Iterator;


/**
 * Assert certain things are true about the board.
 * Helpful for debugging.
 * 
 * @author Barry Becker
 */
public class BoardValidator {

    private GoBoard board_;

    public BoardValidator(GoBoard board) {
        board_ = board;
    }

    /**
     * Confirm no empty strings, stones in valid groups, all stones in unique groups,
     * and all stones in groups claimed.
     * @param pos position to check
     */
    public void consistencyCheck(GoBoardPosition pos) {
        board_.getGroups().confirmNoEmptyStrings();
        confirmStonesInValidGroups();
        board_.getGroups().confirmAllStonesInUniqueGroups();
        try {
            confirmAllStonesInGroupsClaimed(board_.getGroups());
        } catch (AssertionError e) {
            System.err.println("The move was :" + pos);
            throw e;
        }
    }

    
    /**
     * verify that all the stones on the board are in the boards member list of groups.
     */
    public void confirmStonesInValidGroups()
    {
        GoGroupSet groups = board_.getGroups();
        for ( int i = 1; i <= board_.getNumRows(); i++ )  {
            for ( int j = 1; j <= board_.getNumCols(); j++ ) {
                GoBoardPosition space = (GoBoardPosition) board_.getPosition( i, j );
                if ( space.isOccupied() )
                    groups.confirmStoneInValidGroup(space);
            }
        }
    }


    /**
     * verify that all the stones are marked unvisited.
     */
    public void confirmAllUnvisited()
    {
        GoBoardPosition stone = areAllUnvisited();
        if (stone != null)
           assert false : stone + " is marked visited";
    }


    /**
     * verify that all the stones are marked unvisited.
     * @return positiont hat is still marked visited.
     */
    private GoBoardPosition areAllUnvisited()
    {
        for ( int i = 1; i <= board_.getNumRows(); i++ ) {
            for ( int j = 1; j <= board_.getNumCols(); j++ ) {
                GoBoardPosition stone = (GoBoardPosition) board_.getPosition( i, j );
                if (stone.isVisited())
                    return stone;
            }
        }
        return null;
    }


    /**
     * For every stone in every group specified in groups, verify that the group determined from using that stone as a seed
     * matches the group that is claims by ancestry.
     * (expensive to check)
     * @param groups we will check each stone in each of these groups.
     */
    public void confirmAllStonesInGroupsClaimed(GoGroupSet groups)
    {
        NeighborAnalyzer na = new NeighborAnalyzer(board_);
        for (GoGroup parentGroup : groups) {  // for each group on the board
            GoBoardPositionSet parentGroupStones = parentGroup.getStones();
            for (GoBoardPosition stone : parentGroupStones) {  // for each stone in that group
                // compute the group from this stone and confirm it matches the parent group
                GoBoardPositionList g = na.findGroupFromInitialPosition(stone);
                // perhaps we should do something more than check the size.
                if (g.size() != parentGroupStones.size()) {
                    groups.debugPrint(0, "Confirm stones in groups they Claim failed. \nGroups are:\n", true, true);
                    StringBuilder bldr = new StringBuilder();
                    bldr.append(board_.toString());
                    bldr.append("\n");
                    bldr.append("\n\nIt seems that using different seeds yields different groups:");
                    for (GoBoardPosition stone1 : parentGroupStones) {  // for each stone in that group
                         GoBoardPositionList gg = na.findGroupFromInitialPosition(stone);
                        bldr.append(gg.toString("\nSEED STONE = "+ stone1));
                    }
                    System.out.println(bldr.toString());
                    System.out.flush();
                    //assert false :
                    System.out.println(
                            g.toString("Calculated Group (seeded by ") + stone + "):"
                                + "\n is not equal to the expected parent group:\n" + parentGroup);
                    System.out.flush();
                }
            }
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