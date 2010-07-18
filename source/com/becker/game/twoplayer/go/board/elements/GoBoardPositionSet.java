package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.common.GameContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *  A set of GoStrings.
 *
 *  @author Barry Becker
 */
public class GoBoardPositionSet extends HashSet<GoBoardPosition>
{

    public GoBoardPositionSet() {}

    public GoBoardPositionSet(Set<GoBoardPosition> set) {
        super(set);
    }


    /**
     * pretty print this set of stones.
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
}