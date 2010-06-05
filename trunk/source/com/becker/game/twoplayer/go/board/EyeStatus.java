package com.becker.game.twoplayer.go.board;

/**
 * Enum for the different possible center Eye Status'.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 * @see GoEye
 *
 * @author Barry Becker
 */
public enum EyeStatus
{
    /**
     * The eye will end up as only one eye and this will not be sufficient to
     * live. A nakade eye can be the result of: (1) an eye with an empty set of vital
     * points or (2) an eye with all the set of vital points filled by the opponent’s stones.
     */
    NAKADE("Nakade", "Ends up as one eye and this will not be sufficient to live."),

    /**
     * The eye can end up as a nakade eye or an
     * alive eye depending on the next color to play. An unsettled
     * eye is the result of an eye with one and only one empty
     * intersection in the set of vital points.
     */
    UNSETTLED("Unsettled", "Ends up either nakade or alive depending on who moves first."),

    /**
     * The string owning the eye is alive no matter who plays first and no matter what the surrounding conditions are.
     * An alive eye can be the result of: (1) an eye with two or more empty intersections in the set of vital points
     * or (2) the eye is a shape that cannot be filled by the opponent with an n-1 nakade shape.
	 * We will make no distinction between being alive or being alive in seki, because in many cases
     * being alive in seki is nearly as good.
     */
    ALIVE("Alive", "Unconditionally alive no matter who plays first."),

    /**
     * This is a particular case in which the surrounding conditions
     * determine the status of the eye. We say that an eye has an AliveInAtari status
     * if there are only one or zero empty intersections adjacent to the surrounding
     * block but capturing the opponent stones inside the eye grants an alive status.
     * Only when the external liberties of the string owning the eye are played it is
     * necessary to capture the stones inside the eye.
     */
    ALIVE_IN_ATARI("Alive in atari", "There are only one or zero empty intersections adjacent to the surrounding" +
            " block, and capturing the opponent stones inside the eye grants an alive status."),

    /**
     * Other possibilities: Unknown, dead, false
     */
    UNCLASSIFIED("Unclassified", "The status has not been determined yet");

    private String label_;
    private String description_;

    

    /**
     * constructor.
     *
     * @param label simple label
     * @param description long description of the eye status.
     */
    EyeStatus(String label, String description) {
        label_ = label;
        description_ = description;
    }


    @Override
    public String toString() {
        return label_;
    }

    public String getDescription() {
        return description_;
    }

}