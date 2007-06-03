package com.becker.game.twoplayer.blockade;

import com.becker.common.Util;
import com.becker.game.common.*;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameExporter;
import com.becker.game.twoplayer.blockade.persistence.BlockadeGameImporter;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.ParameterArray;
import com.becker.sound.MusicMaker;

import java.util.*;

/**
 * Defines for the computer how it should play blockade.
 *
 * Todo items
 *   - in debug mode, show the shortest paths to the opponent base.
 *   - computer moves only one space instead of two.
 *   - The winner should win as soon as he lands on an opponent base and not have to wait to place the wall.
 *   - only allow wall placements up to (xdim*ydim)/4 walls for each player
 *
 * @author Barry Becker
 */
public class BlockadeController extends TwoPlayerController
{

    public static final int DEFAULT_LOOKAHEAD = 2;

    // the desfault Blockade board is 14 by 11
    private static final int NUM_ROWS = 14;
    private static final int NUM_COLS = 11;


    /**
     *  Construct the Blockade game controller.
     */
    public BlockadeController()
    {
        initializeData();
        board_ = new BlockadeBoard(NUM_ROWS, NUM_COLS);
    }

    /**
     * this gets the Blockade specific weights.
     */
    protected void initializeData()
    {
        weights_ = new BlockadeWeights();
    }

    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions(DEFAULT_LOOKAHEAD, 50, MusicMaker.APPLAUSE);
    }

    /**
     * The computer makes the first move in the game
     */
    public void computerMovesFirst()
    {
        // create a bogus previous move
        TwoPlayerMove lastMove = BlockadeMove.createMove( 2, 2, 3, 3, 0, new GamePiece(false), null );

        // determine the possible moves and choose one at random.
        List moveList = getSearchable().generateMoves( lastMove, weights_.getPlayer1Weights(), true );

        makeMove( getRandomMove(moveList) );
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public double getStrengthOfWin()
    {
        if (!getPlayer1().hasWon() && !getPlayer2().hasWon())
             return 0.0;
        return worth(board_.getLastMove(), weights_.getDefaultWeights());
    }

    
    /**
     * save the current state of the blockade game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    public void saveToFile( String fileName, AssertionError ae )
    {
        BlockadeGameExporter exporter = new BlockadeGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }


    public void restoreFromFile( String fileName ) {
        BlockadeGameImporter importer = new BlockadeGameImporter(this);
        importer.restoreFromFile(fileName);
    }


    /**
     * The primary way of computing the score for Blockade is to
     * weight the difference of the 2 shortest minimum paths plus the
     * weighted difference of the 2 furthest minimum paths.
     * An alternative method might be to weight the sum of the our shortest paths
     * and difference it with the weighte sum of the opponent shortest paths.
     * The minimum path for a piece is the distance to its closest enemy home position.
     * @@ It can be expensive to compute these paths. Consider caching them.
     * We can use the wall in lastMove to determine which paths might have changed.
     *
     * @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        int row, col;
        final int p1 = 0;
        final int p2 = 1;
        BlockadeBoard board = (BlockadeBoard)board_;
        PathLengths[] pathLengths = new PathLengths[2];
        pathLengths[p1] = new PathLengths();
        pathLengths[p2] = new PathLengths();

        for ( row = 1; row <= board.getNumRows(); row++ ) {   
            for ( col = 1; col <= board.getNumCols(); col++ ) { 
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();
                    int pInd = piece.isOwnedByPlayer1()? p1 : p2;
                    Path paths[] = board.findShortestPaths(pos);                    
                    pathLengths[pInd].updatePathLengths(paths);
                }
            }
        }
        double value;

        // if it landed on an opponents home base, then return a winning value.
        // it has landed if any of the shortest paths are 0.
        if ( pathLengths[p1].shortestLength == 0 )
            value = WINNING_VALUE;
        else if ( pathLengths[p2].shortestLength == 0 )
            value = -WINNING_VALUE;
        else {
            int shortestLengthDiff = pathLengths[p2].shortestLength - pathLengths[p1].shortestLength;
            int secondShortestDiff = pathLengths[p2].secondShortestLength - pathLengths[p1].secondShortestLength;
            int furthestLengthDiff = pathLengths[p2].furthestLength - pathLengths[p1].furthestLength;

            value =  weights.get(BlockadeWeights.CLOSEST_WEIGHT_INDEX).getValue() *  shortestLengthDiff
                   + weights.get(BlockadeWeights.SECOND_CLOSEST_WEIGHT_INDEX).getValue() * secondShortestDiff
                   + weights.get(BlockadeWeights.FURTHEST_WEIGHT_INDEX).getValue() * furthestLengthDiff;
        }
        return value;
    }

    public List<BlockadeMove> getPossibleMoveList(BoardPosition position)
    {
        return ((BlockadeBoard)board_).getPossibleMoveList(position, !position.getPiece().isOwnedByPlayer1());
    }

    /**
     * @param paths are any of these paths blocked by the specified wall?
     * @param wall that we check to see if blocking any paths
     * @return true if the wall is blocking any of the paths.
     */
    private static  boolean arePathsBlockedByWall(Path[] paths, BlockadeWall wall, BlockadeBoard b)
    {
        assert (wall!=null);
        for (final Path path : paths) {
            if (path.isBlockedByWall(wall, b))
                return true;
        }
        return false;
    }
 

    /**
     * The 9 wall cases for a diagonal move
     */
    private List<BlockadeWall> checkWallsForDiagonal(
                                                    BlockadeBoardPosition topLeft, 
                                                    BlockadeBoardPosition topRight,
                                                    BlockadeBoardPosition bottomLeft )
    {
        boolean leftWall = topLeft.isSouthBlocked();
        boolean rightWall = topRight.isSouthBlocked();
        boolean topWall = topLeft.isEastBlocked();
        boolean bottomWall = bottomLeft.isEastBlocked();
        List<BlockadeWall> wallsToCheck = new LinkedList<BlockadeWall>();

        // now check and add walls based on the nine possible cases
        if (!(leftWall || rightWall || topWall || bottomWall)) {
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck.add( new BlockadeWall(topLeft, bottomLeft) );
        }
       else if (leftWall && bottomWall) {
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
            wallsToCheck = handleDirectionCase(topLeft, -1, 1, wallsToCheck);
        }
        else if (topWall && rightWall) {
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else if (topWall && leftWall) {
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else if (rightWall && bottomWall) {
            wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck);
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
        }
        else if (leftWall) {
            wallsToCheck.add( new BlockadeWall( topLeft, bottomLeft) );
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
        }
        else if (rightWall) {
            wallsToCheck.add( new BlockadeWall( topLeft, bottomLeft) );
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
        }
        else if (topWall) {
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else if (bottomWall) {
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck);
        }
        return wallsToCheck;
    }
    
    
    /**
     * This used to be 4 methods (one for each of right, left, top, and bottom).
     * @param pos one of the 3 base positions
     * @return list of accumulated walls to check.
     */
    private List handleDirectionCase(BlockadeBoardPosition pos, int rowOffset, int colOffset, 
                                                          List<BlockadeWall> wallsToCheck)
    {
        BlockadeBoardPosition offsetPos =
                    (BlockadeBoardPosition)board_.getPosition(pos.getRow() + rowOffset, pos.getCol() + colOffset);
        boolean isVertical = (rowOffset != 0);
        
        if (offsetPos != null) {
            boolean dirOpen = isVertical? offsetPos.isEastOpen() : offsetPos.isSouthOpen();
            if (dirOpen)
               wallsToCheck.add( new BlockadeWall(pos, offsetPos));
        }  
        return wallsToCheck;
    }


    /**
     * Add the walls that don't block your own paths, but do block the opponent shortest paths.
     * @param pos to start from.
     * @param paths our friendly paths (do not add wall  if it intersects one of these paths).
     * @param direction to move one space (one of EAST, WEST, NORTH, SOUTH).
     * @return the accumulated list of walls.
     */
    private List<BlockadeWall> checkAddWallsForDirection(BlockadeBoardPosition pos, Path[] paths, 
                                                                                               Direction direction, List<BlockadeWall> wallsList)
    {
        int row = pos.getRow();
        int col = pos.getCol();
        BlockadeBoard b = (BlockadeBoard)board_;
        int numRows = b.getNumRows();
        int numCols = b.getNumCols();
        List<BlockadeWall> wallsToCheck = new LinkedList<BlockadeWall>();
        BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, b); 
        BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, b);  
        BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, b); 
        BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, b); 

        switch (direction) {
            case EAST :
                addWallsForEast(eastPos, pos, wallsToCheck);  
                break;
            case WEST :
                addWallsForWest(westPos, pos, wallsToCheck);               
                break;
            case NORTH :
                addWallsForNorth(northPos, pos, wallsToCheck);       
                break;
            case SOUTH :
                 addWallsForSouth(southPos, pos, wallsToCheck);                  
                 break;
            // There are 4 basic cases for all the diagonals.
            case NORTH_WEST :
                 BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, b);   
                 wallsToCheck = checkWallsForDiagonal(northWestPos, northPos, westPos );
                 break;
            case NORTH_EAST :
                 BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b);  
                 wallsToCheck = checkWallsForDiagonal(northPos, northEastPos, pos );
                 break;
            case SOUTH_WEST :
                 BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b);  
                 wallsToCheck = checkWallsForDiagonal(westPos, pos, southWestPos );
                 break;
            case SOUTH_EAST :
                 wallsToCheck = checkWallsForDiagonal(pos, eastPos, southPos);
                 break;
        }

        return getBlockedWalls(wallsToCheck, paths, wallsList);
    }
    
    /**
     *Add valid wall placements to the east.
     *Also verify not intersecting a horizontal wall.
     */
    private void addWallsForEast(BlockadeBoardPosition eastPos, 
                                                      BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (eastPos!=null && !pos.isEastBlocked())  {   
            BlockadeBoard b = (BlockadeBoard)board_;
            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, b);  
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, b); 
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b);  
            if (northPos != null && !northPos.isEastBlocked()
                 && !(northPos.isSouthBlocked() && northPos.getSouthWall() == northEastPos.getSouthWall())) {                                
                wallsToCheck.add( new BlockadeWall(pos, northPos) );
            }
            if (southPos != null && !southPos.isEastBlocked()
                && !(pos.isSouthBlocked() && pos.getSouthWall() == eastPos.getSouthWall())) {    
                wallsToCheck.add( new BlockadeWall( pos, southPos) );
            }
        }
    }

    /**
     *Add valid wall placements to the west.
     */
    private void addWallsForWest(BlockadeBoardPosition westPos, 
                                                       BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (westPos!=null && !westPos.isEastBlocked())  {   
            BlockadeBoard b = (BlockadeBoard)board_;
            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, b);   
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, b); 
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, b);
            if (northPos !=  null && !northWestPos.isEastBlocked()
                && !(northWestPos.isSouthBlocked() && northWestPos.getSouthWall() == northPos.getSouthWall())) {        
                wallsToCheck.add( new BlockadeWall(westPos, northWestPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b); 
            if (southPos != null && !southWestPos.isEastBlocked()
                && !(westPos.isSouthBlocked() && westPos.getSouthWall() == pos.getSouthWall())) {   
                wallsToCheck.add( new BlockadeWall(westPos, southWestPos) );
            }
        }
    }
    
    /**
     *Add valid wall placements to the north.
     */
    private void addWallsForNorth(BlockadeBoardPosition northPos, 
                                                        BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (northPos!=null && !northPos.isSouthBlocked())  {
            BlockadeBoard b = (BlockadeBoard)board_;
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, b); 
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, b);   
            if (westPos != null && !northWestPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == northWestPos.getEastWall())) {   
                wallsToCheck.add( new BlockadeWall( northPos, northWestPos) );
            }                    
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b);
            if (northEastPos != null && !northEastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == northPos.getEastWall())) {   
                wallsToCheck.add( new BlockadeWall(northPos, northEastPos) );
            }
        }
    }
    
    /**
     *Add valid wall placements to the south.
     */
    private void addWallsForSouth(BlockadeBoardPosition southPos, 
                                                      BlockadeBoardPosition pos , List<BlockadeWall> wallsToCheck) {
        if (southPos != null && !pos.isSouthBlocked()) {    
            BlockadeBoard b = (BlockadeBoard)board_;
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, b);
            BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, b);
            if (eastPos != null && !eastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == southPos.getEastWall())) { 
                wallsToCheck.add( new BlockadeWall(pos, eastPos) );
            }               
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b); 
            if (westPos!=null && !westPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == southWestPos.getEastWall())) { 
                wallsToCheck.add( new BlockadeWall(pos, westPos) );
            }
         }
    }
    
    /**
     * @param wallsToCheck
     * @param paths
     * @return wallsList list of walls that are blocking paths.
     */
    private List getBlockedWalls(List<BlockadeWall> wallsToCheck, Path[] paths, List wallsList)  {
        BlockadeBoard board = (BlockadeBoard)board_;
        Iterator it = wallsToCheck.iterator();
        while (it.hasNext()) {
            BlockadeWall w = (BlockadeWall)it.next();
            if (w != null && !arePathsBlockedByWall(paths, w, board))
              wallsList.add(w);
        }

        return wallsList;
    }

    /**
     * Find all the possible and legal wall placements for this given step along the opponent path
     * that do not adversely affecting our own shortest paths.
     * Public so it can be tested.
     * @param move move along an opponent path.
     * @param paths our friendly paths.
     * @return the walls for a specific move along an opponent path.
     */
    public List<BlockadeWall> getWallsForMove(BlockadeMove move, Path[] paths)
    {
        List<BlockadeWall> wallsList = new LinkedList<BlockadeWall>();

        // 12 cases
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoard b = (BlockadeBoard)board_;
        BlockadeBoardPosition origPos =  (BlockadeBoardPosition)board_.getPosition(fromRow, fromCol);
        BlockadeBoardPosition westPos = origPos.getNeighbor(Direction.WEST, b); 
        BlockadeBoardPosition eastPos = origPos.getNeighbor(Direction.EAST, b);  
        BlockadeBoardPosition northPos = origPos.getNeighbor(Direction.NORTH, b); 
        BlockadeBoardPosition southPos = origPos.getNeighbor(Direction.SOUTH, b); 
        switch (move.getDirection()) {
            case EAST_EAST :
                checkAddWallsForDirection(eastPos, paths, Direction.EAST, wallsList);
            case EAST :
                checkAddWallsForDirection(origPos, paths, Direction.EAST, wallsList);
                break;
            case WEST_WEST :
                checkAddWallsForDirection(westPos, paths, Direction.WEST, wallsList);
            case WEST :
                checkAddWallsForDirection(origPos, paths, Direction.WEST, wallsList);
                break;
            case SOUTH_SOUTH :
                checkAddWallsForDirection(southPos, paths, Direction.SOUTH, wallsList);
            case SOUTH :
                checkAddWallsForDirection(origPos, paths, Direction.SOUTH, wallsList);
                break;
            case NORTH_NORTH :
                checkAddWallsForDirection(northPos, paths, Direction.NORTH, wallsList);
            case NORTH :
                checkAddWallsForDirection(origPos, paths, Direction.NORTH, wallsList);
                break;
            case NORTH_WEST :
                checkAddWallsForDirection(origPos, paths, Direction.NORTH_WEST, wallsList);
                break;
            case NORTH_EAST :
                checkAddWallsForDirection(origPos, paths, Direction.NORTH_EAST, wallsList);
                break;
            case SOUTH_WEST :
                checkAddWallsForDirection(origPos, paths, Direction.SOUTH_WEST, wallsList);
                break;
            case SOUTH_EAST:
                checkAddWallsForDirection(origPos, paths, Direction.SOUTH_EAST, wallsList);
                break;
            default : assert false:("Invalid direction "+move.getDirection());
        }

        return wallsList;
    }

    /**
     * Find variations for move firstStep based on all the possible wall placements that make the opponent
     * shortest paths longer, while not adversely affecting our own shortest paths.
     * @param firstStep the move to find wall placements for.
     * @param paths our shortest paths.
     * @param opponentPaths the opponent shortest paths.
     * @return all move variations on firstStep based on different wall placements.
     */
    private List<BlockadeMove> findWallPlacementsForMove(BlockadeMove firstStep, Path[] paths, Path[] opponentPaths,
                                           ParameterArray weights)
    {
        List<BlockadeMove> moves = new LinkedList<BlockadeMove>();
        BlockadeMove ourmove = firstStep;
        for (int i=0; i<opponentPaths.length; i++) {
           Path opponentPath = opponentPaths[i];
           assert (opponentPath != null): 
               "Opponent path "+i+" was null. There are "+opponentPaths.length+" oppenent paths.";
           for (int j = 0; j < opponentPath.getLength(); j++) {
               // if there is no wall currently interfering with this wall placement,
               // and it does not impact a friendly path,
               // then consider this (and/or its twin) a candidate placement.
               // by twin I mean the other wall placement that also intersects this opponent path
               // (there are always N for walls of size N where N is the number of spaces spanned by a wall).
               BlockadeMove move = opponentPath.get(j);

               // get all the possible legal and reasonable wall placements for this move along the opponent path.
               List<BlockadeWall> walls = getWallsForMove(move, paths);
               GameContext.log(2, "num walls for move "+move+"  = "+walls.size() );

               Iterator it = walls.iterator();
               while (it.hasNext()) {
                   BlockadeWall wall = (BlockadeWall)it.next();
                   //GameContext.log(0, "wallplacement="+wall);

                   // @@ we should provide the value here since we have all the path info.
                   // we do not want to compute the path info again by calling worth.
                   // The value will change based on how much we shorten our paths while lengthening the opponents.
                   double value = 0.0;
                   BlockadeMove m =
                           BlockadeMove.createMove(ourmove.getFromRow(), ourmove.getFromCol(),
                                                   ourmove.getToRow(), ourmove.getToCol(),
                                                   value, ourmove.getPiece(), wall);
                   // for the time being just call worth directly. Its less efficient, but simpler.
                   board_.makeMove(m);
                   m.setValue(worth(m, weights, m.getPiece().isOwnedByPlayer1()));
                   board_.undoMove();
                   moves.add(m);
               }
           }
       }
       return moves;
    }

    /**
      * Find all the moves a piece can make from position p, and insert them into moveList.
      *
      * @param p the piece to check.
      * @param moveList add the potential moves to this existing list.
      * @param weights to use.
      * @return the number of moves added.
      */
     private int addMoves( BoardPosition p, List moveList, Path[] opponentPaths,
                          ParameterArray weights )
     {
         BlockadeBoard board = (BlockadeBoard)board_;
         int numMovesAdded = 0;

         // first find the NUM_HOMES shortest paths for p.
         Path[] paths = board.findShortestPaths((BlockadeBoardPosition)p);

         assert (paths.length == BlockadeBoard.NUM_HOMES):
                 "There must be at least one route to each opponent home base. Numpaths="+paths.length;

         // for each of these paths add possible wall positions.
         // Take the first move from each path and add the wall positions to it.
         for (int i = 0; i < BlockadeBoard.NUM_HOMES; i++) {
             BlockadeMove firstStep = paths[i].get(0);
             //firstStep.moveNumber = (lastMove == null)? 0 : (lastMove.moveNumber+1);
             // make the move
             board.makeMove(firstStep);

             // after making the first move, the shortest paths may have changed somewhat.
             // unfortunately, I think we need to recalculate them.
             BlockadeBoardPosition newPos = (BlockadeBoardPosition)board.getPosition(firstStep.getToRow(), firstStep.getToCol());
             Path[] ourPaths = board.findShortestPaths(newPos);

             List wallMoves = findWallPlacementsForMove(firstStep, ourPaths, opponentPaths, weights);
             //GameContext.log(1, "num wall placements for Move = " +wallMoves.size());
             moveList.addAll(wallMoves);
             numMovesAdded += wallMoves.size();
             // undo the move
             board.undoMove();
         }
         GameContext.log(2, "addMoves nummoves add="+numMovesAdded );
         return numMovesAdded;
    }


    public Searchable getSearchable() {
        return new BlockadeSearchable();
    }


    public class BlockadeSearchable extends TwoPlayerSearchable {

        /**
         *  generate all possible legal and reasonable next moves.
         *  In blockade, there are a huge amount of possible next moves because of all the possible
         *  wall placements. So restrict wall placements to those that hinder the enemy while not hindering you.
         */
        public List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List moveList = new LinkedList();
            boolean player1 = !(lastMove.isPlayer1());
            BlockadeBoard board = (BlockadeBoard)board_;

            // first find the opponent's shortest paths. There must be NUM_HOMES squared of them.
            // There is one path from every piece to every opponent home (i.e. n*n)            
            Path[] opponentPaths = board.findAllOpponentShortestPaths(player1);            
            
            // For each piece of the current player's NUM_HOME pieces, add a move that represents a step along
            // its shortest paths to the opponent homes and all reasonable wall placements.
            // To limit the number of wall placements we will restrict possiblities to those positions which
            // effect one of the *opponents* shortest paths.
            for ( int row = 1; row <= board.getNumRows(); row++ ) {
                for ( int col = 1; col <= board.getNumCols(); col++ ) {
                    BoardPosition p = board_.getPosition( row, col );
                    if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() == player1 ) {
                        addMoves( p, moveList, opponentPaths, weights );
                    }
                }
            }
            assert (!moveList.isEmpty()): "There aren't any moves to consider.";
            return getBestMoves( player1, moveList, player1sPerspective );
        }


        /**
         * @@ quiescent search not yet implemented for Blockade
         * Probably we could return moves that result in a dratic change in value.
         *
         * @param lastMove
         * @param weights
         * @param player1sPerspective
         * @return list of urgent moves
         */
        public List generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            return null;
        }

        /**
         * returns true if the specified move caused one or more opponent pieces to become jeopardized
         */
        public boolean inJeopardy( TwoPlayerMove m )
        {
            return false;
        }
    }
}
