package com.becker.game.twoplayer.go;


import java.util.*;


/**
 * static untility methods to support the GoBoard class.
 *
 * @see GoBoard
 * @author Barry Becker.
 */
public final class GoBoardUtil
{

    /**
     * Get an adjacent neighbor stone restricted to the desired type.
     *
     * @param nbrStone   the neighbor to check
     * @param friendOwnedByP1  type of the center stone (can't use center.owner since center may be unnoccupied)
     * @param nbrs  hashset of the ngbors matching the criteria.
     * @param neighborType  one of NEIGHBOR_ANY, NEIGHBOR_ENEMY_ONLY, or NEIGHBOR_FRIENDLY_ONLY
     */
    static void getNobiNeighbor( GoBoardPosition nbrStone, boolean friendOwnedByP1, HashSet nbrs, NeighborType neighborType )
    {
        if (nbrStone.isUnoccupied()) return;

        boolean correctNeighborType = true;
        switch (neighborType) {
            case ANY:
                correctNeighborType = true;
                break;
            case OCCUPIED:
                correctNeighborType = (nbrStone.getPiece()!=null);
                break;
            case ENEMY: // the opposite color
                GoStone st = (GoStone)nbrStone.getPiece();
                correctNeighborType = (st.isOwnedByPlayer1() != friendOwnedByP1);
                break;
            case FRIEND: // the same color
                correctNeighborType = (nbrStone.getPiece().isOwnedByPlayer1() == friendOwnedByP1);
                break;
            default : assert false: "unknown or unsupported neighbor type:"+neighborType;
        }
        if (correctNeighborType ) {
            // might happen if the stone belongs to an eye instead of a string.
            assert ( nbrStone.getString()!=null): "this stone does not belong to a string:" + nbrStone;
            nbrs.add( nbrStone );
        }
    }


    /**
     * set the visited flag back to false for a list of lists of stones
     */
    public static void unvisitPositionsInLists( List lists )
    {
        Iterator it = lists.iterator();
        while ( it.hasNext() )
            unvisitPositionsInList( (List) it.next() );
    }

    /**
     * set the visited flag back to false for a set of stones
     */
    public static void unvisitPositionsInList( List positions )
    {
        Iterator it = positions.iterator();
        // return the stone to the unvisited state
        while ( it.hasNext() ) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            s.setVisited( false );
        }
    }


    static void confirmNoDupes( GoBoardPosition seed, List list )
    {
        Object[] stoneArray = list.toArray();

        for ( int i = 0; i < stoneArray.length; i++ ) {
            GoBoardPosition st = (GoBoardPosition) stoneArray[i];
            // make sure that this stone is not a dupe of another in the list
            for ( int j = (i + 1); j < stoneArray.length; j++ ) {
                assert (st != stoneArray[j]): "found a dupe=" + st + " in " + list + "]n the seed = " + seed ;
            }
        }
    }


    public static void confirmUnvisited( List stones)
    {
        Iterator it = stones.iterator();
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            assert !p.isVisited(): p+" in "+stones+" was visited";
        }
    }

}
