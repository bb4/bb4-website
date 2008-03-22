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
                positions_[i][j] = new BlockadeBoardPosition( i, j);
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
     public List<BlockadeMove> getPossibleMoveList(BoardPosition position, boolean op1)
     {
         List<BlockadeMove> possibleMoveList = new LinkedList<BlockadeMove>();

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
             addIfDiagonalLegal(pos, southEastPos, eastOpen && !eastPos.isSouthBlocked(),  southOpen && !southPos.isEastBlocked(),
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
             addIfDiagonalLegal(pos, northEastPos, eastOpen && !northEastPos.isSouthBlocked(), northOpen && !northPos.isEastBlocked(),  
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
             // if either the players own pawn or that of the opponent is blocking the path, then true
             boolean pawnBlockingPath = 
                     (dirDirPosition!=null && dirDirPosition.getPiece()!=null );
             if (directionOpen && !dirPosition.isVisited() && 
                      (pawnBlockingPath || dirPosition.isHomeBase(op1))) {
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
      * In some rare cases we may add 1 space moves if the diagonal is occupied by another pawn.
      */
     private void addIfDiagonalLegal(BlockadeBoardPosition pos, BlockadeBoardPosition diagonalPos, 
                                                           boolean horizontalPathOpen, boolean verticalPathOpen,  
                                                           int fromRow, int fromCol, boolean op1, List possibleMoveList) {
         
          if (diagonalPos == null)
              return;

          // check the 2 alternative paths to this diagonal position to see if either are clear.
          if ((horizontalPathOpen || verticalPathOpen) &&  (diagonalPos.isUnoccupied() || diagonalPos.isHomeBase(op1) && !diagonalPos.isVisited())) {       //Diag
                     possibleMoveList.add(
                              BlockadeMove.createMove(fromRow, fromCol, diagonalPos.getRow(), diagonalPos.getCol(), 0, pos.getPiece(), null));    
          }
          else if (diagonalPos.isOccupied()) {
              // if the diagonal position that we want to move to is occupied, we try to add the 1 space moves
              BlockadeBoardPosition horzPos = (BlockadeBoardPosition) getPosition(fromRow, diagonalPos.getCol());
              BlockadeBoardPosition vertPos = (BlockadeBoardPosition) getPosition(diagonalPos.getRow(), fromCol);

              if (horizontalPathOpen && horzPos.isUnoccupied() && !horzPos.isVisited()) {
                   possibleMoveList.add(
                              BlockadeMove.createMove(fromRow, fromCol, fromRow, diagonalPos.getCol(), 0, pos.getPiece(), null));    
              } 
              if (verticalPathOpen && vertPos.isUnoccupied() && !vertPos.isVisited()) {
                   possibleMoveList.add(
                              BlockadeMove.createMove(fromRow, fromCol, diagonalPos.getRow(), fromCol, 0, pos.getPiece(), null));    
              }
          }
     }
     
     
    /**
     * @param player1 the last player to make a move.
     * @return all the opponent's shortest paths to your home bases.
     */
    public List<Path>  findAllOpponentShortestPaths(boolean player1) {

        int numShortestPaths = NUM_HOMES * NUM_HOMES;
        List<Path> opponentPaths = new LinkedList<Path>();
        Set hsPawns = new LinkedHashSet();
        for ( int row = 1; row <= getNumRows(); row++ ) {
            for ( int col = 1; col <= getNumCols(); col++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition) getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() != player1 ) {
                    hsPawns.add(pos);
                    assert (hsPawns.size() <= NUM_HOMES) : "Error: too many opponent pieces: " + hsPawns ;                     
                    List<Path> paths = findShortestPaths(pos);
                    GameContext.log(2,
                        "about to add "+ paths.size() +" more paths to "+opponentPaths.size()+" maxAllowed="+ numShortestPaths);
                    for (Path p: paths) {
                        opponentPaths.add(p);
                    }         
                }
            }
        }
        //assert (opponentPaths.size() == numShortestPaths) : "Too few opponent paths:"+ opponentPaths;
        return opponentPaths;
    }
     

    /**
     * @param pos the place we are moving from.
     * @param parent the parent node for the child moves
     * @return a list of TreeNodes containing all the moves that lead to unvisited positions.
     */
    private List findPathChildren(BoardPosition pos, MutableTreeNode parent, boolean oppPlayer1)
    {
        List<BlockadeMove> moves = getPossibleMoveList(pos, oppPlayer1);
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
    public List<Path> findShortestPaths( BlockadeBoardPosition position ) 
    {
        boolean opponentIsPlayer1 = !position.getPiece().isOwnedByPlayer1();
        // set of home bases
        // use a LinkedHashMap so the iteration order is predictable.
        Set<MutableTreeNode> homeSet = new LinkedHashSet<MutableTreeNode>();
        // mark position visited so we don't circle back to it.
        position.setVisited(true);

        List<DefaultMutableTreeNode> q = new LinkedList<DefaultMutableTreeNode>();
        // the user object at the root is null, because there is no move there.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
        // if we are sitting on a home, then need to add it to the homeBase set.
        if (position.isHomeBase(opponentIsPlayer1)) {
           homeSet.add(root);
        }
      
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
                }
                List children =  findPathChildren(toPosition, node, opponentIsPlayer1);
                q.addAll(children);
            }
        }

       
        // extract the paths by working backwards to the root from the homes.
        List<Path> paths = extractPaths(homeSet);   
        
         if (!position.isHomeBase(opponentIsPlayer1)) {
             // this could happen, but it should be rare.
             if (paths.size() < BlockadeBoard.NUM_HOMES) 
                 GameContext.log(0, "Too few paths:  "+paths.size()+" != "+BlockadeBoard.NUM_HOMES 
                         +" found for pos="+position+". The paths were :"+paths);
         }
        
        // return everything to an unvisted state.
        unvisitAll();
        return paths;
    }
    
    /**
     *There will be a path to each home base from each pawn.
     * Extract the paths by working backwards to the root from the homes.
     *@param homeSet set of home base positions.
     */
    private List<Path> extractPaths(Set<MutableTreeNode> homeSet) {
        List<Path> paths = new ArrayList<Path>(homeSet.size());
        
        Iterator it = homeSet.iterator();
        int ct = 0;
        while (it.hasNext()) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)it.next();
            Path path = new Path(n); 
            // if the path is not > 0 then then pawn is own the homeBase and the game has been won.
            if (path.getLength() == 0) {
                GameContext.log(0, "found 0 length path ="+path);
            }
            paths.add(path);
        }
        return paths;
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
        
        Map<BlockadeBoardPosition, BlockadeWall> oldWalls = new HashMap<BlockadeBoardPosition, BlockadeWall>();
        
        // iterate over the 2 positions covered by the wall.
        for (BlockadeBoardPosition pos: wall.getPositions())  {
            BlockadeWall origWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

            if (sError==null  && ((vertical && pos.isEastBlocked()) || (!vertical && pos.isSouthBlocked()))) {
                sError = GameContext.getLabel("CANT_OVERLAP_WALLS");
            }
           
            // save the old wall, and temporarily set the candidate wall
            oldWalls.put(pos, origWall);
            if (vertical)
                pos.setEastWall(wall);
            else
                pos.setSouthWall(wall);
        }
        
        BlockadeBoardPosition p = (BlockadeBoardPosition) getPosition(location);
        if (sError==null && hasWallIntersection(wall)) {
             sError = GameContext.getLabel("CANT_INTERSECT_WALLS");
        }        
        else if (sError==null && p == null) {
            sError = GameContext.getLabel("INVALID_WALL_PLACEMENT");
        }        
        else if (sError==null && missingPath()) {
            sError = GameContext.getLabel("MUST_HAVE_ONE_PATH");
        }

       // now restore the original walls
       for (BlockadeBoardPosition pos: wall.getPositions())  { 
            BlockadeWall origWall = oldWalls.get(pos);
            if (vertical)
                pos.setEastWall(origWall);
            else
                pos.setSouthWall(origWall);
        }
        return sError;
    }
    
    /**
     * find all the paths from each player's pawn to each opponent base.
     *@param lastMove last move made
     */
    public PlayerPathLengths findPlayerPathLengths(BlockadeMove lastMove) {
        PlayerPathLengths playerPaths = new PlayerPathLengths();
        
        for ( int row = 1; row <= getNumRows(); row++ ) {   
            for ( int col = 1; col <= getNumCols(); col++ ) { 
                BlockadeBoardPosition pos = (BlockadeBoardPosition)getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();
                  
                    BlockadeWall wall = ((BlockadeMove) lastMove).getWall();
                                                          
                    if (!pos.isHomeBase(!piece.isOwnedByPlayer1())) {
                        // should reuse cached path if still valid.
                        List<Path> paths = pos.findShortestPaths(this, wall);     
                        
                        playerPaths.getPathLengthsForPlayer(piece.isOwnedByPlayer1()).updatePathLengths(paths);
                    } 
                    if (!playerPaths.isValid()) {
                        GameContext.log(1, "We tried, but could not find a path from " + pos + " to all bases because of the placement of "+ wall);
                    }
                }
            }
        }
        return playerPaths;
    }

    
    /**
     * @return error message if the new wall intersects an old one.
     */
    private boolean hasWallIntersection(BlockadeWall wall) 
    {
         boolean vertical = wall.isVertical();
         // you cannot intersect one wall with another
         BlockadeBoardPosition pos = wall.getFirstPosition();
         
         BlockadeBoardPosition secondPos = (BlockadeBoardPosition)
                    (vertical?  getPosition(pos.getRow(), pos.getCol()+1)  :  getPosition(pos.getRow()+1, pos.getCol()));
         if ((vertical && pos.isSouthBlocked() && secondPos.isSouthBlocked() )
             || (!vertical && pos.isEastBlocked()) && secondPos.isEastBlocked()) {
                return true;
        }
        return false;
    }

    /**
     * @return error message if no path exists from this position to an opponent home.
     */
    private boolean missingPath()
    {
        List<Path> paths1 = findAllOpponentShortestPaths(true);  
        List<Path> paths2 = findAllOpponentShortestPaths(false);    
        System.out.println("paths1.length="+paths1.size()+" paths2.length ="+paths2.size() );
        
        int expectedNumPaths = NUM_HOMES*NUM_HOMES;
        return  (paths1.size() < expectedNumPaths || paths2.size() <expectedNumPaths );      
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
