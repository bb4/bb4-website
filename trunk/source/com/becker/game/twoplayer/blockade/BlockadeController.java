package com.becker.game.twoplayer.blockade;

import com.becker.common.Util;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.common.Move;
import com.becker.optimization.ParameterArray;
import com.becker.sound.MusicMaker;

import java.util.*;

/**
 * Defines for the computer how it should play blockade.
 *
 * Todo items
 *   - computer moves only one space instead of two.
 *   - don't allow placing a wall such that it intersects an existing wall.
 *   - The winner should win as soon as he lands on an opponent base and not have to wait to place the wall.
 *   - only allow wall placements up to (xdim*ydim)/4 walls for each player
 *
 * @author Barry Becker
 */
public class BlockadeController extends TwoPlayerController
{

    public static final int DEFAULT_LOOKAHEAD = 2;

    // the desfault Blockade board is 14 by 11
    static final int NUM_ROWS = 14;
    static final int NUM_COLS = 11;


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

        int r = (int) (Math.random() * moveList.size());
        TwoPlayerMove m = (TwoPlayerMove) moveList.get( r );

        makeMove( m );
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
     * The primary way of computing the score for Blockade is to
     * weight the difference of the 2 shortest minimum paths plus the
     * weighted difference of the 2 furthest minimum paths.
     * An alternative method might be to weight the sum of the our shortest paths
     * and diference it with the weight the sum of the opponent shortest paths.
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

