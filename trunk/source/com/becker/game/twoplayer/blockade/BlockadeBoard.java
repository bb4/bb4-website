package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
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

    /** The number of home bases for each player.  Traditional rules call for 2. */
    public static final int NUM_HOMES = 2;

    /** The percentage away from the players closest edge to place the bases. */
    private static final float HOME_BASE_POSITION_PERCENT = 0.3f;

    /** Home base positions for both players. */
    private BoardPosition[] p1Homes_ = null;
    private BoardPosition[] p2Homes_ = null;


    /**
     * Constructor.
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


    /**
     * Can't change the size of a Blockade board.
     */
    public final void setSize( int numRows, int numCols )
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
     * @return player1's home bases.
     */
    public BoardPosition[] getPlayer1Homes()
    {
        return p1Homes_;
    }

    /**
     * 
     * @return player2's home bases.
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
      *     Moves are only allowed if the candidate position is unoccupied (unless a home base) and if
      * it has not been visited already. The visited part is only significant when we are doing a traversal
      * such as when we are finding the shortest paths to home bases.
      * <pre>
      *       #     There are at most 12 moves from this position
      *     #*#    (some of course may be blocked by walls)
      *   #*O*#    The most common being marked with #'s.
      *     #*#
      *       #
      * </pre>
      *
      * We only add the one space moves if 
      *   1. jumping 2 spaces in that direction would land on an opponent pawn,
      *   2. or moving one space moves lands on an opponent home base.
      *
      * @param position we are moving from
      * @param op1 true if opposing player is player1; false if player2.
      * @return a list of legal piece movements
      */
     public List getPossibleMoveList(BoardPosition position, boolean op1)
     {
         List possibleMoveList = new LinkedList();

         BlockadeBoardPosition pos = (BlockadeBoardPosition)position;
         int fromRow = pos.getRow();
         int fromCol = pos.getCol();
       
         BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, this); 
         BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, this);  
         BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, this); 
         BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, this); 

         boolean eastOpen = !pos.isEastBlocked() && eastPos != null;                  // E
         addIf1HopNeeded(pos, eastOpen, eastPos, fromRow, fromCol, 0, 1, op1, possibleMoveList);

         boolean southOpen = !pos.isSouthBlocked() && southPos != null;           // S
         addIf1HopNeeded(pos, southOpen, southPos, fromRow, fromCol, 1, 0, op1, possibleMoveList); 

         if (southPos != null ) {             
             addIf2HopLegal(pos, southOpen, southPos.isSouthBlocked(),                                 
                                         fromRow, fromCol, fromRow+2, fromCol, op1,  possibleMoveList);    // SS
                                       
             BlockadeBoardPosition southEastPos = pos.getNeighbor(Direction.SOUTH_EAST, this); 
             addIfDiagonalLegal(pos, southEastPos, southOpen && !southPos.isEastBlocked(), eastOpen && !eastPos.isSouthBlocked(), 
                                                          fromRow, fromCol,  op1,  possibleMoveList);                  // SE       
         }
         boolean westOpen = false;
         if (westPos != null)  {             
             BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, this);  
             westOpen = (!westPos.isEastBlocked());                                                                  // W
             addIf1HopNeeded(pos, westOpen, westPos, fromRow, fromCol, 0, -1, op1, possibleMoveList); 
                      
             addIfDiagonalLegal(pos, southWestPos, westOpen && !westPos.isSouthBlocked(), southOpen && !southWestPos.isEastBlocked(), 
                                                          fromRow, fromCol,  op1,  possibleMoveList);                     // SW
         }
         
         BlockadeBoardPosition westWestPos = pos.getNeighbor(Direction.WEST_WEST, this); 
         if (westWestPos != null) {
            addIf2HopLegal(pos, westOpen,  westWestPos.isEastBlocked(),
                                       fromRow, fromCol, fromRow, fromCol-2, op1,  possibleMoveList);    // WW
         }      

         boolean northOpen = false;
         if (northPos != null) {
             BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, this);    
             northOpen = (!northPos.isSouthBlocked()) ;                                                                              // N
             addIf1HopNeeded(pos, northOpen, northPos, fromRow, fromCol, -1, 0, op1, possibleMoveList);              
             addIfDiagonalLegal(pos, northEastPos, northOpen && !northPos.isEastBlocked(), eastOpen && !northEastPos.isSouthBlocked(), 
                                              fromRow, fromCol,  op1,  possibleMoveList);                                             // NE  
         }
         
         BlockadeBoardPosition northNorthPos =   pos.getNeighbor(Direction.NORTH_NORTH, this);  
         if (northNorthPos != null) {             
             addIf2HopLegal(pos, northOpen,  northNorthPos.isSouthBlocked(),                                           // NN
                                         fromRow, fromCol, fromRow-2, fromCol, op1,  possibleMoveList);          
         }

         BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, this); 
         if (northWestPos != null) {
             addIfDiagonalLegal(pos, northWestPos, westOpen && !northWestPos.isSouthBlocked(), northOpen && !northWestPos.isEastBlocked(), 
                                              fromRow, fromCol,  op1,  possibleMoveList);                                              // NW
         }
         
         if (eastPos != null) {
             addIf2HopLegal(pos, eastOpen,  eastPos.isEastBlocked(),                                                           // EE
                                         fromRow, fromCol, fromRow, fromCol+2, op1,  possibleMoveList);    
         }
             
         return possibleMoveList;
     }

     /**
      * Check for needed 1 space moves (4 cases).
      * A one space move is needed if one of 2 conditions arise:
      *   1. jumping 2 spaces in that direction would land on an opponent pawn,
      *   2. or moving one space moves lands on an opponent home base.
      */
     private void addIf1HopNeeded(BlockadeBoardPosition pos, boolean directionOpen, BlockadeBoardPosition dirPosition, 
                                                     int fromRow, int fromCol, int rowOffset, int colOffset, boolean op1, List possibleMoveList) {     
           
             BlockadeBoardPosition dirDirPosition =  (BlockadeBoardPosition) getPosition(fromRow + 2 * rowOffset, fromCol + 2 * colOffset);
             boolean opposingPlayerBlockingPath = 
                     (dirDirPosition!=null && dirDirPosition.getPiece()!=null && dirDirPosition.getPiece().isOwnedByPlayer1() == op1);
             if (directionOpen && !dirPosition.isVisited() && 
                      (opposingPlayerBlockingPath || dirPosition.isHomeBase(op1))) {
                  possibleMoveList.add( 
                          BlockadeMove.createMove(fromRow, fromCol, fromRow + rowOffset, fromCol + colOffset, 0, pos.getPiece(), null));   
                   GameContext.log(2, "ADDED 1 HOP" + dirPosition);
              }                                  
     }
     
     /**
      * Check for 2 space moves (4 cases).
      */
     private void addIf2HopLegal(BlockadeBoardPosition pos, boolean directionOpen, boolean blocked,
                                                     int fromRow, int fromCol, int toRow, int toCol, boolean op1, List possibleMoveList) {
         
         BlockadeBoardPosition dirDirPosition =  (BlockadeBoardPosition) getPosition(toRow, toCol);
         if (directionOpen && (dirDirPosition != null) && !blocked
              && (dirDirPosition.isUnoccupied() || dirDirPosition.isHomeBase(op1)) && !dirDirPosition.isVisited()) {     //DD
               possibleMoveList.add(
                       BlockadeMove.createMove(fromRow, fromCol, toRow, toCol, 0, pos.getPiece(), null));    
         }
     }
     
     /**
      * Check for diagonal moves (4 cases).
      */
     private void addIfDiagonalLegal(BlockadeBoardPosition pos, BlockadeBoardPosition diagonalPos, boolean path1Open, boolean path2Open,  
                                                           int fromRow, int fromCol, boolean op1, List possibleMoveList) {
         
          if (diagonalPos != null) {
              // check the 2 alternative paths to this diagonal position to see if either are clear.
              if ((path1Open || path2Open) &&  (diagonalPos.isUnoccupied() || diagonalPos.isHomeBase(op1) && !diagonalPos.isVisited())) {       //Diag
                         possibleMoveList.add(
                                  BlockadeMove.createMove(fromRow, fromCol, diagonalPos.getRow(), diagonalPos.getCol(), 0, pos.getPiece(), null));    
               }
          }
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
     * Find the shortest paths from the specified position to opponent homes.
     * We use DefaultMutableTreeNodes to represent nodes in the path.
     * If the number of paths returned by this method is less than NUM_HOMES,
     * then there has been an illegal wall pacement, since according to the rules
     * of the game there must always be paths from all pieces to all opponent homes.
     * If a pawn has reached an opponent home then the path length is 0 and that player won.
     * 
     * @param position position to check shortest paths for.
     * @return the NUM_HOMES shortest paths from toPosition.
     */
    public Path[] findShortestPaths( BlockadeBoardPosition position )
    {
        boolean opponentIsPlayer1 = !position.getPiece().isOwnedByPlayer1();
        // set of home bases
        Set<MutableTreeNode> homeSet = new HashSet<MutableTreeNode>();
        // mark position visited so we don't circle back to it.
        position.setVisited(true);

        List<DefaultMutableTreeNode> q = new LinkedList<DefaultMutableTreeNode>();
        // the user object at the root is null, because there is no move there.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
        // if we are sitting on a home, then need to add it to the homeBase set.
        if (position.isHomeBase(opponentIsPlayer1)) {
           homeSet.add(root);
        }
        else {
            q.addAll( findPathChildren(position, root, opponentIsPlayer1) );

            // do a breadth first search until you have spanned/visited all opponent homes.
            while (homeSet.size() < NUM_HOMES && !q.isEmpty()) {
                // pop the next move from the head of the queue.
                DefaultMutableTreeNode node = q.remove(0);
                BlockadeMove nodeMove = (BlockadeMove)node.getUserObject();
                BlockadeBoardPosition toPosition = (BlockadeBoardPosition)getPosition(nodeMove.getToRow(), nodeMove.getToCol());
                if (!toPosition.isVisited()) {
                    toPosition.setVisited(true);
                    MutableTreeNode parentNode = (MutableTreeNode)node.getParent();
                    node.setParent(null);
                    parentNode.insert(node, parentNode.getChildCount());
                    if (toPosition.isHomeBase(opponentIsPlayer1)) {
                        homeSet.add(node);
                        // also add the home base pos to the path
                        //DefaultMutableTreeNode homeNode = new DefaultMutableTreeNode(toPosition);
                        //node.insert(homeNode, node.getChildCount());
                    }
                    List children =  findPathChildren(toPosition, node, opponentIsPlayer1);
                    q.addAll(children);
                }
            }

            // extract the paths by working backwards to the root from the homes.
            if (homeSet.size() < NUM_HOMES)
            {            
                assert false : "We did not find all the homes. Only : "+ homeSet;
                //assert homeSet.size()>0: "not even 1 home found.";
                //confirmAllVisited();            
                // we better have searched all positions looking for the opp homes
            }
        }

        Path[] paths = extractPaths(homeSet);        
        
        // return everything to an unvisted state.
        unvisitAll();
        return paths;
    }
    
    /**
     *There will be a path to each home base from each pawn.
     *@param homeSet set of home base positions.
     */
    private Path[] extractPaths(Set<MutableTreeNode> homeSet) {
        Path[] paths = new Path[homeSet.size()];
        
        Iterator it = homeSet.iterator();
        int ct = 0;
        while (it.hasNext()) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)it.next();
            Path path = new Path(n);            
            paths[ct++] = path;
        }
        return paths;
    }

    /**
     * Check to see if a given wall blocks the move.
     * We assume the move is valid (eg does not move off the board or anything like that).
     * @param move to check
     * @param wall to see if blocking our move.
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
        BlockadeBoardPosition west = start.getNeighbor(Direction.WEST, this); 
        BlockadeBoardPosition north = start.getNeighbor(Direction.NORTH, this); 
        BlockadeBoardPosition south, east;

        switch (move.getDirection()) {
            case NORTH_NORTH :
                BlockadeBoardPosition northNorth = start.getNeighbor(Direction.NORTH_NORTH, this); 
                if (northNorth.isSouthBlocked())
                    blocked = true;
            case NORTH :
                if (north.isSouthBlocked() )
                    blocked = true;
                break;
            case WEST_WEST :
                BlockadeBoardPosition westWest = start.getNeighbor(Direction.WEST_WEST, this); 
                if (westWest.isEastBlocked() )
                    blocked = true;
            case WEST :
                if (west.isEastBlocked())
                    blocked = true;
                break;
            case EAST_EAST :
                east = start.getNeighbor(Direction.EAST, this); 
                if (east.isEastBlocked())
                    blocked = true;
            case EAST :
                if (start.isEastBlocked())
                    blocked = true;
                break;
            case SOUTH_SOUTH :
                south = start.getNeighbor(Direction.SOUTH, this); 
                if (south.isSouthBlocked())
                    blocked = true;
            case SOUTH :
                if (start.isSouthBlocked())
                    blocked = true;
                break;
            case NORTH_WEST :
                BlockadeBoardPosition northWest = start.getNeighbor(Direction.NORTH_WEST, this); 
                if (!((west.isEastOpen() && northWest.isSouthOpen()) ||
                     (north.isSouthOpen() && northWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case NORTH_EAST :
                BlockadeBoardPosition northEast = start.getNeighbor(Direction.NORTH_EAST, this); 
                if (!((start.isEastOpen() && northEast.isSouthOpen()) ||
                     (north.isSouthOpen() && north.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_WEST :
                BlockadeBoardPosition southWest = start.getNeighbor(Direction.SOUTH_WEST, this); 
                if (!((west.isEastOpen() && west.isSouthOpen()) ||
                     (start.isSouthOpen() && southWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_EAST :
                south = start.getNeighbor(Direction.SOUTH, this); 
                east = start.getNeighbor(Direction.EAST, this); 
                if (!((start.isEastOpen() && east.isSouthOpen()) ||
                     (start.isSouthOpen() && south.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
        }

        removeWall(wall);

        return blocked;
    }

    /*
     * It is illegal to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     * @param wall to place. has not been placed yet.
     * @param location where the wall is to be placed.
     * @return an error string if the wall is not a legal placement on the board.
     */
    public String checkLegalWallPlacement(BlockadeWall wall, Location location, GamePiece piece)
    {
        String sError = null;
        assert (wall != null);
        boolean vertical = wall.isVertical();
        Iterator it = wall.getPositions().iterator();
        Map oldWalls = new HashMap();
        BlockadeBoardPosition pos;
        // iterate over the 2 positions covered by the wall.
        while (it.hasNext())  {
            pos = (BlockadeBoardPosition)it.next();
            BlockadeWall candidateWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

            if ((vertical && pos.isEastBlocked()) || (!vertical && pos.isSouthBlocked())) {
                sError = GameContext.getLabel("CANT_OVERLAP_WALLS");
            }
           
            // save the old wall, and temporarily set the candidate wall
            oldWalls.put(pos, candidateWall);
            if (vertical)
                pos.setEastWall(wall);
            else
                pos.setSouthWall(wall);
        }
        
        sError = checkForWallIntersection(wall);
        
        BlockadeBoardPosition p = (BlockadeBoardPosition)getPosition(location);
        if (p == null) {
            return GameContext.getLabel("INVALID_WALL_PLACEMENT");
        }
        
        sError = verifyPathExistence(p, piece);

        // now restore the original walls
        it = wall.getPositions().iterator();
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

    
    /**
     * @return error message if the new wall intersects an old one.
     */
    private String checkForWallIntersection(BlockadeWall wall) 
    {
        String sError = null;
        boolean vertical = wall.isVertical();
         // you cannot intersect one wall with another
         BlockadeBoardPosition pos = wall.getFirstPosition();
         
         BlockadeBoardPosition secondPos = (BlockadeBoardPosition)
                    (vertical?  getPosition(pos.getRow(), pos.getCol()+1)  :  getPosition(pos.getRow()+1, pos.getCol()));
         if ((vertical && pos.isSouthBlocked() && secondPos.isSouthBlocked() )
             || (!vertical && pos.isEastBlocked()) && secondPos.isEastBlocked()) {
             sError = GameContext.getLabel("CANT_INTERSECT_WALLS");
        }
        return sError;
    }

    /**
     * @return error message if no path exists from this position to an opponent home.
     */
    private String verifyPathExistence( BlockadeBoardPosition p, GamePiece piece)
    {
         String sError = null;
        p.setPiece(piece);
        Path paths[] = findShortestPaths(p);
        if (paths.length != NUM_HOMES) {
            sError = GameContext.getLabel("MUST_HAVE_ONE_PATH");
        }
        p.setPiece(null);
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

    /**
     * If this throws an assertion, it means that one or more pieces are 
     * prevented from reaching all the spaces on the board.
     */
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
        // hitting this
        assert allVisited: "not all the positions are visited! unvisted= "+unvisitedList;
    }


    /**
     * Given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move,
     * and then also places a wall somewhere.
     * @return true if the move was made successfully
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


    /**
     * Num different states.
     * There are 12 unique states for a position. 4 ways the walls can be arranged around the position.
     * @return number of different states this position can have.
     */
    public int getNumPositionStates() {
         return 12;
    }

    /**
     * The index of the state for this position.
     * @return The index of the state for tihs position.
     */
    public  int getStateIndex(BoardPosition pos) {
        return ((BlockadeBoardPosition) pos).getStateIndex();
    }
    

    public String toString()
    {
        StringBuffer buf = new StringBuffer(50);
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
