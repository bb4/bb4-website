package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
import com.becker.common.Location;
import com.becker.game.common.Box;
import com.becker.game.common.GameContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Performs static analysis of a go board to determine strings and
 * other analysis involving neighbor locations.
 * 
 * @author becker
 */
public class NeighborAnalyzer {

    GoBoard board_;
    
    public NeighborAnalyzer(GoBoard board) {
        board_ = board;
    }
   
        
    /**
     * determines a string connected from a seed stone within a specified bounding area
     * @return string from seed stone
     */
    public List<GoBoardPosition> findStringFromInitialPosition( GoBoardPosition stone,  boolean friendOwnedByP1,
                                                     boolean returnToUnvisitedState, NeighborType type,
                                                     int rMin, int rMax, int cMin, int cMax )
    {
        List<GoBoardPosition> stones = new ArrayList<GoBoardPosition>();
        // perform a breadth first search  until all found.
        // use the visited flag to indicate that a stone has been added to the string
        List<GoBoardPosition> stack = new LinkedList<GoBoardPosition>();
        assert ( stone.getRow() >= rMin && stone.getRow() <= rMax && stone.getCol() >= cMin && stone.getCol() <= cMax ):
                "rMin="+rMin +" rMax="+rMax+" cMin="+cMin+" cMax="+cMax+"   r="+stone.getRow()+" c="+stone.getCol();
        assert ( !stone.isVisited() ): "stone="+stone;
        stack.add( 0, stone );
        while ( !stack.isEmpty() ) {
            GoBoardPosition s = stack.remove( 0 );
            if ( !s.isVisited() ) {
                s.setVisited( true );
                stones.add( s );
                pushStringNeighbors(s, friendOwnedByP1, stack, true, type,  new Box(rMin, cMin, rMax, cMax));
            }
        }
        if ( returnToUnvisitedState )
            GoBoardUtil.unvisitPositions( stones );
        // GoBoardUtil.confirmNoDupes( stone, stones );

        return stones;
    }

    /**
     * get neighboring stones of the specified stone.
     * @param stone the stone (or space) whose neighbors we are to find.
     * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space and has undefined ownership.
     * @param neighborType (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    public Set<GoBoardPosition> getNobiNeighbors( GoBoardPosition stone, boolean friendOwnedByP1, NeighborType neighborType )
    {
        Set<GoBoardPosition> nbrs = new HashSet<GoBoardPosition>();
        int row = stone.getRow();
        int col = stone.getCol();

        if ( row > 1 )
            getNobiNeighbor( (GoBoardPosition) board_.getPosition(row - 1, col),
                                         friendOwnedByP1, nbrs, neighborType );
        if ( row + 1 <= board_.getNumRows() )
            getNobiNeighbor( (GoBoardPosition) board_.getPosition(row + 1, col),
                                         friendOwnedByP1, nbrs, neighborType );
        if ( col > 1 )
            getNobiNeighbor( (GoBoardPosition) board_.getPosition(row, col - 1),
                                         friendOwnedByP1, nbrs, neighborType );
        if ( col + 1 <= board_.getNumCols() )
            getNobiNeighbor( (GoBoardPosition)board_.getPosition(row, col + 1),
                                         friendOwnedByP1, nbrs, neighborType );

        return nbrs;
    }
    
    /**
     * 
     * @param stone
     * @return
     */
    public Set<GoString> findStringNeighbors(GoBoardPosition stone ) {
        Set<GoString> stringNbrs = new HashSet<GoString>();
        List<GoBoardPosition> nobiNbrs = new LinkedList<GoBoardPosition>();
        pushStringNeighbors(stone, false, nobiNbrs, false);

        // add strings only once
        for (Object nn : nobiNbrs) {
            GoBoardPosition nbr = (GoBoardPosition)nn;
            stringNbrs.add(nbr.getString());
        }
        return stringNbrs;
    }
    
