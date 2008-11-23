package com.becker.game.twoplayer.go;

import com.becker.game.common.*;

import java.util.*;

/**
 * static methods for determing properties of groups
 * Includes Benson's algoritm for unconditional life
 *
 * @author Barry Becker Date: Aug 28, 2005
 */
public final class GoGroupUtil {

    private GoGroupUtil() {}

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @return true if unconditionally alive
     */
    public static boolean isUnconditionallyAlive(GoGroup group, GoBoard board) {

        // mark all the strings in the group as not UA
        Set<GoString> candidateStrings = new HashSet<GoString>();
        for (GoString str : group.getMembers()) {
            str.setUnconditionallyAlive(true);
            candidateStrings.add(str);
        }
       
        findNeighborStringSets(group, board);

        // now create the neighbor eye sets for each qualified string
        for (GoEye eye : group.getEyes(board)) {          
            if (eye.getNeighbors() != null) {
                for (GoString str : eye.getNeighbors()) {                  
                    if (str.getNeighbors() == null) {
                        str.setNbrs(new HashSet<GoString>());
                    }
                    // only add the eye if every unoccupied position in the eye is adjacent to the str
                    if  (eye.allUnocupiedAdjacentToString(str, board)) {
                        str.getNeighbors().add(eye);
                    }
                }
            }
        }

        boolean done;
        do {
            done = true;
            for (GoEye eye : group.getEyes(board)) {
                eye.setUnconditionallyAlive(true);
                if (eye.getNeighbors() != null) {
                    for (GoString nbrStr : eye.getNeighbors()) {                     
                        if (!nbrStr.isUnconditionallyAlive()) {
                            eye.setUnconditionallyAlive(false);
                        }
                    }
                }
            }
            Iterator<GoString> it = candidateStrings.iterator();
            while (it.hasNext()) {
                GoString str = it.next();
                // find the number of ua eyes adjacent
                int numUAEyesAdjacent = 0;
                if (str.getNeighbors() != null) {
                    for (GoString eye : str.getNeighbors()) {                      
                        if (eye.isUnconditionallyAlive()) {
                            numUAEyesAdjacent++;
                        }
                    }
                }
                if (numUAEyesAdjacent < 2) {
                    str.setUnconditionallyAlive(false);
                    it.remove();
                    done = false; // something changed
                }
            }

        }  while ( !(done || candidateStrings.isEmpty()));

        // clear str nbrs
        for (Object s : group.getMembers()) {
            GoString str = (GoString)s;
            str.setNbrs(null);
        }

       return  !candidateStrings.isEmpty();
    }


    /**
     * first find the neighbor string sets for each true eye in the group.
     * @param group
     * @param board
     */
    private static void findNeighborStringSets(GoGroup group, GoBoard board) {
        
        for (GoEye eye : group.getEyes(board)) {         
            if (eye.getNeighbors() == null) {
                eye.setNbrs(new HashSet<GoString>());
            }
            for (Object point : eye.getMembers()) {
                GoBoardPosition pos = (GoBoardPosition) point;
                if (pos.isUnoccupied()) {
                    Set nbrs = board.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
                    for (Object n : nbrs) {
                        GoBoardPosition nbr = (GoBoardPosition) n;
                        if (nbr.getString().getGroup() != group) {
                            // this eye is not UA.
                            eye.setNbrs(null);
                            break;
                        }
                        else {
                            if (eye.getNeighbors() != null ) {
                                eye.getNeighbors().add(nbr.getString());
                                //candidateUAStrings.add(nbr.getString());
                            }
                        }
                    }
                }
            }
            GameContext.log(2, "num string nbrs of eyes = "
                    + ((eye.getNeighbors() == null)? 0 : eye.getNeighbors().size()));
        }
    }


    /** see if the group contains all the stones that are in the specified list (it may contain others as well)
     * @param stones list of stones to check if same as those in this group
     * @return true if all the strings are in this group
     */
    private static  boolean contains(GoGroup group, List stones )
    {
        Iterator it = stones.iterator();
        while ( it.hasNext() ) {
            GoString s = ((GoBoardPosition) it.next()).getString();
            if ( !group.getMembers().contains( s ) )
                return false;
        }
        return true;
    }

    /**
     * @param stones list of stones to check if same as those in this group
     * @return true if this group exacly contains the list of stones and no others
     */
    public static boolean exactlyContains(GoGroup group, List stones)
    {
        if ( !contains(group, stones ) )
            return false;
        // make sure that every stone in the group is also in the list.
        // that way we are assured that they are the same.
        Iterator sIt = group.getStones().iterator();
        while ( sIt.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) sIt.next();
            if ( !stones.contains( s ) )
                return false;
        }
        return true;
    }
}
