package com.becker.game.twoplayer.go.board.analysis.neighbor;

/**
 * Enum for the different possible Neighbor types.
 * These constants represent the types of possible neigbors that can be searched for.
 *
 * @author Barry Becker
 */
public enum NeighborType
{
    /** Has a stone in the space */
    OCCUPIED,
            
    /** No stone at the nbr position. */
    UNOCCUPIED,
    
    /** nbr stone same color as current stone. */
    FRIEND,
    
    /** nbr stone enemy of current stone. */
    ENEMY,
    
    /** Enemy or unoccupied. */
    NOT_FRIEND,
    
    /** any kind of nbr. */
    ANY
}

