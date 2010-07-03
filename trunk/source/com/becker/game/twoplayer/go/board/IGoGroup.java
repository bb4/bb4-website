package com.becker.game.twoplayer.go.board;

import java.util.Set;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoGroup class.
 *
 * @author Barry Becker
 */
public interface IGoGroup {

    float getAbsoluteHealth();

    //float getRelativeHealth();

    boolean isOwnedByPlayer1();

    int getNumStones();
}