package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;

/**
 *  A GoEye is composed of a strongly connected set of empty spaces (and possible some dead enemy stones).
 *
 *  @author Barry Becker
 */
public interface IGoEye extends IGoSet {

    EyeInformation getInformation();

    EyeStatus getStatus();

    String getEyeTypeName();

    int getNumCornerPoints();

    int getNumEdgePoints();

    /**
     * @return  the group that this string belongs to.
     */
    IGoGroup getGroup();

    /**
     * @return the hashSet containing the members
     */
    GoBoardPositionSet getMembers();

    /**
     * empty the positions from the eye.
     */
    void clear();

    /**
     * @return true if unconditionally alive.
     */
    boolean isUnconditionallyAlive();

    void setUnconditionallyAlive(boolean unconditionallyAlive);
}