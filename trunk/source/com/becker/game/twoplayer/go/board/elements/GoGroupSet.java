package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;

import java.util.*;

/**
 *  A set of GoGroups.
 *
 *  @author Barry Becker
 */
public class GoGroupSet implements Set<GoGroup>
{
    private final Set<GoGroup> groups;

    /**
     * Default constructor creates unsynchronized version.
     */
    public GoGroupSet() {
        this(false);
    }

    /**
     * Constructor
     * @param synchronize  if true, then create synchronized set.
     */
    private GoGroupSet(boolean synchronize) {
        groups = synchronize? Collections.synchronizedSet(new LinkedHashSet<GoGroup>()) :
                              new LinkedHashSet<GoGroup>();
    }

    /**
     * Copy constructor.
     * @param groups to add initially.
     */
    public GoGroupSet(GoGroupSet groups) {
        this(false);
        addAll(groups);
    }
    
    public int size() {
        return groups.size();
    }

    public boolean isEmpty() {
        return groups.isEmpty();
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

    public boolean contains(Object o) {
        return groups.contains(o);
    }

    public Iterator<GoGroup> iterator() {
        return groups.iterator();
    }

    public Object[] toArray() {
        return groups.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return groups.toArray(a);
    }

    public boolean add(GoGroup group) {
        return groups.add(group);
    }

    public boolean remove(Object o) {
        return groups.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return groups.containsAll(c);
    }

    public synchronized boolean addAll(Collection<? extends GoGroup> c) {
        return groups.addAll(c);
    }

    /** Intentionally not implemented for thread safety reasons.*/
    public boolean retainAll(Collection<?> c) {
        assert false : "unsafe";
        return false;
    }

    /** Intentionally not implemented for thread safety reasons.*/
    public boolean removeAll(Collection<?> c) {
        assert false : "unsafe";
        return false;
    }

    /** Intentionally not implemented for thread safety reasons.*/
    public void clear() {
        assert false : "unsafe";
    }


    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public String toString()
    {
        return toString(true, true);
    }


    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    String toString(boolean showBlack, boolean showWhite)
    {
        StringBuffer groupText = new StringBuffer( "" );
        StringBuffer blackGroupsText = new StringBuffer(showBlack? "The black groups are :\n" : "" );
        StringBuffer whiteGroupsText =
                new StringBuffer((showBlack?"\n":"") + (showWhite? "The white groups are :\n" : ""));

        for (Object group1 : groups) {
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
    void debugPrint( int logLevel)
    {
        debugPrint( logLevel,  "---The groups are:", true, true);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public void debugPrint(int logLevel, String title, boolean showBlack, boolean showWhite)
    {
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
    public void confirmAllStonesInUniqueGroups()
    {
        for (GoGroup g : this) {
            confirmStonesInOneGroup(g);
        }
    }


    /**
     * confirm that the stones in this group are not contained in any other group.
     */
    public void confirmStonesInOneGroup( GoGroup group)
    {
        for (GoString string : group.getMembers()) {
            for (GoGroup g : this) {  // for each group on the board

                if (!g.equals(group)) {
                    for (GoString s : g.getMembers()) {   // fro each string in that group
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


    public void confirmNoEmptyStrings()
    {
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
    public void confirmStoneInValidGroup(GoBoardPosition stone)
    {
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
                    g + " \nThe valid groups are:" + groups;
        }
    }

    private void confirmStoneInStringAlsoInGroup(GoString str, GoGroup group) {
        //make sure that every stone in the string belongs in this group
        for (GoBoardPosition pos : str.getMembers()) {

            if (pos.getGroup() != null && !group.equals(pos.getGroup())) {
                this.debugPrint(0, "Confirm stones in one group failed. Groups are:", true, true);
                assert false : pos + " does not just belong to " + pos.getGroup()
                              + " as its ancestry indicates. It also belongs to " + group;
            }
        }
    }

}