package com.becker.game.twoplayer.go;

import com.becker.common.EnumeratedType;

/**
 * Enum for the different possible Eye types.
 * @see GoEye
 *
 * @author Barry Becker
 */
public enum EyeType
{
    FALSE_EYE("Becomes true eye if you play first."),
    TRUE_EYE("Unconditional eye."),
    BIG_EYE("One or two eye depending on who plays first."),
    TERRITORIAL_EYE("At least 2 eyes even if opponent plays first.");


    private String description_;


    /**
     * constructor for eye type enum
     *
     * @param description string name of the eye type (eg "False Eye")
     */
    private EyeType(final String description) {
        description_ = description;
    }


}

