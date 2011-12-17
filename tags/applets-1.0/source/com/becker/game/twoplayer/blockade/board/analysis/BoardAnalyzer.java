/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade.board.analysis;

import com.becker.common.geometry.Location;
import com.becker.game.common.GameContext;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.blockade.board.BlockadeBoard;
import com.becker.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.board.Path;
import com.becker.game.twoplayer.blockade.board.PlayerPathLengths;
import com.becker.game.twoplayer.blockade.board.analysis.PossibleMoveAnalyzer;
import com.becker.game.twoplayer.blockade.board.move.BlockadeMove;
import com.becker.game.twoplayer.blockade.board.move.BlockadeWall;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

/**
 * Analyzes the blockade board.
 *
 * @author Barry Becker
 */
public class BoardAnalyzer {

    private BlockadeBoard board;

    /**
     * Constructor.
     */
    public BoardAnalyzer(BlockadeBoard board) {
        this.board = board;
    }

    /**
      * @see PossibleMoveAnalyzer
      */
     public List<BlockadeMove> getPossibleMoveList(BoardPosition position, boolean op1) {
         PossibleMoveAnalyzer moveAnalyzer = new PossibleMoveAnalyzer(board, position, op1);
         return moveAnalyzer.getPossibleMoveList();
     }


    /**
     * @param player1 the last player to make a move.
     * @return all the opponent's shortest paths to your home bases.
     */
    public List<Path> findAllOpponentShortestPaths(boolean player1) {

        int numShortestPaths = BlockadeBoard.NUM_HOMES * BlockadeBoard.NUM_HOMES;
        List<Path> opponentPaths = new LinkedList<Path>();
        Set<BlockadeBoardPosition> hsPawns = new LinkedHashSet<BlockadeBoardPosition>();
        for ( int row = 1; row <= board.getNumRows(); row++ ) {
            for ( int col = 1; col <= board.getNumCols(); col++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition) board.getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() != player1 ) {
                    hsPawns.add(pos);
                    assert (hsPawns.size() <= BlockadeBoard.NUM_HOMES) : "Error: too many opponent pieces: " + hsPawns ;
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
     * Find moves going to unvisited positions.
     * @param pos the place we are moving from.
     * @param parent the parent node for the child moves
     * @param oppPlayer1 the opposing player (opposite of pies at pos).
     * @return a list of TreeNodes containing all the moves that lead to unvisited positions.
     */
    private List<DefaultMutableTreeNode> findPathChildren(BoardPosition pos,
                                                      MutableTreeNode parent, boolean oppPlayer1) {
        List<BlockadeMove> moves = getPossibleMoveList(pos, oppPlayer1);
        List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
        for (BlockadeMove move : moves) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(move);
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
     * If a pawn has reached an opponent home then the path magnitude is 0 and that player won.
     * 
     * @param position position to check shortest paths for.
     * @return the NUM_HOMES shortest paths from toPosition.
     */
    public List<Path> findShortestPaths( BlockadeBoardPosition position )  {

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
        else
        {
            q.addAll( findPathChildren(position, root, opponentIsPlayer1) );

            // do a breadth first search until you have spanned/visited all opponent homes.
            while (homeSet.size() < BlockadeBoard.NUM_HOMES && !q.isEmpty()) {
                // pop the next move from the head of the queue.
                DefaultMutableTreeNode node = q.remove(0);
                BlockadeMove nodeMove = (BlockadeMove)node.getUserObject();
                BlockadeBoardPosition toPosition =
                        (BlockadeBoardPosition) board.getPosition(nodeMove.getToRow(), nodeMove.getToCol());
                if (!toPosition.isVisited()) {
                    toPosition.setVisited(true);
                    MutableTreeNode parentNode = (MutableTreeNode)node.getParent();
                    node.setParent(null);
                    parentNode.insert(node, parentNode.getChildCount());
                    if (toPosition.isHomeBase(opponentIsPlayer1)) {
                        homeSet.add(node);
                    }
                    List<DefaultMutableTreeNode> children =  findPathChildren(toPosition, node, opponentIsPlayer1);
                    q.addAll(children);
                }
            }
        }
        // extract the paths by working backwards to the root from the homes.
        List<Path> paths = extractPaths(homeSet);   

        unvisitAll();
        return paths;
    }


    /**
     * return everything to an unvisited state.
     */
    private void unvisitAll() {
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                ((BlockadeBoardPosition)board.getPosition(i, j)).setVisited(false);
            }
        }
    }

    /**
     * There will be a path to each home base from each pawn.
     * Extract the paths by working backwards to the root from the homes.
     * @param homeSet set of home base positions.
     * @return extracted paths
     */
    private List<Path> extractPaths(Set<MutableTreeNode> homeSet) {
        List<Path> paths = new ArrayList<Path>(homeSet.size());
        
        for (MutableTreeNode home : homeSet) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)home;
            Path path = new Path(n); 
            // if the path is not > 0 then then pawn is on the homeBase and the game has been won.
            if (path.getLength() == 0) {
                GameContext.log(2, "found 0 magnitude path =" + path +" for home "  + n);
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
    public String checkLegalWallPlacement(BlockadeWall wall, Location location) {
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
        
        BlockadeBoardPosition p = (BlockadeBoardPosition) board.getPosition(location);
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
        
        for ( int row = 1; row <= board.getNumRows(); row++ ) {
            for ( int col = 1; col <= board.getNumCols(); col++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition) board.getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();
                  
                    BlockadeWall wall = lastMove.getWall();

                    // should reuse cached path if still valid.
                    List<Path> paths = pos.findShortestPaths(board, wall);
                        
                    playerPaths.getPathLengthsForPlayer(piece.isOwnedByPlayer1()).updatePathLengths(paths);
                }
            }
        }
        return playerPaths;
    }

    /**
     * @return error message if the new wall intersects an old one.
     */
    private boolean hasWallIntersection(BlockadeWall wall) {
         boolean vertical = wall.isVertical();
         // you cannot intersect one wall with another
         BlockadeBoardPosition pos = wall.getFirstPosition();
         
         BlockadeBoardPosition secondPos = (BlockadeBoardPosition)
                    (vertical? board.getPosition(pos.getRow(), pos.getCol()+1) :
                               board.getPosition(pos.getRow()+1, pos.getCol()));
        return (vertical && pos.isSouthBlocked() && secondPos.isSouthBlocked())
                || (!vertical && pos.isEastBlocked()) && secondPos.isEastBlocked();
    }

    /**
     * @return error message if no path exists from this position to an opponent home.
     */
    private boolean missingPath() {
        List<Path> paths1 = findAllOpponentShortestPaths(true);  
        List<Path> paths2 = findAllOpponentShortestPaths(false);
       GameContext.log(2, "paths1.magnitude="+paths1.size()+" paths2.magnitude ="+paths2.size() );
        
        int expectedNumPaths = getExpectedNumPaths();
        return  (paths1.size() < expectedNumPaths || paths2.size() <expectedNumPaths );      
    }

    private int getExpectedNumPaths() {
        return BlockadeBoard.NUM_HOMES * BlockadeBoard.NUM_HOMES;
    }
}
