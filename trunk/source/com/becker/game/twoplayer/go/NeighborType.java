package com.becker.game.twoplayer.go;

import com.becker.common.EnumeratedType;

/**
 * Enum for the different possible Neighbor types.
 * @see GoBoard
 * @see GoBoardUtil
 *
 * @author Barry Becker
 */
public final class NeighborType extends EnumeratedType.BasicValue
{

    // Ordinals
    // these constants represent the types of possible neigbors that can be searched for

    // has a steon
    public static final int OCCUPIED_CODE = 0;
    // no stone at the nbr position
    public static final int UNOCCUPIED_CODE = 1;
    // single eye if opponent plays first; 2 eyes if you play 1st.
    public static final int FRIEND_CODE = 2;
    // nbr stone enemy of current
    public static final int ENEMY_CODE = 3;
    // enemy or unoccupied
    public static final int NOT_FRIEND_CODE = 4;
    // any kind of nbr.
    public static final int ANY_CODE = 5;


    // The enumerated values
    public static final NeighborType OCCUPIED =
            new NeighborType(OCCUPIED_CODE, "Occupied");
    public static final NeighborType UNOCCUPIED =
            new NeighborType(UNOCCUPIED_CODE, "Unoccupied");
    public static final NeighborType FRIEND =
            new NeighborType(FRIEND_CODE, "Friend");
    public static final NeighborType ENEMY =
            new NeighborType(ENEMY_CODE, "Enemey");
    public static final NeighborType NOT_FRIEND =
            new NeighborType(NOT_FRIEND_CODE, "Big Eye");
    public static final NeighborType ANY =
            new NeighborType(ANY_CODE, "Territorial", "All neighbors.");

    /**
     * Contains all valid {@link com.becker.game.twoplayer.go.NeighborType}s.
     */
    private static final EnumeratedType enumeration = new EnumeratedType(
            new NeighborType[] {
                OCCUPIED , UNOCCUPIED, FRIEND, ENEMY, NOT_FRIEND, ANY
            }
    );

    /**
     * constructor for nbr type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     */
    private NeighborType(final int ordinal, final String name) {
        super(name, ordinal, null);
    }

    /**
     * constructor for nbr type enum
     *
     * @param ordinal ordered integer value for the eye type enum
     * @param name string name of the eye type (eg "False Eye")
     * @param description short description.
     */
    private NeighborType(final int ordinal, final String name, final String description) {
            super(name, ordinal, description);
        }


    public EnumeratedType getEnumeratedType() {
        return enumeration;
    }

    /**
     * Looks up an {@link com.becker.game.twoplayer.go.NeighborType}
     * @throws Error if the name is not a member of the enumeration
     */
    public static NeighborType get(final String name, final boolean finf) {
        return (NeighborType) enumeration.getValue(name, finf);
    }
}