    /**
     * Get an adjacent neighbor stones restricted to the desired type.
     *
     * @param nbrStone   the neighbor to check
     * @param friendOwnedByP1  type of the center stone (can't use center.owner since center may be unnoccupied)
     * @param nbrs  hashset of the ngbors matching the criteria.
     * @param neighborType  one of NEIGHBOR_ANY, NEIGHBOR_ENEMY_ONLY, or NEIGHBOR_FRIENDLY_ONLY
     */
    private static void getNobiNeighbor( GoBoardPosition nbrStone, boolean friendOwnedByP1, 
                                                                 Set<GoBoardPosition> nbrs, NeighborType neighborType )
    {

        boolean correctNeighborType = true;
        switch (neighborType) {
            case ANY:
                correctNeighborType = true;
                break;
            case OCCUPIED:
                // note friendOwnedByP1 is intentionally ignored
                correctNeighborType = nbrStone.isOccupied();
                break;
            case ENEMY: // the opposite color
                if (nbrStone.isUnoccupied())
                    return;
                GoStone st = (GoStone)nbrStone.getPiece();
                correctNeighborType =  st.isOwnedByPlayer1() != friendOwnedByP1;
                break;
            case FRIEND: // the same color
                if (nbrStone.isUnoccupied())
                    return;
                correctNeighborType = (nbrStone.getPiece().isOwnedByPlayer1() == friendOwnedByP1);
                break;
            default : assert false: "unknown or unsupported neighbor type:"+neighborType;
        }
        if (correctNeighborType ) {
            nbrs.add( nbrStone );
        }
    }


    /**
     * return a set of stones which are loosely connected to this stone.
     * Check the 16 purely group neighbors and 4 string neighbors
     *         ***
     *        **S**
     *        *SXS*
     *        **S**
     *         ***
     * @param stone (not necessarily occupied)
     * @param friendPlayer1 typically stone.isOwnedByPlayer1 value of stone unless it is blank.
     * @param samePlayerOnly if true then find group nbrs that are have same ownership as friendPlayer1
     */
    public Set<GoBoardPosition> getGroupNeighbors( GoBoardPosition stone, boolean friendPlayer1, boolean samePlayerOnly )
    {  
        List<GoBoardPosition> stack = new LinkedList<GoBoardPosition>();

        pushGroupNeighbors( stone, friendPlayer1, stack, samePlayerOnly );
        Set<GoBoardPosition> nbrStones = new HashSet<GoBoardPosition>();
        nbrStones.addAll( stack );

        return nbrStones;
    }

