package com.becker.game.twoplayer.go;

import com.becker.common.EnumeratedType;

/**
 * Enum for the different possible Eye types.
 * @see GoEye
 *
 * @author Barry Becker
 */
public final class EyeType  extends  EnumeratedType.BasicValue
{

    // Ordinals
    // these constants represent the types of possible eyes that can be formed
    public static final int FALSE_EYE_CODE = 0;
    public static final int TRUE_EYE_CODE = 1;
    // single eye if opponent plays first; 2 eyes if you play 1st
    public static final int BIG_EYE_CODE = 2;
    // large internal space (>= 2 eyes) - even if opponent plays first
    public static final int TERRITORIAL_EYE_CODE = 3;


    // The enumerated values
    public static final EyeType FALSE_EYE =
            new EyeType(FALSE_EYE_CODE, "False Eye", "Becomes true eye if you play first.");
    public static final EyeType TRUE_EYE =
            new EyeType(TRUE_EYE_CODE, "True Eye", "Unconditional eye.");
    public static final EyeType BIG_EYE =
            new EyeType(BIG_EYE_CODE, "Big Eye","One or two eye depending on who plays first.");
    public static final EyeType TERRITORIAL_EYE =
            new EyeType(TERRITORIAL_EYE_CODE, "Territorial", "At least 2 eyes even if opponent plays first.");

    /**
     * Contains all valid {@link EyeType}s.
     */
    private static final EnumeratedType enumeration = new EnumeratedType(
            new EyeType[] {
                FALSE_EYE , TRUE_EYE, BIG_EYE, TERRITORIAL_EYE
            }
    );

    /**
     * constructor for eye type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     */
    private EyeType(final int ordinal, final String name) {
        super(name, ordinal, null);
    }

    /**
     * constructor for eye type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     * @param description short description.
     */
    private EyeType(final int ordinal, final String name, final String description) {
            super(name, ordinal, description);
        }


    public EnumeratedType getEnumeratedType() {
        return enumeration;
    }

    /**
     * Looks up an {@link EyeType}
     * @throws Error if the name is not a member of the enumeration
     */
    public static EyeType get(final String name, final boolean finf) {
        return (EyeType) enumeration.getValue(name, finf);
    }
}

