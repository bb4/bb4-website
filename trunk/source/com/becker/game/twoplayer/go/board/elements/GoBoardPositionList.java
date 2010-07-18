package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;

import java.util.*;

/**
 *  A list of GoStrings.
 *
 *  @author Barry Becker
 */
public class GoBoardPositionList extends ArrayList<GoBoardPosition>
{

    /**
     * Default construcotr.
     */
    public GoBoardPositionList() {}

    /**
     * @param initialCapacity initial size.
     */
    public GoBoardPositionList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * copy constructor.
     *
     * @param positionList list to initialize with
     */
    public  GoBoardPositionList(GoBoardPositionList positionList) {
        super(positionList);
    }


    /**
     * pretty print this list.
     */
    public void debugPrint( int logLevel, String title)
    {
       GameContext.log(logLevel, this.toString(title));
    }


    /**
     * pretty print a list of all the current groups (and the strings they contain)
     * @return string form of list of stones.
     */
    public String toString(String title)
    {
        StringBuffer buf = new StringBuffer(title);
        buf.append("\n  ");
        for (GoBoardPosition stone : this) {
            buf.append(stone.toString()).append(", ");
        }
        return buf.substring(0, buf.length() - 2);
    }



    // --- internal consistency checks ----

    /**
     * Verify all stones in this list are marked unvsited.
     */
    public void confirmUnvisited()
    {
        for (GoBoardPosition pos : this) {
            assert !pos.isVisited() : pos + " in " + this + " was visited";
        }
    }

    /**
     * Verify no duplicate positions in this list
     * @param seed stone to start checking from .
     */
    public void confirmNoDupes( GoBoardPosition seed)
    {
        Object[] stoneArray = this.toArray();

        for ( int i = 0; i < stoneArray.length; i++ ) {
            GoBoardPosition st = (GoBoardPosition) stoneArray[i];
            // make sure that this stone is not a dupe of another in the list
            for ( int j = i + 1; j < stoneArray.length; j++ ) {
                assert (!st.equals(stoneArray[j])): "found a dupe=" + st + " in " + this + "]n the seed = " + seed ;
            }
        }
    }

    /**
     * Confirm that this list contains some smaller list
     * @param smallerGroup
     * @return true if larger group contains smaller group.
     */
    public boolean confirmStoneListContains(GoBoardPositionList smallerGroup)
    {
        for (GoBoardPosition smallPos : smallerGroup) {
            boolean found = false;
            Iterator largeIt = this.iterator();
            while (largeIt.hasNext() && !found) {
                GoBoardPosition largePos = (GoBoardPosition) largeIt.next();
                if (largePos.getRow() == smallPos.getRow() && largePos.getCol() == smallPos.getCol())
                    found = true;
            }
            if (!found)
                return false;
        }
        return true;
    }
}