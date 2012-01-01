package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;

import java.util.*;

/**
 * A set of GoGroups.
 *
 * @author Barry Becker
 */
public class GoGroupSet extends LinkedHashSet<GoGroup> {

    /**
     * Default constructor.
     */
    public GoGroupSet() {
    }

    /**
     * Copy constructor.
     * @param groups to add initially.
     */
    public GoGroupSet(GoGroupSet groups) {
        addAll(groups);
    }


    /**
     * Check all the groups of the same color to see if the stone is already in one of them
     * @param pos position on the board to check
     * @return true if the specified position is in one of our groups.
     */
    public boolean containsPosition(GoBoardPosition pos) {
        // if there is no stone in the position, then it cannot be part of a group
        if (!pos.isOccupied())
            return false;

        for (GoGroup group : this) {
            if (group.isOwnedByPlayer1() == pos.getPiece().isOwnedByPlayer1() && group.containsStone(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public String toString() {
        return toString(true, true);
    }


    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    String toString(boolean showBlack, boolean showWhite) {

        StringBuilder groupText = new StringBuilder( "" );
        StringBuilder blackGroupsText = new StringBuilder(showBlack? "The black groups are :\n" : "" );
        StringBuilder whiteGroupsText =
                new StringBuilder((showBlack?"\n":"") + (showWhite? "The white groups are :\n" : ""));

        for (Object group1 : this) {
            GoGroup group = (GoGroup) group1;
            if (group.isOwnedByPlayer1() && (showBlack)) {
                //blackGroupsText.append( "black group owner ="+ group.isOwnedByPlayer1());
                blackGroupsText.append(group);
            } else if (!group.isOwnedByPlayer1() && showWhite) {
                //whiteGroupsText.append( "white group owner ="+ group.isOwnedByPlayer1());
                whiteGroupsText.append(group);
            }
        }
        groupText.append( blackGroupsText );
        groupText.append( whiteGroupsText );

        return groupText.toString();
    }


    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    void debugPrint( int logLevel) {
        debugPrint( logLevel,  "---The groups are:", true, true);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public void debugPrint(int logLevel, String title, boolean showBlack, boolean showWhite) {
        if (logLevel <= GameContext.getDebugMode())  {
            GameContext.log( logLevel, title );
            GameContext.log( logLevel, this.toString(showBlack, showWhite));
            GameContext.log( logLevel, "----" );
        }
    }


    // --- methods for ensuring internal consistency ----

    /**
     * for every stone one the board verify that it belongs to exactly one group
     */
    public void confirmAllStonesInUniqueGroups() {
        for (GoGroup g : this) {
            confirmStonesInOneGroup(g);
        }
    }


    /**
     * confirm that the stones in this group are not contained in any other group.
     */
    public void confirmStonesInOneGroup( GoGroup group) {
        for (GoString string : group.getMembers()) {
            for (GoGroup g : this) {  // for each group on the board

                if (!g.equals(group)) {
                    for (GoString s : g.getMembers()) {   // for each string in that group
                        if (string.equals(s)) {
                            debugPrint(0);
                            assert false : "ERROR: " + s + " contained by 2 groups";
                        }
                        confirmStoneInStringAlsoInGroup(s, g);
                    }
                }
            }
        }
    }

    /**
     * verify that we contain no empty strings.
     */
    public void confirmNoEmptyStrings() {
        for (Object g : this)  {
            for (Object s : ((GoSet) g).getMembers()) {
                GoString string = (GoString) s;
                assert (string.size() > 0): "There is an empty string in " + string.getGroup();
            }
        }                  
    }

    /**
     * @param stone verify that this stone has a valid string and a group in the board's member list.
     */
    public void confirmStoneInValidGroup(GoBoardPosition stone) {
        GoString str = stone.getString();
        assert ( str!=null) : stone + " does not belong to any string!" ;
        GoGroup g = str.getGroup();
        boolean valid = false;
        Iterator gIt = this.iterator();
        GoGroup g1;
        while ( !valid && gIt.hasNext() ) {
            g1 = (GoGroup) gIt.next();
            valid = g.equals(g1);
        }
        if ( !valid ) {
            this.debugPrint( 0, "Confirm stones in valid groups failed. The groups are:",
                    g.isOwnedByPlayer1(), !g.isOwnedByPlayer1());
            assert false :
                   "Error: This " + stone + " does not belong to a valid group: " +
                    g + " \nThe valid groups are:" + this;
        }
    }

    /**
     * Make sure that every stone in the string belongs in this group
     * @param str every stone in this string should also be in the specified group
     * @param group group that contains all the stones in the string.
     */
    private void confirmStoneInStringAlsoInGroup(GoString str, GoGroup group) {

        for (GoBoardPosition pos : str.getMembers()) {

            if (pos.getGroup() != null && !group.equals(pos.getGroup())) {
                this.debugPrint(0, "Confirm stones in one group failed. Groups are:", true, true);
                assert false : pos + " does not just belong to " + pos.getGroup()
                              + " as its ancestry indicates. It also belongs to " + group;
            }
        }
    }

}