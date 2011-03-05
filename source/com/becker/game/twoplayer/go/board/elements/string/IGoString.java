package com.becker.game.twoplayer.go.board.elements.string;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.IGoSet;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoString or GoEye class.
 *
 * @author Barry Becker
 */
public interface IGoString extends IGoSet {

    /**
     * @return  set of member positions.
     */
    GoBoardPositionSet getMembers();

    /**
     * @return  the group that this string belongs to.
     */
    IGoGroup getGroup();

    boolean contains(GoBoardPosition pos);

    boolean isUnconditionallyAlive();

    void setUnconditionallyAlive(boolean unconditionallyAlive);

    void remove( GoBoardPosition stone, GoBoard board);

    void updateTerritory( float health );

    void setGroup(IGoGroup group);
}
