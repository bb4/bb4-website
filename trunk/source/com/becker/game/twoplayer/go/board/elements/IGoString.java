package com.becker.game.twoplayer.go.board.elements;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoString or GoEye class.
 *
 * @author Barry Becker
 */
public interface IGoString {

    /**
     * @return  set of member positions.
     */
    GoBoardPositionSet getMembers();


    /**
     * @return  the group that this string belongs to.
     */
    GoGroup getGroup();

    /**
     * Mark all members unvisted.
     */
    void unvisit();
}
