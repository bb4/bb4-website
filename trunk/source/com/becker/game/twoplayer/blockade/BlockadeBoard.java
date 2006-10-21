package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.common.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

/**
 * Defines the structure of the blockade board and the pieces on it.
 * Each BlockadeBoardPosition can contain a piece and south and east walls.
 *
 * @author Barry Becker
 */
public class BlockadeBoard extends TwoPlayerBoard
{

    // The number of home bases for each player.
    // Traditional rules call for 2.
    public static final int NUM_HOMES = 2;

    // the percentage away from the players closest edge to place the bases.
    private static final float HOME_BASE_POSITION_PERCENT = 0.3f;

    private BoardPosition[] p1Homes_ = null;
    private BoardPosition[] p2Homes_ = null;


    /**
     *   constructor.
     * @param numRows number of rows in the board grid.
     * @param numCols number of rows in the board grid.
     */
    public BlockadeBoard(int numRows, int numCols)
    {
        setSize(numRows, numCols);
    }

    /**
     * reset the board to its initial state.
     */
    public void reset()
    {
        super.reset();
        assert ( positions_!=null );
        int i;
        for ( i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BlockadeBoardPosition( i, j, null, null, null, false, false );
            }
        }
        p1Homes_ = new BlockadeBoardPosition[NUM_HOMES];
        p2Homes_ = new BlockadeBoardPosition[NUM_HOMES];