    /**
     * Check all nobi neighbors (at most 4).
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, 
                                     List<GoBoardPosition> stack, boolean samePlayerOnly, 
                                     NeighborType type, Box bbox )  {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;
        Location loc = new Location(r, c);

        if ( r > 1 )
            numPushed += checkNeighbor( loc, -1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( c > 1 )
            numPushed += checkNeighbor( loc, 0, -1, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( r + 1 <= board_.getNumRows() )
            numPushed += checkNeighbor( loc, 1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox );
        if ( c + 1 <= board_.getNumCols() )
            numPushed += checkNeighbor( loc, 0, 1, friendPlayer1, stack, samePlayerOnly, type, bbox );

        return numPushed;
    }

    private int pushStringNeighbors( GoBoardPosition s, boolean friendPlayer1, List<GoBoardPosition> stack, 
                                               boolean samePlayerOnly )
    {
        return pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly,
                                    NeighborType.OCCUPIED, new Box(1, 1, board_.getNumRows(), board_.getNumCols()));
    }

    /**
     * Check all diagonal neighbors (at most 4).
     * @param s the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushEnemyDiagonalNeighbors( GoBoardPosition s, boolean friendPlayer1, 
                                                                           List<GoBoardPosition> stack )
    {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;
        if ( r > 1 && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, -1, -1, friendPlayer1, false, stack );
        if ( r + 1 <= board_.getNumRows() && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, 1, -1, friendPlayer1, false, stack );
        if ( r + 1 <= board_.getNumRows() && c + 1 <= board_.getNumCols() )
            numPushed += checkDiagonalNeighbor( r, c, 1, 1, friendPlayer1, false, stack );
        if ( r > 1 && c + 1 <= board_.getNumCols() )
            numPushed += checkDiagonalNeighbor( r, c, -1, 1, friendPlayer1, false, stack );

        return numPushed;
    }

    /**
     * Check all non-nobi group neighbors.
     * @param pos the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @param sameSideOnly if true push pure group nbrs of the same side only.
     * @return number of stones added to the stack
     */
    private int pushPureGroupNeighbors( GoBoardPosition pos, boolean friendPlayer1, boolean sameSideOnly, 
                                                                   List<GoBoardPosition> stack )
    {
        int r = pos.getRow();
        int c = pos.getCol();
        int numPushed = 0;

        // if the stone of which we are checking nbrs is in atari, then there are no pure group nbrs because an
        // atari counts as a cut
        if (pos.isInAtari(board_))
          return 0;

        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();

        // now check the diagonals
        if ( r > 1 && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, -1, -1, friendPlayer1, sameSideOnly, stack );
        if ( r > 1 && c + 1 <= numCols )
            numPushed += checkDiagonalNeighbor( r, c, -1, 1, friendPlayer1, sameSideOnly, stack );
        if ( r + 1 <= numRows && c + 1 <= numCols )
            numPushed += checkDiagonalNeighbor( r, c, 1, 1, friendPlayer1, sameSideOnly, stack );
        if ( r + 1 <= numRows && c > 1 )
            numPushed += checkDiagonalNeighbor( r, c, 1, -1, friendPlayer1, sameSideOnly, stack );

        // now check the 1-space jumps
        if ( r > 2 )
            numPushed += checkOneSpaceNeighbor( r, c, -2, 0, friendPlayer1, sameSideOnly, stack );
        if ( c > 2 )
            numPushed += checkOneSpaceNeighbor( r, c, 0, -2, friendPlayer1, sameSideOnly, stack );
        if ( r + 2 <= numRows )
            numPushed += checkOneSpaceNeighbor( r, c, 2, 0, friendPlayer1, sameSideOnly, stack );
        if ( c + 2 <= numCols )
            numPushed += checkOneSpaceNeighbor( r, c, 0, 2, friendPlayer1, sameSideOnly, stack );

        // now check knights move neighbors
        if ( (r > 2) && (c > 1) )
            numPushed += checkKogeimaNeighbor( r, c, -2, -1, friendPlayer1,  sameSideOnly, stack );
        if ( (r > 2) && (c + 1 <= numCols) )
            numPushed += checkKogeimaNeighbor( r, c, -2, 1, friendPlayer1, sameSideOnly, stack );

        if ( (r + 2 <= numRows) && (c > 1) )
            numPushed += checkKogeimaNeighbor( r, c, 2, -1, friendPlayer1, sameSideOnly, stack );
        if ( (r + 2 <= numRows) && (c + 1 <= numCols) )
            numPushed += checkKogeimaNeighbor( r, c, 2, 1, friendPlayer1, sameSideOnly, stack );

        if ( (r > 1) && (c > 2) )
            numPushed += checkKogeimaNeighbor( r, c, -1, -2, friendPlayer1, sameSideOnly, stack );
        if ( (r + 1 <= numRows) && (c > 2) )
            numPushed += checkKogeimaNeighbor( r, c, 1, -2, friendPlayer1, sameSideOnly, stack );

        if ( (r > 1) && (c + 2 <= numCols))
            numPushed += checkKogeimaNeighbor( r, c, -1, 2, friendPlayer1, sameSideOnly, stack );
        if ( (r + 1 <= numRows) && (c + 2 <= numCols) )
            numPushed += checkKogeimaNeighbor( r, c, 1, 2, friendPlayer1, sameSideOnly, stack );

        return numPushed;
    }


    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals are not cut nor 1-space jumps peeped.
     *
     * @param s the position containing a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find groups stones for.
     * @param stack the stack to add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, List<GoBoardPosition> stack )
    {
        return pushGroupNeighbors( s, friendPlayer1, stack, true );
    }

    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals and 1-space jumps are not cut.
     * Don't push a group neighbor if it is part of a string which is in atari
     *
     * @param s the position of a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find group stones for.
     * @param stack the stack on which we add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, List<GoBoardPosition> stack, 
                                                           boolean samePlayerOnly )
    {
        // start with the nobi string nbrs
        int numPushed = pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly );

        // now push the non-nobi group neighbors
        if ( !samePlayerOnly )
            numPushed += pushEnemyDiagonalNeighbors( s, friendPlayer1, stack );

        // we only find pure group neighbors of the same color
        numPushed += pushPureGroupNeighbors( s, friendPlayer1, true, stack );

        return numPushed;
    }


    /**
     * determine a set of stones that have group connections to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     * Group connections include nobi, ikken tobi, and kogeima.
     *
     * @param stone the stone to search from for group neighbors.
     * @param returnToUnvisitedState if true, then mark everything unvisited when done.
     * @return the list of stones in the group that was found.
     */
    public List<GoBoardPosition> findGroupFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
    {
        
        List<GoBoardPosition> stones = new ArrayList<GoBoardPosition>();
        // perform a breadth first search  until all found.
        // use the visited flag to indicate that a stone has been added to the group
        List<GoBoardPosition> stack = new LinkedList<GoBoardPosition>();
        stack.add( 0, stone );
        while ( !stack.isEmpty() ) {
            GoBoardPosition s = stack.remove( 0 );
            if ( !s.isVisited()) {
                s.setVisited( true );
                assert (s.getPiece().isOwnedByPlayer1()==stone.getPiece().isOwnedByPlayer1()):
                        s+" does not have same ownership as "+stone;
                stones.add( s );
                pushGroupNeighbors( s, s.getPiece().isOwnedByPlayer1(), stack );
            }
        }
        if ( returnToUnvisitedState ) {
            GoBoardUtil.unvisitPositions( stones );
            if (GameContext.getDebugMode() > 1)
                BoardValidationUtil.confirmAllUnvisited(board_);
        }
        return stones;
    }
    
    
    /**
     * @param empties a list of unoccupied positions.
     * @return a list of stones bordering the set of empty board positions.
     */
    public Set<GoBoardPosition> findOccupiedNeighbors(List<GoBoardPosition> empties)
    {
        Set<GoBoardPosition> allNbrs = new HashSet<GoBoardPosition>();
        for (GoBoardPosition empty : empties) {
            assert (empty.isUnoccupied());
            Set<GoBoardPosition> nbrs = getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
            // add these nbrs to the set of all nbrs
            // (dupes automatically culled because HashSets only have unique members)
            allNbrs.addAll(nbrs);
        }
        return allNbrs;
    }

    /**
     * Check an immediately adjacent (nobi) nbr.
     *
     * @param r row
     * @param c column
     * @param rowOffset offset from row indicating position of ngbor to check
     * @param colOffset offset from column indicating position of ngbor to check
     * @param friendOwnedByPlayer1 need to specify this when the position checked, s, is empty and has undefined ownership.
     * @param stack if nbr fits criteria then add to stack
     * @param samePlayerOnly  mus the nbr be owned by the same player only
     * @param type one of REGULAR_PIECE, UNOCCUPIED, or NOT_FRIEND
     * @return  1 if this is a valid neighbor of the type that we want
     */
    private int checkNeighbor( int r, int c, int rowOffset, int colOffset,
                                    boolean friendOwnedByPlayer1, List<GoBoardPosition> stack, 
                                    boolean samePlayerOnly, NeighborType type)
    {
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);

        switch (type) {
            case OCCUPIED:  // occupied black or white
                if ( !nbr.isVisited() && nbr.isOccupied() &&
                     (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendOwnedByPlayer1)) {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case UNOCCUPIED:  // empty space
                if ( !nbr.isVisited() && nbr.isUnoccupied() ) {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           case NOT_FRIEND:   // blank or enemy
                if ( !nbr.isVisited() &&
                    ( nbr.isUnoccupied() ||
                       ( nbr.isOccupied() && (nbr.getPiece().isOwnedByPlayer1() != friendOwnedByPlayer1))
                    ))  {
                    stack.add( 0, nbr );
                    return 1;
                }
                break;
           default : assert false: "unknown or unsupported neighbor type:"+type;
        }
        return 0;
    }


    /**
     * return 1 if this is a valid neighbor according to specification.
     * These are the immediately adjacent (nobi) nbrs within the specified rectangular bounds
     */
    private int checkNeighbor( Location loc, int rowOffset, int colOffset,
                               boolean friendOwnedByPlayer1, List<GoBoardPosition> stack, 
                               boolean samePlayerOnly, NeighborType type,
                               Box bbox )
    {
        int r = loc.getRow();
        int c = loc.getCol();
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if ( nbr.getRow() >= bbox.getMinRow() && nbr.getRow() <= bbox.getMaxRow()
          && nbr.getCol() >= bbox.getMinCol() && nbr.getCol() <= bbox.getMaxCol() ) {
            return checkNeighbor( r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, type );
        }
        else {
            return 0;
        }
    }

    /**
     *  We allow these connections as long as the diagonal has not been fully cut.
     *  i.e. not an opponent stone on both sides of the cut (or the diag stone is not in atari).
     *
     *  @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
     */
    private int checkDiagonalNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean sameSideOnly, 
                                       List<GoBoardPosition> stack )
    {
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if (nbr.isUnoccupied()) {
            return 0;
        }
        // don't add it if it is in atari
        // but this leads to a problem in that ataried stones then don't belong to a group.
        if  (nbr.isInAtari(board_)) {
            return 0;
        }
        // determine the side we are checking for (one or the other)
        boolean sideTest = sameSideOnly ? friendPlayer1 : !friendPlayer1;
        if ( (nbr.getPiece().isOwnedByPlayer1() == sideTest) && !nbr.isVisited()) {
            if (!((board_.getPosition(r + rowOffset, c).isOccupied() &&
                   board_.getPosition(r + rowOffset, c).getPiece().isOwnedByPlayer1() != sideTest) &&
                   (board_.getPosition(r, c + colOffset).isOccupied() &&
                   board_.getPosition(r, c + colOffset).getPiece().isOwnedByPlayer1() != sideTest)) )  {
                // then not cut
                 stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
    }

    /**
     * Connected only add if not completely cut (there's no enemy stone in the middle).
     */
    private int checkOneSpaceNeighbor( int r, int c, int rowOffset, int colOffset,
                                                                 boolean friendPlayer1, boolean samePlayerOnly, 
                                                                 List<GoBoardPosition> stack )
    {
        GoBoardPosition nbr = (GoBoardPosition)board_.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        if (nbr.isInAtari(board_))
            return 0;
        if ( nbr.isOccupied() &&
                (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            // we consider the link cut if there is an opponent piece between the 2 stones
            //     eg:          *|*
            boolean cut;
            if ( rowOffset == 0 ) {
                int col = c + (colOffset >> 1);
                cut =  (board_.getPosition(r, col).isOccupied() &&
                        (board_.getPosition(r, col).getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int row = r + (rowOffset >> 1);
                cut =   (board_.getPosition(row, c).isOccupied() &&
                        (board_.getPosition(row, c).getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            if ( !cut ) {
                stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
    }

    /**
     * for the knight's move we consider it cut if there is an enemy stone at the base.
     */
    private int checkKogeimaNeighbor( int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly, 
                                      List<GoBoardPosition> stack )
    {
        if ( !board_.inBounds( r + rowOffset, c + colOffset )) {
            return 0;
        }
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        if (nbr.isInAtari(board_)) {
            return 0;
        }

        if ( nbr.isOccupied() && (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
            boolean cut;
            // consider it cut if there is an opponent stone in one of the 2 spaces between.
            if ( Math.abs( rowOffset ) == 2 ) {
                int rr = r + (rowOffset >> 1);
                cut = (board_.getPosition(rr, c).isOccupied()
                        && (board_.getPosition(rr, c).getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                        (board_.getPosition(rr, c + colOffset).isOccupied()
                        && (board_.getPosition(rr, c + colOffset).getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            else {
                int cc = c + (colOffset >> 1);
                cut = (board_.getPosition(r, cc).isOccupied()
                        && (board_.getPosition(r, cc).getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                        (board_.getPosition(r + rowOffset, cc).isOccupied()
                        && (board_.getPosition(r + rowOffset, cc).getPiece().isOwnedByPlayer1() != friendPlayer1));
            }
            if ( !cut ) {
                stack.add( 0, nbr );
                return 1;
            }
        }
        return 0;
    }
    
}
