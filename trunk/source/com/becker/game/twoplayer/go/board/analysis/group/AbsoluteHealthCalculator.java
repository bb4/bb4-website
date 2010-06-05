package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameProfiler;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.GoString;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 * @author Barry Becker
 */
public class AbsoluteHealthCalculator {

    /** The group of go stones that we are analyzing. */
    private GoGroup group_;

    /**
     * This is a number between -1 and 1 that indicates how likely the group is to live
     * independent of the health of the stones around it.
     * all kinds of factors can contribute to the health of a group.
     * Local search should be used to make this as accurate as possible.
     * If the health is 1.0 then the group has at least 2 eyes and is unconditionally alive.
     * If the health is -1.0 then there is no way to save the group even if you could
     * play 2 times in a row.
     * Unconditional life means the group cannot be killed no matter how many times the opponent plays.
     * A score of near 0 indicates it is very uncertain whether the group will live or die.
     */
    private float absoluteHealth_ = 0;

    /**
     * need 2 true eyes to be unconditionally alive.
     * This is a set of GoEyes which give the spaces in the eye.
     * It includes eyes of all types including false eyes.
     * false-eye: any string of spaces or dead enemy stones for which one is a false eye.
     */
    private Set<GoEye> eyes_;

    /** measure of how easily the group can make 2 eyes. */
    private float eyePotential_;


    /** Number of stones in the group. */
    private int cachedNumStonesInGroup_;

    /**
     * Set this to true when the eyes need to be recalculated.
     * It must be set to true if the group has changed in any way.
      */
    private boolean eyeCacheBroken_ = true;

    /**
     * This is the cached number of liberties.
     * It updates whenever something has changed.
     */
    private Set<GoBoardPosition> cachedLiberties_;


    /**
     * Constructor
     * @param group the group to analyze
     */
    public AbsoluteHealthCalculator(GoGroup group) {
        group_ = group;
        eyes_ = new LinkedHashSet<GoEye>();
        eyeCacheBroken_ = true;
    }

    /**
     * Calculate the absolute health of a group.
     * All the stones in the group have the same health rating because the
     * group lives or dies as a unit
     * (not entirely true - strings live or die as unit, but there is a relationship).
     * Good health of a black group is positive; white, negative.
     * The health is a function of the number of eyes, false-eyes, liberties, and
     * the health of surrounding groups. If the health of an opponent bordering group
     * is in worse shape than our own then we get a boost since we can probably
     * kill that group first. See calculateRelativeHealth below.
     * A perfect 1 (or -1) indicates unconditional life (or death).
     * This means that the group cannot be killed (or given life) no matter
     * how many times the opponent plays (see Dave Benson 1977).
     *http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @@ need expert advice to make this work well.
     * @@ make the constants parameters and optimize them.
     * @@ we currently don't give any bonus for false eyes. should we?
     *
     * @return the overall health of the group independent of nbr groups.
     */
    public float calculateAbsoluteHealth(GoBoard board, GameProfiler profiler )
    {
        if ( !eyeCacheBroken_ ) {
            return absoluteHealth_;
        }

        // if nothing has changed about the group, then we can return the cached value
        int numLiberties = getLiberties(board).size();

        // we multiply by a +/- sign depending on the side
        float side = group_.isOwnedByPlayer1() ? 1.0f : -1.0f;

        // we need to come up with some approximation for the health so update eyes can be done more accurately.
        float numEyes = calcNumEyes();
        absoluteHealth_ = determineHealth(side, numEyes, numLiberties, board);

        profiler.start(GoProfiler.UPDATE_EYES);
        updateEyes(board);  // expensive
        profiler.stop(GoProfiler.UPDATE_EYES);

        numEyes = Math.max(eyePotential_, calcNumEyes());

        // health based on eye shape - the most significant factor
        float health = determineHealth(side, numEyes, numLiberties, board);

        // Should there be any bonus at all for false eyes??  no
        // health += (side * .015 * numFalseEyes);

        absoluteHealth_ = health;
        if (Math.abs(absoluteHealth_) > 1.0) {
            GameContext.log(0,  "Warning: health exceeded 1.0: " +" health="+health+" numEyes="+numEyes);
            absoluteHealth_ = side;
        }

        return absoluteHealth_;
    }


    /**
     * @return  set of eyes currently identified for this group.
     */
    public Set<GoEye> getEyes(GoBoard board)
    {
        updateEyes(board);
        return eyes_;
    }

    public void breakEyeCache() {
        clearEyes(); 
    }

