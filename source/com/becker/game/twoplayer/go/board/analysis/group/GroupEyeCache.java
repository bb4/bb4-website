package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.elements.GoGroup;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Keep a cache of the eyes in a group because its expensive to recompute them.
 * @author Barry Becker
 */
public class GroupEyeCache {

    /** The group we are maintaining eyes for. */
    private GoGroup group_;

    /**
     * need 2 true eyes to be unconditionally alive.
     * This is a set of GoEyes which give the spaces in the eye.
     * It includes eyes of all types including false eyes.
     * false-eye: any string of spaces or dead enemy stones for which one is a false eye.
     */
    private Set<GoEye> eyes_;

    /** measure of how easily the group can make 2 eyes. */
    private float eyePotential_;

    /**
     * Set this to true when the eyes need to be recalculated.
     * It must be set to true if the group has changed in any way.
      */
    private boolean isValid_ = false;


    /**
     * Constructor
     */
    public GroupEyeCache(GoGroup group) {
        group_ = group;
        eyes_ = new LinkedHashSet<GoEye>();
        isValid_ = false;
    }

    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board) {
        updateEyes(board);
        return eyes_;
    }

    /**
     * compute how many eyes (connected internal blank areas) this group has.
     * the eyes are either false eyes or true (or big or territorial) eyes.
     * Also update eyePotential (a measure of how good the groups ability to make 2 eyes(.
     * This method is expensive. That is why the 2 things it computes (eyes and eyePotential) are cached.
     * After this method runs, the cache is valid until something about the group changes.
     */
    public void updateEyes(GoBoard board) {
        if (isValid_ || board == null) {
            return;
        }

        EyeSpaceAnalyzer eyeAnalyzer = new EyeSpaceAnalyzer(group_, board);
        EyePotentialAnalyzer potentialAnalyzer = new EyePotentialAnalyzer(group_, board);
        eyes_ = eyeAnalyzer.determineEyes();
        eyePotential_ = potentialAnalyzer.calculateEyePotential();
        isValid_ = true;
    }

    public void invalidate() {
        clearEyes();
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean isValid() {
        return isValid_;
    }

    /** used only for test. */
    public float getEyePotential() {
        assert isValid_;
        return eyePotential_;
    }

    /**
     * Determine approximately how many eyes the group has.
     * This is purposely a little vague, but if more than 2.0, then must be unconditionally alive.
     * The value that we count for each type of eye could be optimized.
     * @return approximation to the number of eyes in group.
     */
    public float calcNumEyes() {
        float numEyes = 0;
        for (GoEye eye : eyes_) {
            numEyes += eye.getStatus().getScore(); //eye.getInformation().getEyeValue();
        }
        return numEyes;
    }

    /**
     * clear the current eyes for the group (in preparation for recomputing them).
     */
    private void clearEyes() {
        if (eyes_.isEmpty()) return;

        for (GoEye eye : eyes_) {
            eye.clear();
        }
        eyes_.clear();
        isValid_ = false;
    }

    /**
     * @return a deep copy of this instance
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();

        if (eyes_ !=null)  {
            ((GroupEyeCache)clone).eyes_ = new HashSet<GoEye>();
            Set<GoEye> m = ((GroupEyeCache)clone).eyes_;

            for (GoEye eye : this.eyes_) {
                m.add((GoEye) eye.clone());
            }
        }
        return clone;
    }
}