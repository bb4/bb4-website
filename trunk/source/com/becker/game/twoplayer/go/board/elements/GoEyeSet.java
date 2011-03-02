package com.becker.game.twoplayer.go.board.elements;

import java.util.LinkedHashSet;

/**
 *  A set of GoEyes.
 *
 *  @author Barry Becker
 */
public class GoEyeSet extends LinkedHashSet<IGoEye> {

    public GoEyeSet() {}

    /**
     * Copy constructor.
     * @param set
     */
    public GoEyeSet(GoEyeSet set) {
        super(set);
    }
}