    /**
     * Get the number of liberties that the group has.
     * @return the number of liberties that the group has
     */
    public Set<GoBoardPosition> getLiberties(GoBoard board)
    {
        if (!eyeCacheBroken_) {
             return cachedLiberties_;
        }
        Set<GoBoardPosition> liberties = new HashSet<GoBoardPosition>();
        for (GoString str : group_.getMembers()) {
            liberties.addAll(str.getLiberties(board));
        }
        cachedLiberties_ = liberties;
        return liberties;
    }

    /**
     * If nothing cached, this may not be accurate.
     * @return number of cached liberties.
     */
    public int getNumLiberties() {
        return cachedLiberties_== null ? 0 : cachedLiberties_.size();
    }

    /**
     * Calculate the number of stones in the group.
     * @return number of stones in the group.
     */
    public int getNumStones()
    {
        if (!eyeCacheBroken_) {
            return cachedNumStonesInGroup_;
        }
        int numStones = 0;
        for (GoString str : group_.getMembers()) {
            numStones += str.size();
        }
        cachedNumStonesInGroup_ = numStones;
        return numStones;
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean hasChanged()
    {
        return eyeCacheBroken_;
    }

    /** used only for test. */
    public float getEyePotential() {
        assert !eyeCacheBroken_;
        return eyePotential_;
    }

    /**
     * Determine approximately how many eyes the group has.
     * This is purposely a little vague, but if more than 2.0, then must be unconditionally alive.
     * The value that we count for each type of eye could be optimized.
     * @return approximation to the number of eyes in group
     */
    private float calcNumEyes() {
        // figure out how many of each eye type we have
        Iterator<GoEye> it = eyes_.iterator();
        float numEyes = 0;
        while ( it.hasNext() ) {
            GoEye eye = it.next();
            switch (eye.getEyeType()) {
                case FALSE_EYE:
                    numEyes+= 0.19f;
                    break;
                case TRUE_EYE:
                    numEyes++;
                    break;
                case BIG_EYE:
                    numEyes += 1.1;
                    break;
                case TERRITORIAL_EYE:
                    numEyes += 1.6f;
                    break; // counts as 2 true eyes
            }
        }

        return numEyes;
    }

    /**
     * @return the health of the group based on the number of eyes and the number of liberties.
     */
    private float determineHealth(float side, float numEyes, int numLiberties, GoBoard board)  {
        float health;

        if ( numEyes >= 2.0 )  {
           health = calcTwoEyedHealth(side, board);
        }
        else if (numEyes >= 1.5) {
            health = calcAlmostTwoEyedHealth(side, numLiberties);
        }
        else if (numEyes >= 1.0) {
            health = calcOneEyedHealth(side, numLiberties);
        }
        else {
            health = calcNoEyeHealth(side, numLiberties);
        }
        return health;
    }


    private static final float BEST_TWO_EYED_HEALTH = 1.0f;
    private static final float BEST_ALMOST_TWO_EYED_HEALTH = 0.94f;
    private static final float BEST_ONE_EYED_HEALTH = 0.89f;

    /**
     * @return the health of a group that has 2 eyes.
     */
    private float calcTwoEyedHealth(float side, GoBoard board) {
        float health;
        LifeAnalyzer analyzer = new LifeAnalyzer(group_, board);
        if (analyzer.isUnconditionallyAlive()) {
            // in addition to this, the individual strings will get a score of side (ie +/- 1).
            health = BEST_TWO_EYED_HEALTH * side;
        }
        else {
            // its probably alive
            // may not be alive if the opponent has a lot of kos and gets to play lots of times in a row
            health = BEST_ALMOST_TWO_EYED_HEALTH * side;
        }
        return health;
    }

    /**
     * @return the health of a group that has only one eye.
     */
    private float calcAlmostTwoEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6)  {
            health = side * Math.min(BEST_ALMOST_TWO_EYED_HEALTH, (1.15f - 20.0f/(numLiberties + 23.0f)));
        }
        else  {  // numLiberties<=5. Very unlikely to occur
            switch (numLiberties) {
                case 0:
                case 1:
                    // assert false: "can't have almost 2 eyes and only 1 or fewer liberties! " + this.toString();
                    // but apparently it can (seen on 5x5 game):
                    //  OOOOX
                    //      XOX
                    break;
                case 2:
                    health = side * 0.02f;
                    // this actually happens quite often.
                     GameContext.log(1, "We have almost 2 eyes but only 2 Liberties. How can that be? " + this.toString());
                    break;
                case 3:
                    health = side * 0.05f;
                    break;
                case 4:
                    health = side * 0.1f;
                    break;
                case 5:
                    health = side * 0.19f;
                    break;
                case 6:
                    health = side * 0.29f;
                    break;
                default: assert false;
            }
        }
        return health;
    }

    /**
     * @return the health of a group that has only one eye.
     */
    private static float calcOneEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6)  {
            health = side * Math.min(BEST_ONE_EYED_HEALTH, (1.03f - 20.0f/(numLiberties + 20.0f)));
        }
        else  {  // numLiberties<=5
            switch (numLiberties) {
                case 0:
                    // this can't happen because the stone should already be captured.
                    assert false: "can't have 1 eye and no liberties!";
                    break;
                case 1:
                    // @@ we need to consider a seki here.
                    // what if the neighboring enemy group also has one or zero eyes?
                    // one eye beats no eyes.
                    health = -side * 0.8f;
                    break;
                case 2:
                    health = -side * 0.3f;
                    break;
                case 3:
                    health = -side * 0.2f;
                    break;
                case 4:
                    health = -side * 0.05f;
                    break;
                case 5:
                    health = side * 0.01f;
                    break;
                case 6:
                    health = side * 0.19f;
                    break;
                default: assert false;
            }
        }
        return health;
    }

    /**
     *  @return the health of a group that has no eyes.
     */
    float calcNoEyeHealth(float side, int numLiberties) {

        int numStones = getNumStones();

        if ( numLiberties > 5 )  {
            return side * Math.min(0.8f, (1.2f - 46.0f/(numLiberties+40.0f)));
        }
        else if (numStones == 1) {
            return calcSingleStoneHealth(side, numLiberties);
        } else {
            return calcMultiStoneHealth(side, numLiberties);
        }
    }

    /**
     * @return health of a single stone mased on numbe rof liberties it has.
     */
    private float calcSingleStoneHealth(float side, int numLiberties) {
        float health = 0;
        switch (numLiberties) { // numEyes == 0
            case 0:
                // this can't happen because the stone should already be captured.
                assert false : "can't have no liberties and still be on the board! "+ this;
                health = -side;
                break;
            case 1:
                health = -side * 0.6f;
                break;
            case 2:
                // @@ consider seki situations where the adjacent enemy group also has no eyes.
                //      XXXXXXX     example of seki here.
                //    XXooooooX
                //    Xo.XXX.oX
                //    XooooooXX
                //    XXXXXXX
                health = side * 0.02f;
                break;
            case 3:
                health = side * 0.1f;
                break;
            case 4:
                health = side * 0.1f;
                break;
            default: assert false : "there were too many liberties for a single stone: "+numLiberties;
        }
        return health;
    }

    /*
     * @return health for multi-stone groupe iwth less than 5 liberties.
     */
    private float calcMultiStoneHealth(float side, int numLiberties) {
        float health = 0;
        switch (numLiberties) { // numEyes == 0
            case 0:
                // this can't happen because the stone should already be captured.
                //assert false : "can't have no liberties and still be on the board! "+ this;
                health = -side;
                break;
            case 1:
                health = -side * 0.6f;
                break;
            case 2:
                // @@ consider seki situations where the adjacent enemy group also has no eyes.
                //      XXXXXXX     example of seki here.
                //    XXooooooX
                //    Xo.XXX.oX
                //    XooooooXX
                //    XXXXXXX
                health = -side * 0.3f;
                break;
            case 3:
                health = side * 0.02f;
                break;
            case 4:
                health = side * 0.05f;
                break;
            case 5:
                health = side * 0.1f;
                break;
            default: assert false : "We should have already covered the case of >5 liberties";
        }
        return health;
    }


    /**
     * compute how many eyes (connected internal blank areas) this group has.
     * the eyes are either false eyes or true (or big or territorial) eyes.
     * Also update eyePotential (a measure of how good the groups ability to make 2 eyes(.
     * This method is expensive. That is why the 2 things it computes (eyes and eyePotential) are cached.
     */
    private void updateEyes(GoBoard board)
    {
        if (!eyeCacheBroken_ || board == null) {
            return;
        }

        EyeSpaceAnalyzer eyeAnalyzer = new EyeSpaceAnalyzer(group_, board);
        eyes_ = eyeAnalyzer.determineEyes();
        eyePotential_ = eyeAnalyzer.calculateEyePotential();
         // cached until something changes
        eyeCacheBroken_ = false;
    }

    /**
     * clear the current eyes for the group (in preparation for recomputing them).
     */
    private void clearEyes()
    {
        if (eyes_.isEmpty())
            return;
        for (GoEye eye : eyes_) {
            eye.clear();
        }
        eyes_.clear();
        eyeCacheBroken_ = true;
    }

    /**
     * @return a deep copy of this instance
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();

        if (eyes_ !=null)  {
            ((AbsoluteHealthCalculator)clone).eyes_ = new HashSet<GoEye>();
            Set<GoEye> m = ((AbsoluteHealthCalculator)clone).eyes_;

            for (GoEye eye : this.eyes_) {
                m.add((GoEye) eye.clone());
            }
        }
        return clone;
    }
}
