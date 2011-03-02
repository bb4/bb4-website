package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.twoplayer.go.board.GoBoard;

import java.util.Set;

/**
 *  A GoEye is composed of a strongly connected set of empty spaces (and possible some dead enemy stones).
 *
 *  @author Barry Becker
 */
public interface IGoSet extends IGoMember {

    /**
     * @return the hashSet containing the members
     */
    Set<? extends IGoMember> getMembers();

    boolean isEnemy(GoBoardPosition pos);

    boolean isOwnedByPlayer1();

    /**
     * Mark all members unvisited.
     * @param visited whether or not the string members should be marked visited or unvisited.
     */
    void setVisited(boolean visited);

    /**
     * @return the number of elements in the string
     */
    int size();

    /**
     * Get the number of liberties (open surrounding spaces)
     * @param board go board
     * @return the liberties/positions for the set.
     */
    GoBoardPositionSet getLiberties(GoBoard board);

    int getNumLiberties(GoBoard board);
}