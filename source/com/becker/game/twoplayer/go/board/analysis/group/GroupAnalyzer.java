package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.elements.GoGroup;

import java.util.Set;

/**
 * Analyzes a group to determine how alive it is, and also find other properties like eyes and liberties.
 *
 * @author Barry Becker
 */
public class GroupAnalyzer {

    /** The group of go stones that we are analyzing. */
    private GoGroup group_;

    /**
     * This measure of health is also between -1 and 1 but it should be more
     * accurate because it takes into account the health of neighboring enemy groups as well.
     * it uses the absolute health as a base and exaggerates it base on the relative strength of the
     * weakest enemy nbr group.
     */
    private float relativeHealth_;

    private AbsoluteHealthCalculator absHealthCalculator_;

    /** cached absolute health to avoid needless recalculation. */
    private float absoluteHealth_;

    /**
     * Constructor.
     * @param group group to analyze.
     */
    public GroupAnalyzer(GoGroup group) {
        group_ = group;
        absHealthCalculator_ = new AbsoluteHealthCalculator(group);
    }

    /**
     * @return health score independent of neighboring groups.
     */
    public float getAbsoluteHealth() {
        //if (!isValid()) {
        //    assert false :  "Getting stale absolute health = " + absoluteHealth_;
        //}
        return absoluteHealth_;
    }

    /**
     * @return health score dependent on strength of neighboring groups.
     */
    public float getRelativeHealth() {
        return relativeHealth_;
    }

    public void invalidate() {
        absHealthCalculator_.invalidate();
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean isValid()
    {
        return absHealthCalculator_.isValid();
    }

    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    public GoBoardPositionSet getLiberties(GoBoard board) {
        return absHealthCalculator_.getLiberties(board);
    }

    /**
     * If nothing cached, this may not be accurate.
     * @return number of cached liberties.
     */
    public int getNumLiberties(GoBoard board) {
        return absHealthCalculator_.getNumLiberties(board);
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones() {
       return absHealthCalculator_.getNumStones();
    }

    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board) {
        return absHealthCalculator_.getEyes(board);
    }

    public float calculateAbsoluteHealth(GoBoard board) {
        absoluteHealth_ = absHealthCalculator_.calculateAbsoluteHealth(board);
        return absoluteHealth_;
    }

    /**
     * used only for test. Remove when tested thru AbsoluteGroupHealthCalc
     * @return eye potential
     */
    public float getEyePotential() {
        return absHealthCalculator_.getEyePotential();
    }

    /**
     * Calculate the relative health of a group.
     * This method must be called only after calculateAbsoluteHealth has be done for all groups.
     * Good health is positive for a black group.
     * This measure of the group's health should be much more accurate than the absolute health
     * because it takes into account the relative health of neighboring groups.
     * If the health of an opponent bordering group is in worse shape
     * than our own then we get a boost since we can probably kill that group first.
     *
     * @return the overall health of the group.
     */
    public float calculateRelativeHealth(GoBoard board) {
        if (!isValid()) {
            calculateAbsoluteHealth(board);
        }

        RelativeHealthCalculator relativeCalculator = new RelativeHealthCalculator(group_);
        relativeHealth_ = relativeCalculator.calculateRelativeHealth(board, absoluteHealth_);

        return relativeHealth_;
    }

    /**
     * @return a deep copy of this GroupAnalyzer
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        ((GroupAnalyzer)clone).absHealthCalculator_ = new AbsoluteHealthCalculator(group_);
        return clone;
    }
}
