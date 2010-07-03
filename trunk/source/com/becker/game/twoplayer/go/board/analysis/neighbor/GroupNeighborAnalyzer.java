package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.BoardValidator;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;

import java.util.*;

/**
 * Performs static analysis of a go board to determine groups.
 *
 * @author Barry Becker
 */
public class GroupNeighborAnalyzer {

    private GoBoard board_;
    private StringNeighborAnalyzer stringAnalyzer_;
    private BoardValidator validator_;

    /**
     * Constructor
     */
    GroupNeighborAnalyzer(GoBoard board) {
        board_ = board;
        stringAnalyzer_ = new StringNeighborAnalyzer(board);
        validator_ = new BoardValidator(board);
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
     * @return group neighbors for specified stone.
     */
    Set<GoBoardPosition> getGroupNeighbors(GoBoardPosition stone, boolean friendPlayer1, boolean samePlayerOnly)
    {
        List<GoBoardPosition> stack = new LinkedList<GoBoardPosition>();

        pushGroupNeighbors( stone, friendPlayer1, stack, samePlayerOnly );
        Set<GoBoardPosition> nbrStones = new HashSet<GoBoardPosition>();
        nbrStones.addAll( stack );

        return nbrStones;
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
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals are not cut nor 1-space jumps peeped.
     *
     * @param s the position containing a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find groups stones for.
     * @param stack the stack to add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors( GoBoardPosition s, boolean friendPlayer1, List<GoBoardPosition> stack ) {
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
                                    boolean samePlayerOnly ) {
        // start with the nobi string nbrs
        int numPushed = stringAnalyzer_.pushStringNeighbors( s, friendPlayer1, stack, samePlayerOnly );

        // now push the non-nobi group neighbors
        if ( !samePlayerOnly )
            numPushed += pushEnemyDiagonalNeighbors( s, friendPlayer1, stack );

        // we only find pure group neighbors of the same color
        numPushed += pushPureGroupNeighbors( s, friendPlayer1, true, stack );

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
        // atari counts as a cut. Maybe not.
        //if (pos.isInAtari(board_))
        //  return 0;

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
     * determine a set of stones that have group connections to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     * Group connections include nobi, ikken tobi, and kogeima.
     *
     * @param stone the stone to search from for group neighbors.
     * @param returnToUnvisitedState if true, then mark everything unvisited when done.
     * @return the list of stones in the group that was found.
     */
    List<GoBoardPosition> findGroupFromInitialPosition( GoBoardPosition stone, boolean returnToUnvisitedState )
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
                pushGroupNeighbors(s, s.getPiece().isOwnedByPlayer1(), stack );
            }
        }
        if ( returnToUnvisitedState ) {
            GoBoardUtil.unvisitPositions( stones );
            if (GameContext.getDebugMode() > 1) //failing
                validator_.confirmAllUnvisited();
        }
        return stones;
    }

    /**
     *  We allow these connections as long as the diagonal has not been fully cut.
     *  i.e. not an opponent stone on both sides of the cut (or the diag stone is not in atari).
     *
     *  @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
     * @return o or 1 depending on if diagonal neighbor
     */
    private int checkDiagonalNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean sameSideOnly,
                                       List<GoBoardPosition> stack ) {
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if (nbr.isUnoccupied()) {
            return 0;
        }
        // don't add it if it is in atari
        // but this leads to a problem in that ataried stones then don't belong to a group.
        ////if  (nbr.isInAtari(board_)) {
        ////    return 0;
        ////}
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
     * @return return 1 or 0 depending on if there si a onespace neighbor
     */
    private int checkOneSpaceNeighbor( int r, int c, int rowOffset, int colOffset,
                                       boolean friendPlayer1, boolean samePlayerOnly,
                                       List<GoBoardPosition> stack ) {
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
     * @param stack kogeima neighbors, if found, are added to this stack.
     * @return number of kogeima neighbors added.
     */
    private int checkKogeimaNeighbor( int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly,
                                      List<GoBoardPosition> stack ) {
        if ( !board_.inBounds( r + rowOffset, c + colOffset )) {
            return 0;
        }
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        if (nbr.isInAtari(board_)) {
            return 0;
        }

        if ( nbr.isOccupied() &&
            (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited() ) {
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