package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

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

    /**
     * @return true if pos is our enemy.
     */
    boolean isEnemy(GoBoardPosition pos);

    /**
     * @return true if owned by player 1 (i.e. black).
     */
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

    /** @return the number of liberties. */
    int getNumLiberties(GoBoard board);
}