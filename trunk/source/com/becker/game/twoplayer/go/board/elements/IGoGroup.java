package com.becker.game.twoplayer.go.board.elements;

import com.becker.common.Box;
import com.becker.game.twoplayer.go.board.GoBoard;

import java.util.List;
import java.util.Set;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoGroup class.
 *
 * @author Barry Becker
 */
public interface IGoGroup extends IGoSet {

    void addMember(GoString string);

    GoStringSet getMembers();

    float getAbsoluteHealth();

    boolean isOwnedByPlayer1();

    int getNumStones();

    GoEyeSet getEyes(GoBoard board);

    float getRelativeHealth(GoBoard board, boolean useCachedValue);

    boolean containsStone(GoBoardPosition stone);

    void remove(IGoString string);

    GoBoardPositionSet getStones();

    float calculateAbsoluteHealth( GoBoard board);

    float calculateRelativeHealth( GoBoard board);

    void updateTerritory( float health );

    Box findBoundingBox();

    String toHtml();
}