        for ( row = 1; row <= board.getNumRows(); row++ ) {      //rows
            for ( col = 1; col <= board.getNumCols(); col++ ) {  //cols
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();
                    int pInd = piece.isOwnedByPlayer1()?p1:p2;
                    List moves[] = board.findShortestPaths(pos);
                    if (moves.length != BlockadeBoard.NUM_HOMES)
                        GameContext.log(1, "Too few paths = "+moves.length+" != "+BlockadeBoard.NUM_HOMES);
                    pathLengths[pInd].updatePathLengths(moves);
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

    public List getPossibleMoveList(BoardPosition position)
    {
        return ((BlockadeBoard)board_).getPossibleMoveList(position, !position.getPiece().isOwnedByPlayer1());
    }

    /**
     * @param path
     * @param wall
     * @return true if the wall is blocking the paths.
     */
    private static boolean isPathBlockedByWall(List path, BlockadeWall wall, BlockadeBoard b)
    {
        Iterator it = path.iterator();
        while (it.hasNext()) {
            BlockadeMove move = (BlockadeMove)it.next();
            if (b.isMoveBlockedByWall(move, wall) )
                return true;
        }
        return false;
    }

    /**
     * @param paths are any of these paths blocked by the specified wall.
     * @param wall
     * @return true if the wall is blocking any of the paths.
     */
    private static  boolean arePathsBlockedByWall(List[] paths, BlockadeWall wall, BlockadeBoard b)
    {
        assert (wall!=null);
        for (final List newVar : paths) {
            if (isPathBlockedByWall(newVar, wall, b))
                return true;
        }
        return false;
    }

    private static BlockadeWall createWall(boolean isVertical, BlockadeBoardPosition p1, BlockadeBoardPosition p2)
    {
         Set hsPositions = new HashSet( 2 );
         hsPositions.add( p1 );
         hsPositions.add( p2 );
         return new BlockadeWall( isVertical, hsPositions );
    }

    private List handleRightCase(BlockadeBoardPosition topRight, List wallsToCheck)
    {
        BlockadeBoardPosition topRightRight =
                    (BlockadeBoardPosition)board_.getPosition(topRight.getRow(), topRight.getCol()+1);
        if (topRightRight!=null && topRightRight.isSouthOpen()) {
            wallsToCheck.add( createWall(false, topRight, topRightRight) );
        }
        return wallsToCheck;
    }

    private List handleLeftCase(BlockadeBoardPosition topLeft, List wallsToCheck)
    {
        BlockadeBoardPosition topLeftLeft =
                (BlockadeBoardPosition)board_.getPosition(topLeft.getRow(), topLeft.getCol()-1);
        if (topLeftLeft!=null && topLeftLeft.isSouthOpen()) {
            wallsToCheck.add( createWall(false, topLeft, topLeftLeft ));
        }
        return wallsToCheck;
    }

    private List handleTopCase(BlockadeBoardPosition topLeft, List wallsToCheck)
    {
        BlockadeBoardPosition topTopLeft =
                (BlockadeBoardPosition)board_.getPosition(topLeft.getRow()-1, topLeft.getCol());
        if (topTopLeft!=null && topTopLeft.isEastOpen()) {
            wallsToCheck.add( createWall(true, topLeft, topTopLeft) );
        }
        return wallsToCheck;
    }

    private List handleBottomCase(BlockadeBoardPosition bottomLeft, List wallsToCheck)
    {
        BlockadeBoardPosition bottomBottomLeft =
                (BlockadeBoardPosition)board_.getPosition(bottomLeft.getRow()+1, bottomLeft.getCol());
        if (bottomBottomLeft!=null && bottomBottomLeft.isEastOpen()) {
            wallsToCheck.add( createWall(true, bottomLeft, bottomBottomLeft) );
        }
        return wallsToCheck;
    }


    /**
     * The 9 wall cases for a diagonal move
     */
    private List checkWallsForDiagonal(BlockadeBoardPosition topLeft, BlockadeBoardPosition topRight,
                                          BlockadeBoardPosition bottomLeft )
    {
        boolean leftWall = topLeft.isSouthBlocked();
        boolean rightWall = topRight.isSouthBlocked();
        boolean topWall = topLeft.isEastBlocked();
        boolean bottomWall = bottomLeft.isEastBlocked();
        List wallsToCheck = new LinkedList();

        // now check and add walls based on the nine possible cases
        if (!leftWall && !rightWall && !topWall && !bottomWall) {
            wallsToCheck.add( createWall(false, topLeft, topRight) );
            wallsToCheck.add( createWall(true, topLeft, bottomLeft) );
        }
        // probably can condense these cases
        else if (leftWall && bottomWall) {
            wallsToCheck = handleRightCase(topRight, wallsToCheck);
            wallsToCheck = handleTopCase(topLeft, wallsToCheck);
        }
        else if (topWall && rightWall) {
            wallsToCheck = handleLeftCase(topLeft, wallsToCheck);
            wallsToCheck = handleBottomCase(bottomLeft, wallsToCheck);
        }
        else if (topWall && leftWall) {
            wallsToCheck = handleRightCase(topRight, wallsToCheck);
            wallsToCheck = handleBottomCase(bottomLeft, wallsToCheck);
        }
        else if (rightWall && bottomWall) {
            wallsToCheck = handleTopCase(topLeft, wallsToCheck);
            wallsToCheck = handleLeftCase(topLeft, wallsToCheck);
        }
        else if (leftWall) {
            wallsToCheck.add( createWall(true, topLeft, bottomLeft) );
            wallsToCheck = handleRightCase(topRight, wallsToCheck);
        }
        else if (rightWall) {
            wallsToCheck.add( createWall(true, topLeft, bottomLeft) );
            wallsToCheck = handleLeftCase(topLeft, wallsToCheck);
        }
        else if (topWall) {
            wallsToCheck.add( createWall(false, topLeft, topRight) );
            wallsToCheck = handleBottomCase(bottomLeft, wallsToCheck);
        }
        else if (bottomWall) {
            wallsToCheck.add( createWall(false, topLeft, topRight) );
            wallsToCheck = handleTopCase(topLeft, wallsToCheck);
        }
        return wallsToCheck;
    }

    /**
     * Add the walls that don't block your own paths, but do block the opponent shortest paths.
     * @param pos to start from.
     * @param paths our friendly paths ( do not add wall  if it intersects one of these paths).
     * @param direction to move one space (one of EAST, WEST, NORTH, SOUTH).
     * @return the accumulated list of walls.
     */
    private List checkAddWallsForDirection(BlockadeBoardPosition pos, List[] paths, int direction, List wallsList)
    {
        int row = pos.getRow();
        int col = pos.getCol();
        BlockadeBoard board = (BlockadeBoard)board_;
        int numRows = board.getNumRows();
        int numCols = board.getNumCols();
        List wallsToCheck = new LinkedList();
        BlockadeBoardPosition westPos;
        BlockadeBoardPosition eastPos;
        BlockadeBoardPosition northPos;
        BlockadeBoardPosition southPos;

        switch (direction) {
            case BlockadeMove.EAST :
                if (!pos.isEastBlocked() && col+1 <= numCols)  {
                    if (row-1 > 0) {
                        northPos = (BlockadeBoardPosition)board_.getPosition(row-1, col);
                        if (!northPos.isEastBlocked())
                            wallsToCheck.add( createWall(true, pos, northPos) );
                    }
                    if (row+1 <= numRows) {
                        southPos = (BlockadeBoardPosition)board_.getPosition(row+1, col);
                        if (!southPos.isEastBlocked())
                            wallsToCheck.add( createWall(true, pos, southPos) );
                    }
                }
                break;
            case BlockadeMove.WEST :
                westPos = (BlockadeBoardPosition)board_.getPosition(row, col-1);
                if (westPos!=null && !westPos.isEastBlocked())  {
                    if (row-1 > 0) {
                        northPos = (BlockadeBoardPosition)board_.getPosition(row-1, col-1);
                        if (!northPos.isEastBlocked())
                            wallsToCheck.add( createWall(true, westPos, northPos) );
                    }
                    if (row+1 <= numRows) {
                        southPos = (BlockadeBoardPosition)board_.getPosition(row+1, col-1);
                        if (!southPos.isEastBlocked())
                            wallsToCheck.add( createWall(true, westPos, southPos) );
                    }
                }
                break;
            case BlockadeMove.NORTH :
                northPos = (BlockadeBoardPosition)board_.getPosition(row-1, col);
                if (northPos!=null && !northPos.isEastBlocked())  {
                    if (col-1 > 0) {
                        westPos = (BlockadeBoardPosition)board_.getPosition(row-1, col-1);
                        if (!westPos.isSouthBlocked())
                            wallsToCheck.add( createWall(false, northPos, westPos) );
                    }
                    if (col+1 <= numCols) {
                        eastPos = (BlockadeBoardPosition)board_.getPosition(row-1, col+1);
                        if (!eastPos.isEastBlocked())
                            wallsToCheck.add( createWall(false, northPos, eastPos) );
                    }
                }
                break;
            case BlockadeMove.SOUTH :
                 if (!pos.isSouthBlocked() && row+1 <= numRows)  {
                    if (col-1 > 0) {
                        eastPos = (BlockadeBoardPosition)board_.getPosition(row, col-1);
                        if (!eastPos.isSouthBlocked())
                            wallsToCheck.add( createWall(false, pos, eastPos) );
                    }
                    if (col+1 < numCols) {
                        westPos = (BlockadeBoardPosition)board_.getPosition(row, col+1);
                        if (!westPos.isSouthBlocked())
                            wallsToCheck.add( createWall(false, pos, westPos) );
                    }
                 }
                 break;
            // There are 4 basic cases for all the diagonals.
            case BlockadeMove.NORTH_WEST :
                 westPos = (BlockadeBoardPosition)board_.getPosition(row, col-1);
                 northPos = (BlockadeBoardPosition)board_.getPosition(row-1, col);
                 BlockadeBoardPosition northWestPos = (BlockadeBoardPosition)board_.getPosition(row-1, col-1);
                 wallsToCheck = checkWallsForDiagonal(northWestPos, northPos, westPos );
                 break;
            case BlockadeMove.NORTH_EAST :
                 northPos = (BlockadeBoardPosition)board_.getPosition(row-1, col);
                 BlockadeBoardPosition northEastPos = (BlockadeBoardPosition)board_.getPosition(row-1, col+1);
                 wallsToCheck = checkWallsForDiagonal(northPos, northEastPos, pos );
                 break;
            case BlockadeMove.SOUTH_WEST :
                 westPos = (BlockadeBoardPosition)board_.getPosition(row, col-1);
                 BlockadeBoardPosition southWestPos = (BlockadeBoardPosition)board_.getPosition(row+1, col-1);
                 wallsToCheck = checkWallsForDiagonal(westPos, pos, southWestPos );
                 break;
            case BlockadeMove.SOUTH_EAST :
                 eastPos = (BlockadeBoardPosition)board_.getPosition(row, col+1);
                 southPos = (BlockadeBoardPosition)board_.getPosition(row+1, col);
                 wallsToCheck = checkWallsForDiagonal(pos, eastPos, southPos );
                 break;

            default: assert false:("invalid direction for checkAddWalls: "+direction);
        }

        return getBlockedWalls(wallsToCheck, paths, wallsList);
    }

    private List getBlockedWalls(List wallsToCheck, List[] paths, List wallsList)  {
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
     * Find all the possible wall placements for this given step along the opponent path
     * that do not adversely affecting our own shortest paths.
     * @param move move along an opponent path.
     * @param paths our friendly paths.
     * @return the walls for a specific move along an opponent path.
     */
    private List getWallsForMove(BlockadeMove move, List[] paths)
    {
        List wallsList = new LinkedList();

        // 12 cases
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoardPosition origPos =  (BlockadeBoardPosition)board_.getPosition(fromRow, fromCol);
        BlockadeBoardPosition northPos = (BlockadeBoardPosition)board_.getPosition(fromRow-1, fromCol);
        BlockadeBoardPosition eastPos =  (BlockadeBoardPosition)board_.getPosition(fromRow, fromCol+1);
        BlockadeBoardPosition westPos =  (BlockadeBoardPosition)board_.getPosition(fromRow, fromCol-1);
        BlockadeBoardPosition southPos = (BlockadeBoardPosition)board_.getPosition(fromRow+1, fromCol);
        // @@ add diagonals too
        switch (move.getDirection()) {
            case BlockadeMove.EAST_EAST :
                checkAddWallsForDirection(eastPos, paths, BlockadeMove.EAST, wallsList);
                checkAddWallsForDirection(origPos, paths, BlockadeMove.EAST, wallsList);
                break;
            case BlockadeMove.EAST :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.EAST, wallsList);
                break;
            case BlockadeMove.WEST_WEST :
                checkAddWallsForDirection(westPos, paths, BlockadeMove.WEST, wallsList);
                checkAddWallsForDirection(origPos, paths, BlockadeMove.WEST, wallsList);
                break;
            case BlockadeMove.WEST :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.WEST, wallsList);
                break;
            case BlockadeMove.SOUTH_SOUTH :
                checkAddWallsForDirection(southPos, paths, BlockadeMove.SOUTH, wallsList);
                checkAddWallsForDirection(origPos, paths, BlockadeMove.SOUTH, wallsList);
                break;
            case BlockadeMove.SOUTH :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.SOUTH, wallsList);
                break;
            case BlockadeMove.NORTH_NORTH :
                checkAddWallsForDirection(northPos, paths, BlockadeMove.NORTH, wallsList);
                checkAddWallsForDirection(origPos, paths, BlockadeMove.NORTH, wallsList);
                break;
            case BlockadeMove.NORTH :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.NORTH, wallsList);
                break;
            case BlockadeMove.NORTH_WEST :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.NORTH_WEST, wallsList);
                break;
            case BlockadeMove.NORTH_EAST :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.NORTH_EAST, wallsList);
                break;
            case BlockadeMove.SOUTH_WEST :
                checkAddWallsForDirection(origPos, paths, BlockadeMove.SOUTH_WEST, wallsList);
                break;
            case BlockadeMove.SOUTH_EAST:
                checkAddWallsForDirection(origPos, paths, BlockadeMove.SOUTH_EAST, wallsList);
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
    private List findWallPlacementsForMove(BlockadeMove firstStep, List[] paths, List[] opponentPaths,
                                           ParameterArray weights)
    {
        List moves = new LinkedList();
        BlockadeMove ourmove = firstStep;
        for (int i=0; i<opponentPaths.length; i++) {
           List opponentPath = opponentPaths[i];
           assert (opponentPath!=null): 
               "Opponent path "+i+" was null. There are "+opponentPaths.length+" oppenent paths.";
           for (int j=0; j<opponentPath.size(); j++) {
               // if there is no wall currently interfering with this wall placement,
               // and it does not impact a friendly path,
               // then consider this (and/or its twin) a candidate placement.
               // by twin I mean the other wall placement that also intersects this opponent path
               // (there are always N for walls of size N where N is the number of spaces spanned by a wall).
               BlockadeMove move = (BlockadeMove)opponentPath.get(j);

               // get all the possible legal and reasonable wall placements for this move along the opponent path.
               List walls = getWallsForMove(move, paths);
               //GameContext.log(0, "num walls for move "+move+"  = "+walls.size() );

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
     private int addMoves( BoardPosition p, List moveList, List[] opponentPaths,
                          ParameterArray weights )
     {
         BlockadeBoard board = (BlockadeBoard)board_;
         int numMovesAdded = 0;

         // first find the NUM_HOMES shortest paths for p.
         List[] paths = board.findShortestPaths((BlockadeBoardPosition)p);

         assert (paths.length == BlockadeBoard.NUM_HOMES):
                 "There must be at least one route to each opponent home base. Numpaths="+paths.length;

         // for each of these paths add possible wall positions.
         // Take the first move from each path and add the wall positions to it.
         for (int i=0; i<BlockadeBoard.NUM_HOMES; i++) {
             BlockadeMove firstStep = (BlockadeMove)paths[i].get(0);
             //firstStep.moveNumber = (lastMove == null)? 0 : (lastMove.moveNumber+1);
             // make the move
             board.makeMove(firstStep);

             // after making the first move, the shortest paths may have changed somewhat.
             // unfortunately, I think we need to recalculate them.
             BlockadeBoardPosition newPos = (BlockadeBoardPosition)board.getPosition(firstStep.getToRow(), firstStep.getToCol());
             List[] ourPaths = board.findShortestPaths(newPos);

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
            int row,col;
            boolean player1 = !(lastMove.isPlayer1());
            BlockadeBoard board = (BlockadeBoard)board_;

            // first find the opponents shortest paths. There must be NUM_HOMES squared of them.
            // There is one path from every piece to every opponent home (i.e. n*n)
            int numHomes = BlockadeBoard.NUM_HOMES;
            int numShortestPaths = numHomes * numHomes;
            List[] opponentPaths = new List[numShortestPaths];
            int totalOpponentPaths = 0;
            Set hsPawns = new HashSet();
            for ( row = 1; row <= board.getNumRows(); row++ ) {
                for ( col = 1; col <= board.getNumCols(); col++ ) {
                    BlockadeBoardPosition p = (BlockadeBoardPosition)board_.getPosition( row, col );
                    if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() != player1 ) {
                        hsPawns.add(p);
                        if (hsPawns.size() > numHomes) {
                            GameContext.log(0, "Error: too many opponent pieces: " );   // assert?
                            Util.printCollection(hsPawns);
                        }
                        List[] paths = board.findShortestPaths(p);
                        assert (paths.length == numHomes):
                            "There must be at least one route to each opponent home base. Numpaths="+paths.length;
                        GameContext.log(2,
                            "about to add "+numHomes+" more paths to "+totalOpponentPaths+" maxAllowed="+opponentPaths.length );
                        System.arraycopy(paths, 0, opponentPaths, totalOpponentPaths, numHomes);
                        totalOpponentPaths += numHomes;
                    }
                }
            }
            // For each piece of the current player's NUM_HOME pieces, add a move that represents a step along
            // its shortest paths to the opponent homes and all reasonable wall placements.
            // To limit the number of wall placements we will restrict possiblities to those positions which
            // effect one of the *opponents* shortest paths.
            for ( row = 1; row <= board.getNumRows(); row++ ) {
                for ( col = 1; col <= board.getNumCols(); col++ ) {
                    BoardPosition p = board_.getPosition( row, col );
                    if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() == player1 ) {
                        addMoves( p, moveList, opponentPaths, weights );
                    }
                }
            }
            assert (moveList.size()>0): "There aren't any moves to consider.";
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
