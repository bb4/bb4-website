package com.becker.game.twoplayer.go.board;

import java.util.*;

/**
 *  A set of GoGroups.
 *
 *  @author Barry Becker
 */
public class GoGroupSet implements Set<GoGroup>
{
    Set<GoGroup> groups;

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
    public GoGroupSet(boolean synchronize) {
        groups = synchronize? Collections.synchronizedSet(new LinkedHashSet<GoGroup>()) :
                              new LinkedHashSet<GoGroup>();
    }


    public int size() {
        return groups.size();
    }

    public boolean isEmpty() {
        return groups.isEmpty();
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

    public boolean addAll(Collection<? extends GoGroup> c) {
        return groups.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return groups.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return groups.removeAll(c);
    }

    public void clear() {
        groups.clear();
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
                            BoardDebugUtil.debugPrintGroups(0, this);
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


    private void confirmStoneInStringAlsoInGroup(GoString str, GoGroup group) {
        //make sure that every stone in the string belongs in this group
        for (GoBoardPosition pos : str.getMembers()) {

            if (pos.getGroup() != null && !group.equals(pos.getGroup())) {
                BoardDebugUtil.debugPrintGroups(0, "Confirm stones in one group failed. Groups are:", true, true, this);
                assert false : pos + " does not just belong to " + pos.getGroup()
                              + " as its ancestry indicates. It also belongs to " + group;
            }
        }
    }

}