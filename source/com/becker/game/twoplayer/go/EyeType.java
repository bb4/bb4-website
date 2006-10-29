package com.becker.game.twoplayer.go;

/**
 * Enum for the different possible Eye types.
 * @see GoEye
 *
 * @author Barry Becker
 */
public enum EyeType
{
    FALSE_EYE("FalseEye", "Becomes true eye if you play first."),
    TRUE_EYE("TrueEye", "Unconditional eye."),
    BIG_EYE("BigEye", "One or two eye depending on who plays first."),
    TERRITORIAL_EYE("Territory", "At least 2 eyes even if opponent plays first.");

    private String label_;
    private String description_;


    /**
     * constructor for eye type enum
     *
     * @param description string name of the eye type (eg "False Eye")
     */
    private EyeType(String label, String description) {
        label_ = label;
        description_ = description;
    }


    public String toString() {
        return label_;
    }

    public String getDescription() {
        return description_;
    }


}

