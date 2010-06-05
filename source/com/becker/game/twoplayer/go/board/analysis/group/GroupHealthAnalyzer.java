package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.*;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameProfiler;

import java.util.Set;

/**
 * Analyzes a group to determine how alive it is.
 *
 * @author Barry Becker
 */
public class GroupHealthAnalyzer implements Cloneable {

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
    private float absoluteHealth_;

    /**
     * Constructor.
     * @param group group to analyze.
     */
    public GroupHealthAnalyzer(GoGroup group) {
        group_ = group;
        absHealthCalculator_ = new AbsoluteHealthCalculator(group);
    }

    /**
     * only used in tester. otherwise would be private.
     * @return health score independent of neighboring groups.
     */
    public float getAbsoluteHealth()
    {
        //assert !eyeCacheBroken_;
        return absoluteHealth_;
    }

    public void breakEyeCache() {
        absHealthCalculator_.breakEyeCache();
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean hasChanged()
    {
        return absHealthCalculator_.hasChanged();
    }

    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    public Set<GoBoardPosition> getLiberties(GoBoard board)
    {
        return absHealthCalculator_.getLiberties(board);
    }

    /**
     * If nothing cached, this may not be accurate.
     * @return number of cached liberties.
     */
    public int getNumLiberties() {
        return absHealthCalculator_.getNumLiberties();
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones()
    {
       return absHealthCalculator_.getNumStones();
    }

    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board)
    {
        return absHealthCalculator_.getEyes(board);
    }

    public float calculateAbsoluteHealth(GoBoard board, GameProfiler profiler) {
        absoluteHealth_ = absHealthCalculator_.calculateAbsoluteHealth(board, profiler);
        return absoluteHealth_;
    }

    /** used only for test. Remove when tested thru AbsoluteGroupHealthCalc */
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
    public float calculateRelativeHealth(GoBoard board, GoProfiler profiler )
    {
        RelativeHealthCalculator relativeCalculator = new RelativeHealthCalculator(group_);
        relativeHealth_ = relativeCalculator.calculateRelativeHealth(board, profiler, absoluteHealth_);

        return relativeHealth_;
    }


    public float getRelativeHealth()
    {
        if (hasChanged()) {
            GameContext.log(0, "Getting stale relative health = " + relativeHealth_);
        }
        return relativeHealth_;
    }

    /**
     * @return a deep copy of this GroupHealthAnalyzer
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();
        ((GroupHealthAnalyzer)clone).absHealthCalculator_ = new AbsoluteHealthCalculator(group_);
        return clone;
    }
}
