package com.becker.game.twoplayer.go.board.elements;

import java.util.LinkedHashSet;

/**
 *  A set of GoStrings.
 *
 *  @author Barry Becker
 */
public class GoStringSet extends LinkedHashSet<GoString>
{

    public GoStringSet() {}
    
    /**
     * Copy constructor.
     * @param set
     */
    public GoStringSet(GoStringSet set) {
        super(set);
    }
}