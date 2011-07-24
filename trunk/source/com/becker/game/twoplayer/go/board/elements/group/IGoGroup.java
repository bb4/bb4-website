package com.becker.game.twoplayer.go.board.elements.group;

import com.becker.common.geometry.Box;
import com.becker.game.twoplayer.go.board.elements.IGoSet;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.string.GoStringSet;
import com.becker.game.twoplayer.go.board.elements.string.IGoString;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoGroup class.
 *
 * @author Barry Becker
 */
public interface IGoGroup extends IGoSet {

    void addMember(IGoString string);

    GoStringSet getMembers();

    boolean isOwnedByPlayer1();

    void addChangeListener(GroupChangeListener listener);

    int getNumStones();

    boolean containsStone(GoBoardPosition stone);

    void remove(IGoString string);

    GoBoardPositionSet getStones();

    void updateTerritory( float health );

    Box findBoundingBox();

    String toHtml();
}