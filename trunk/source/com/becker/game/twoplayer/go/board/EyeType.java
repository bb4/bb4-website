package com.becker.game.twoplayer.go.board;

/**
 * Enum for the different possible Eye types.
 * @see GoEye
 *
 * @author Barry Becker
 */
public enum EyeType
{
    FALSE_EYE("FalseEye", "Becomes true eye if you play first.", 0.19f),
    TRUE_EYE("TrueEye", "Unconditional eye.", 1.0f),
    BIG_EYE("BigEye", "One or two eye depending on who plays first.", 1.2f),
    TERRITORIAL_EYE("Territory", "At least 2 eyes even if opponent plays first.", 1.6f);

    private String label_;
    private String description_;
    private float eyeValue_;


    /**
     * constructor for eye type enum
     *
     * @param label nice label.
     * @param description string name of the eye type (eg "False Eye")
     * @param eyeValue how much this particular eye counts toward being a ture eye (or eyes)
     */
    EyeType(String label, String description, float eyeValue) {
        label_ = label;
        description_ = description;
        eyeValue_ = eyeValue;
    }


    @Override
    public String toString() {
        return label_;
    }

    public String getDescription() {
        return description_;
    }

    public float getEyeValue() {
        return eyeValue_;
    }

}

