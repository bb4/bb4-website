package com.becker.game.twoplayer.go.board;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

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