        // determine the home base positions,
        // and place the players 2 pieces on their respective home bases initially.
        int homeRow1 = getNumRows() - (int) (HOME_BASE_POSITION_PERCENT * getNumRows())+1;
        int homeRow2 = (int) (HOME_BASE_POSITION_PERCENT * getNumRows());
        float increment = (float)(getNumCols())/(NUM_HOMES+1);
        int baseOffset = Math.round(increment);
        for (i=0; i<NUM_HOMES; i++) {
            int c = baseOffset + Math.round(i*increment);
            positions_[homeRow1][c] = new BlockadeBoardPosition( homeRow1, c, null, null, null, true, false);
            positions_[homeRow2][c] = new BlockadeBoardPosition( homeRow2, c, null, null, null, false, true);
            p1Homes_[i] = positions_[homeRow1][c];
            p1Homes_[i].setPiece(new GamePiece(true));
            p2Homes_[i] = positions_[homeRow2][c];
            p2Homes_[i].setPiece(new GamePiece(false));
        }
    }


    // can't change the size of a Blockade board
    public void setSize( int numRows, int numCols )
    {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        // we don't use the 0 edges of the board
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }

    /**
     * If the Blockade game has more than this many moves, then we assume it is a draw.
     * We make this number big, because in blockade it is impossible to have a draw.
     * I haven't proved it, but I think it is impossible for the number of moves to exceed
     * the rows*cols.
     * @return assumed maximum number of moves.
     */
    public int getMaxNumMoves()
    {
        return Integer.MAX_VALUE;
    }

    /**
     * @return plyer1's home bases.
     */
    public BoardPosition[] getPlayer1Homes()
    {
        return p1Homes_;
    }

    /**
     * @return plyer2's home bases.
     */
    public BoardPosition[] getPlayer2Homes()
    {
        return p2Homes_;
    }

    /**
     * @return typical number of moves in a go game.
     */
    public int getTypicalNumMoves() {
        return rowsTimesCols_;
    }

    /**
      * Blockade pieces can move 1 or 2 spaces in any direction.
      * However, only in rare cases would you ever want to move only 1 space.
      * For example, move 1 space to land on a home base, or in preparation to jump an oppponent piece.
      * They may jump over opponent pieces that are in the way (but they do not capture it).
      * The wall is ignored for the purposed of this method.
      *   Moves are only allowed if the candidate position is unoccupied (unless a home base) and if
      * it has not been visited already. The visited part is only significant when we are doing a traversal
      * such as when we are finding the shortest paths to home bases.
      * <pre>
      *     #     There are at most 12 moves from this position
      *    #*#    (some of course may be blocked by walls.)
      *   #*O*#    The most common being marked with #'s.
      *    #*#
      *     #
      * </pre>
      *  @@ there should be a way to simplify this method by calling separate addIfLegal method for the 12 cases.
      *
      * @param position
      * @param op1 true if opposing player is player1; false if player2.
      * @return a list of legal piece movements
      */
     public List getPossibleMoveList(BoardPosition position, boolean op1)
     {
         List possibleMoveList = new LinkedList();

         BlockadeBoardPosition p = (BlockadeBoardPosition)position;
         int fromRow = p.getRow();
         int fromCol = p.getCol();
         int numRows = getNumRows();
         int numCols = getNumCols();
         boolean southOpen = false;
         boolean eastOpen = false;

         BlockadeBoardPosition southPos = (BlockadeBoardPosition) getPosition(fromRow+1, fromCol);
         BlockadeBoardPosition westPos = (BlockadeBoardPosition) getPosition(fromRow, fromCol-1);
         BlockadeBoardPosition northPos = (BlockadeBoardPosition) getPosition(fromRow-1, fromCol);
         BlockadeBoardPosition eastPos = (BlockadeBoardPosition) getPosition(fromRow, fromCol+1);

         if (!p.isEastBlocked() && fromCol+1<=numCols ) {     // E
             eastOpen = true;
             if (!eastPos.isVisited() && (eastPos.isUnoccupied() || eastPos.isHomeBase(op1)))
                 possibleMoveList.add(
                         BlockadeMove.createMove(fromRow, fromCol, fromRow, fromCol+1, 0, p.getPiece(), null));
         }

         if (!p.isSouthBlocked() && fromRow+1<=numRows) {     // S
             southOpen = true;
             if (!southPos.isVisited() && (southPos.isUnoccupied() || southPos.isHomeBase(op1)))
                 possibleMoveList.add(
                         BlockadeMove.createMove(fromRow, fromCol, fromRow+1,fromCol, 0, p.getPiece(), null));
         }

         if (southPos != null ) {
             BlockadeBoardPosition southSouthPos = (BlockadeBoardPosition) getPosition(fromRow+2, fromCol);
             if (southOpen && !southPos.isSouthBlocked() && fromRow+2<=numRows &&
                  (southSouthPos.isUnoccupied()||southPos.isHomeBase(op1)) && !southSouthPos.isVisited())  // SS
                  possibleMoveList.add(
                          BlockadeMove.createMove(fromRow, fromCol, fromRow+2,fromCol, 0, p.getPiece(), null));
             if ((southOpen && !southPos.isEastBlocked() && fromCol+1<=numCols) ||
                 (eastOpen && !eastPos.isSouthBlocked())) {                                             // SE
                  BlockadeBoardPosition southEastPos = (BlockadeBoardPosition) getPosition(fromRow+1, fromCol+1);
                  if ((southEastPos.isUnoccupied()||southEastPos.isHomeBase(op1)) && !southEastPos.isVisited())
                    possibleMoveList.add(
                            BlockadeMove.createMove(fromRow, fromCol, fromRow+1,fromCol+1, 0, p.getPiece(), null));
             }
         }
         boolean westOpen = false;
         if (westPos != null)  {
             BlockadeBoardPosition southWestPos = (BlockadeBoardPosition) getPosition(fromRow+1, fromCol-1);
             if (!westPos.isEastBlocked() && fromCol-1>0 ) {   // W
                 westOpen = true;
                 if  (!westPos.isVisited() && (westPos.isUnoccupied()||westPos.isHomeBase(op1)))
                     possibleMoveList.add(
                             BlockadeMove.createMove(fromRow, fromCol, fromRow,fromCol-1, 0, p.getPiece(), null));
             }
             boolean test1 = (westOpen && !westPos.isSouthBlocked() && fromCol-1>0 && fromRow+1<=numRows);
             if (test1 || ((southOpen && southWestPos!=null && !southWestPos.isEastBlocked()))
                  && (southWestPos.isUnoccupied()||southWestPos.isHomeBase(op1)) && !southWestPos.isVisited())   // SW
                  possibleMoveList.add(
                          BlockadeMove.createMove(fromRow, fromCol, fromRow+1,fromCol-1, 0, p.getPiece(), null));
         }

         BlockadeBoardPosition westWestPos = (BlockadeBoardPosition) getPosition(fromRow, fromCol-2);
         if (westOpen && westWestPos!=null && !westWestPos.isEastBlocked()
              && (westWestPos.isUnoccupied()||westWestPos.isHomeBase(op1)) && !westWestPos.isVisited())          // WW
              possibleMoveList.add(
                      BlockadeMove.createMove(fromRow, fromCol, fromRow,fromCol-2, 0, p.getPiece(), null));

         boolean northOpen = false;
         if (northPos!=null) {
             BlockadeBoardPosition northEastPos = (BlockadeBoardPosition) getPosition(fromRow-1, fromCol+1);
             if (!northPos.isSouthBlocked())  {       // N
                 northOpen = true;
                 if (!northPos.isVisited() && (northPos.isUnoccupied()||northPos.isHomeBase(op1)))
                     possibleMoveList.add(
                             BlockadeMove.createMove(fromRow, fromCol, fromRow-1,fromCol, 0, p.getPiece(), null));

             }
             boolean test1 = (northOpen && !northPos.isEastBlocked() && fromCol+1<=numCols);
             if ((test1 || (eastOpen && !northEastPos.isSouthBlocked()))
                  && (northEastPos.isUnoccupied()||northEastPos.isHomeBase(op1)) && !northEastPos.isVisited())    // NE
                  possibleMoveList.add(
                          BlockadeMove.createMove(fromRow, fromCol, fromRow-1,fromCol+1, 0, p.getPiece(), null));
         }

         BlockadeBoardPosition northNorthPos = (BlockadeBoardPosition) getPosition(fromRow-2, fromCol);
         if (northOpen && northNorthPos!=null && !northNorthPos.isSouthBlocked()
              && (northNorthPos.isUnoccupied()||northNorthPos.isHomeBase(op1)) && !northNorthPos.isVisited())    // NN
              possibleMoveList.add(
                      BlockadeMove.createMove(fromRow, fromCol, fromRow-2,fromCol, 0, p.getPiece(), null));

         BlockadeBoardPosition northWestPos = (BlockadeBoardPosition) getPosition(fromRow-1, fromCol-1);
         if (northWestPos!=null) {
             boolean test1 = ((westOpen && !northWestPos.isSouthBlocked()) ||
                              (northOpen && !northWestPos.isEastBlocked()));
             if (test1 && (northWestPos.isUnoccupied()||northWestPos.isHomeBase(op1)) && !northWestPos.isVisited())  // NW
                  possibleMoveList.add(
                          BlockadeMove.createMove(fromRow, fromCol, fromRow-1,fromCol-1, 0, p.getPiece(), null));
         }
         BlockadeBoardPosition eastEastPos = (BlockadeBoardPosition) getPosition(fromRow, fromCol+2);
         boolean test1 = eastOpen && eastPos!=null && !eastPos.isEastBlocked() && fromCol+2<=numCols;
         if (test1 && (eastEastPos.isUnoccupied()||eastEastPos.isHomeBase(op1)) && !eastEastPos.isVisited())       // EE
              possibleMoveList.add(
                      BlockadeMove.createMove(fromRow, fromCol, fromRow,fromCol+2, 0, p.getPiece(), null));

         return possibleMoveList;
     }


    /**
     * @param pos the place we are moving from.
     * @param parent the parent node for the child moves
     * @return a list of TreeNodes containing all the moves that lead to unvisited positions.
     */
    private List findPathChildren(BoardPosition pos, MutableTreeNode parent, boolean oppPlayer1)
    {
        List moves = getPossibleMoveList(pos, oppPlayer1);
        List children = new ArrayList();
        Iterator it = moves.iterator();
        while (it.hasNext()) {
            BlockadeMove m = (BlockadeMove)it.next();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(m);
            childNode.setParent(parent);
            children.add(childNode);
        }
        return children;
    }

    /**
     * Find the shortest paths from the specified pos to opponent homes.
     * We use DefaultMutableTreeNodes to represent nodes in the path.
     * If the number of paths returned by this method is less than NUM_HOMES,
     * then there has been an illegal wall pacement, since according to the rules
     * of the game there must always be paths from all pieces to all opponent homes.
     * If a pawn has reached an opponent home then the path length is 0 and that player won.
     *
     * @param pos position to check shortest paths for.
     * @return  the NUM_HOMES shortest paths from p.
     */
    public List[] findShortestPaths( BlockadeBoardPosition pos )
    {
        boolean oppPlayer1 = !pos.getPiece().isOwnedByPlayer1();
        Set homeSet = new HashSet();
        // mark pos visited so we don't circle back to it.
        pos.setVisited(true);

        List q = new LinkedList();
        // the user object at the root is null, because there is no move there.
        MutableTreeNode root = new DefaultMutableTreeNode(null);
        // if we are sitting on a home, then need to add it to the homeBase set.
        if (pos.isHomeBase(oppPlayer1)) {
           homeSet.add(root);
        }
        q.addAll( findPathChildren(pos, root, oppPlayer1) );

        // do a breadth first search until you have spanned/visited all opponent homes.
        while (homeSet.size() < NUM_HOMES && !q.isEmpty()) {
            // pop the next move from the head of the queue.
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)q.remove(0);
            BlockadeMove m = (BlockadeMove)n.getUserObject();
            BlockadeBoardPosition p = (BlockadeBoardPosition)getPosition(m.getToRow(), m.getToCol());
            if (!p.isVisited()) {
                p.setVisited(true);
                MutableTreeNode parentNode = (MutableTreeNode)n.getParent();
                n.setParent(null);
                parentNode.insert(n, parentNode.getChildCount());
                if (p.isHomeBase(oppPlayer1)) {
                    homeSet.add(n);
                }
                q.addAll( findPathChildren(p, n, oppPlayer1) );
            }
        }

        // extract the paths by working backwards to the root from the homes.
        if (homeSet.size() < NUM_HOMES)
        {
            assert homeSet.size()>0: "not even 1 home found.";
            confirmAllVisited();
            // we better have searched all positions looking for the opp homes
        }
        List[] paths = new LinkedList[homeSet.size()];
        Iterator it = homeSet.iterator();
        int ct=0;
        while (it.hasNext()) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)it.next();
            Object[] ps = n.getUserObjectPath();
            List path = new LinkedList();
            if (ps.length>1)  {
                // skip the first null move.
                for (int k=1; k<ps.length; k++) {
                    path.add(ps[k]); //addFirst(ps[k]);
                }
            }
            paths[ct++] = path;
        }

        // return everything to an unvisted state.
        unvisitAll();
        return paths;
    }

    /**
     * Check to see if a given wall blocks the move.
     * We assume the move is valid (eg does not move off the board or anything like that).
     * @param move
     * @param wall
     * @return  true if the wall blocks this move.
     */
    public boolean isMoveBlockedByWall( BlockadeMove move,  BlockadeWall wall)
    {
        // we temporarily place the wall.
        // We assume that this wall does not interfere with other walls as that would be invalid.

        // temporarily place the wall
        addWall(wall);
        boolean blocked = false;

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoardPosition start = (BlockadeBoardPosition)getPosition(fromRow, fromCol);
        BlockadeBoardPosition north = (BlockadeBoardPosition)getPosition(fromRow-1, fromCol);
        BlockadeBoardPosition west = (BlockadeBoardPosition)getPosition(fromRow, fromCol-1);
        BlockadeBoardPosition south, east;

        switch (move.getDirection()) {
            case BlockadeMove.NORTH_NORTH :
                BlockadeBoardPosition northNorth = (BlockadeBoardPosition)getPosition(fromRow-2, fromCol);
                if (northNorth.isSouthBlocked())
                    blocked = true;
            case BlockadeMove.NORTH :
                if (north.isSouthBlocked() )
                    blocked = true;
                break;
            case BlockadeMove.WEST_WEST :
                BlockadeBoardPosition westWest = (BlockadeBoardPosition)getPosition(fromRow, fromCol-2);
                if (westWest.isEastBlocked() )
                    blocked = true;
            case BlockadeMove.WEST :
                if (west.isEastBlocked())
                    blocked = true;
                break;
            case BlockadeMove.EAST_EAST :
                east = (BlockadeBoardPosition)getPosition(fromRow, fromCol+1);
                if (east.isEastBlocked())
                    blocked = true;
            case BlockadeMove.EAST :
                if (start.isEastBlocked())
                    blocked = true;
                break;
            case BlockadeMove.SOUTH_SOUTH :
                south = (BlockadeBoardPosition)getPosition(fromRow+1, fromCol);
                if (south.isSouthBlocked())
                    blocked = true;
            case BlockadeMove.SOUTH :
                if (start.isSouthBlocked())
                    blocked = true;
                break;
            case BlockadeMove.NORTH_WEST :
                BlockadeBoardPosition northWest = (BlockadeBoardPosition)getPosition(fromRow-1, fromCol-1);
                if (!((west.isEastOpen() && northWest.isSouthOpen()) ||
                     (north.isSouthOpen() && northWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case BlockadeMove.NORTH_EAST :
                BlockadeBoardPosition northEast = (BlockadeBoardPosition)getPosition(fromRow-1, fromCol+1);
                if (!((start.isEastOpen() && northEast.isSouthOpen()) ||
                     (north.isSouthOpen() && north.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case BlockadeMove.SOUTH_WEST :
                BlockadeBoardPosition southWest = (BlockadeBoardPosition)getPosition(fromRow+1, fromCol-1);
                if (!((west.isEastOpen() && west.isSouthOpen()) ||
                     (start.isSouthOpen() && southWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case BlockadeMove.SOUTH_EAST :
                south = (BlockadeBoardPosition)getPosition(fromRow+1, fromCol);
                east = (BlockadeBoardPosition)getPosition(fromRow, fromCol+1);
                if (!((start.isEastOpen() && east.isSouthOpen()) ||
                     (start.isSouthOpen() && south.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            default : assert false : "Invalid direction : "+move.getDirection();
        }

        removeWall(wall);

        return blocked;
    }

    /*
     * It is illegal to place a wall at a position that overlaps
     * or intersects another wall (@@), or if the wall prevents one of the pawns from reaching an
     * opponent home.
     * @return an error string if the wall is not a legal placement on the board.
     */
    public String checkLegalWallPlacement(BlockadeWall wall, Location loc, GamePiece piece)
    {
        String sError = null;
        assert (wall!=null);
        boolean vertical = wall.isVertical();
        Set hsPositions = wall.getPositions();
        Iterator it = hsPositions.iterator();
        Map oldWalls = new HashMap();
        BlockadeBoardPosition pos;
        while (it.hasNext())  {
            pos = (BlockadeBoardPosition)it.next();
            BlockadeWall cWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

            if ((vertical && pos.getEastWall()!=null) || (!vertical && pos.getSouthWall()!=null))
                sError = GameContext.getLabel("CANT_OVERLAP_WALLS");
            // you cannot intersect one wall with another

            // save the old and temporarily set the candidate wall
            oldWalls.put(pos, cWall);
            if (vertical)
                pos.setEastWall(wall);
            else
                pos.setSouthWall(wall);
        }

        BlockadeBoardPosition p = (BlockadeBoardPosition)getPosition(loc);
        if (p==null)
            return "INVALID_WALL_PLACEMENT"; //GameContext.getLabel("INVALID_WALL_PLACEMENT");
        p.setPiece(piece);
        List paths[] = findShortestPaths(p);
        if (paths.length != NUM_HOMES)
            sError = GameContext.getLabel("MUST_HAVE_ONE_PATH");
        p.setPiece(null);

        // now restore the original walls
        it = hsPositions.iterator();
        while (it.hasNext())  {
            pos = (BlockadeBoardPosition)it.next();
            BlockadeWall origWall = (BlockadeWall)oldWalls.get(pos);
            if (vertical)
                pos.setEastWall(origWall);
            else
                pos.setSouthWall(origWall);
        }
        return sError;
    }


    public void addWall(BlockadeWall wall)
    {
        showWall(wall, true);
    }

    public void removeWall(BlockadeWall wall)
    {
        showWall(wall, false);
    }

    /**
     * shows or hides this wall on the game board.
     * @param show whether to show or hide the wall.
     */
    private void showWall(BlockadeWall wall, boolean show)
    {
        Set positions  = wall.getPositions();
        if (!positions.isEmpty())
           assert (positions.size()==2): "positions="+positions;
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            // since p may be from a different board, we need to make sure that we set the
            // wall for this board.
            BlockadeBoardPosition p = (BlockadeBoardPosition)it.next();
            BlockadeBoardPosition pp = (BlockadeBoardPosition)getPosition(p.getRow(), p.getCol());
            if (wall.isVertical()) {
                pp.setEastWall(show ? wall : null);
            }
            else  {
                pp.setSouthWall(show ? wall : null);
            }
        }
    }

    private void unvisitAll()
    {
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                ((BlockadeBoardPosition)positions_[i][j]).setVisited(false);
            }
        }
    }

    private void confirmAllVisited()
    {
        boolean allVisited = true;
        List unvisitedList = new ArrayList();
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                if (!((BlockadeBoardPosition)positions_[i][j]).isVisited())   {
                    allVisited = false;
                    unvisitedList.add(positions_[i][j]);
                }
            }
        }
        assert allVisited: "not all the positions are visited! unvisted= "+unvisitedList;
    }


    /**
     * Given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move,
     * and then also places a wall somewhere.
     */
    protected boolean makeInternalMove( Move move )
    {
        BlockadeMove m = (BlockadeMove) move;
        positions_[m.getToRow()][m.getToCol()].setPiece(m.getPiece());

        // we also need to place a wall.
        if (m.getWall() != null)
            addWall(m.getWall());
        positions_[m.getFromRow()][m.getFromCol()].clear();

        return true;
    }

    /**
     * for Blockade, undoing a move means moving the piece back and
     * restoring any captures.
     */
    protected void undoInternalMove( Move move )
    {
        BlockadeMove m = (BlockadeMove) move;
        BoardPosition startPos = positions_[m.getFromRow()][m.getFromCol()];
        startPos.setPiece( m.getPiece() );
        positions_[m.getToRow()][m.getToCol()].clear();

        // remove the wall that was placed by this move.
        if (m.getWall()!=null)
            removeWall(m.getWall());
    }


    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        // print just the walls
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
               BlockadeBoardPosition pos = ((BlockadeBoardPosition)positions_[i][j]);
               if (pos.getEastWall()!=null)
                   buf.append("East wall at: "+i+' '+j+'\n');
               if (pos.getSouthWall()!=null)
                   buf.append("South wall at: "+i+' '+j+'\n');

            }
        }
        return buf.toString();
    }

}
