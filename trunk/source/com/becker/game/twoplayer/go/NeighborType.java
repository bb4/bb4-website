package com.becker.game.twoplayer.go;

/**
 * Enum for the different possible Neighbor types.
 * @see GoBoard
 * @see GoBoardUtil
 *
 * @author Barry Becker
 */
public enum NeighborType
{

    // Ordinals
    // these constants represent the types of possible neigbors that can be searched for

    // has a steon
    OCCUPIED,
    // no stone at the nbr position
    UNOCCUPIED,
    // single eye if opponent plays first; 2 eyes if you play 1st.
    FRIEND,
    // nbr stone enemy of current
    ENEMY,
    // enemy or unoccupied
    NOT_FRIEND,
    // any kind of nbr.
    ANY;

}

