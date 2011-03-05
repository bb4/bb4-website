package com.becker.game.twoplayer.go.board.elements.string;

import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;

import java.util.LinkedHashSet;

/**
 *  A set of GoStrings.
 *
 *  @author Barry Becker
 */
public class GoStringSet extends LinkedHashSet<IGoString> {

    public GoStringSet() {}
    
    /**
     * Copy constructor.
     * @param set
     */
    public GoStringSet(GoStringSet set) {
        super(set);
    }

    /**
     *
     * @param pos
     * @return the string that contains pos if any. Null if none.
     */
    public IGoString findStringContainingPosition(GoBoardPosition pos) {
        for (IGoString str : this) {
            if (str.contains(pos)) {
                return str;
            }
        }
        return null;
    }
}