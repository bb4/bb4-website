package com.becker.game.twoplayer.go.board;

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
}