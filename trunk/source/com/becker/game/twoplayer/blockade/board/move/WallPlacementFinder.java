// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.blockade.board.move;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.blockade.board.*;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Generates candidate next moves for a game of cBlockade given a current board state.
 *
 * @author Barry Becker
 */
public class WallPlacementFinder {

    private BlockadeBoard board_;
    private List<Path> opponentPaths_;
    private ParameterArray weights_;

    /**
     * Constructor
     */
    public WallPlacementFinder(BlockadeBoard board, List<Path> opponentPaths, ParameterArray weights) {

        board_ = board;
        weights_ = weights;
        opponentPaths_ = opponentPaths;
    }

    /**
     * Find variations for move firstStep based on all the possible valid wall placements that make the opponent
     * shortest paths longer, while not adversely affecting our own shortest paths.
     * @@ optimize
     * @param firstStep the move to find wall placements for.
     * @param paths our shortest paths.
     * @return all move variations on firstStep based on different wall placements.
     */
    public List<BlockadeMove> findWallPlacementsForMove(BlockadeMove firstStep, List<Path> paths) {
        List<BlockadeMove> moves = new LinkedList<BlockadeMove>();

        // is it true that the set of walls we could add for any constant set
        // of opponent paths is always the same regardless of firstStep?
        // I think its only true as long as firstStep is not touching any of those opponent paths
        GameContext.log(2, firstStep + "\nopaths="+opponentPaths_ + "\n [[");

        for (Path opponentPath: opponentPaths_) {
            assert (opponentPath != null):
                "Opponent path was null. There are "+opponentPaths_.size()+" oppenent paths.";
            for (int j = 0; j < opponentPath.getLength(); j++) {
                // if there is no wall currently interfering with this wall placement,
                // and it does not impact a friendly path,
                // then consider this (and/or its twin) a candidate placement.
                // by twin I mean the other wall placement that also intersects this opponent path
                // (there are always N for walls of size N where N is the number of spaces spanned by a wall).
                BlockadeMove move = opponentPath.get(j);

                // get all the possible legal and reasonable wall placements for this move
                // along the opponent path that do not interfere with our own paths.
                List<BlockadeWall> walls = getWallsForMove(move, paths);
                GameContext.log(2, "num walls for move "+move+"  = "+walls.size() );

                if  (walls.isEmpty()) {
                    GameContext.log(1, "***No walls for move "+move+" at step j=" + j + " along opponentPath="+opponentPath
                            +" that do not interfere with our path");
                }

                // typically 0-4 walls
                assert walls.size() <=4:"num walls = " + walls.size();
                for (BlockadeWall wall: walls) {
                    addMoveWithWallPlacement(firstStep, wall, weights_, moves);
               }
           }
           if (moves.isEmpty()) {
               GameContext.log(1, "No opponent moves found for "+firstStep +" along opponentPath="+opponentPath);
           }
           //assert (!moves.isEmpty()) : "No opponent moves found for "+firstStep +" along opponentPath="+opponentPath;
       }
       GameContext.log(2, "]]");

        // if no move was added add the more with no wall placement
        if (moves.isEmpty()) {
           addMoveWithWallPlacement(firstStep, null, weights_, moves);
        }

        return moves;
    }

    /**
     * Add a new move to movelist.
     * The move is based on ourmove and the specified wall (the wall may be null is none placed).
     */
    private void addMoveWithWallPlacement(BlockadeMove ourmove, BlockadeWall wall,
                                                                         ParameterArray weights, List<BlockadeMove> moves) {
        int value = 0;
        // @@ we should provide the value here since we have all the path info.
        // we do not want to compute the path info again by calling findPlayerPathLengths.
        // The value will change based on how much we shorten our paths while lengthening the opponents.
        BlockadeMove m =
               BlockadeMove.createMove(ourmove.getFromLocation(),
                                       ourmove.getToLocation(),
                                       value, ourmove.getPiece(), wall);
        // for the time being just call worth directly. Its less efficient, but simpler.
        board_.makeMove(m);
        PlayerPathLengths pathLengths = board_.findPlayerPathLengths(m);
        board_.undoMove();

        if (pathLengths.isValid()) {
            m.setValue(pathLengths.determineWorth(SearchStrategy.WINNING_VALUE, weights));
            moves.add(m);
        }
        else {
            GameContext.log(2, "Did not add "+ m+ " because it was invalid.");
        }
    }

    /**
     * Find all the possible and legal wall placements for this given step along the opponent path
     * that do not adversely affecting our own shortest paths.
     * Public so it can be tested.
     * @param move move along an opponent path.
     * @param paths our friendly paths.
     * @return the walls for a specific move along an opponent path.
     */
    @SuppressWarnings("fallthrough")
    List<BlockadeWall> getWallsForMove(BlockadeMove move, List<Path> paths) {
        List<BlockadeWall> wallsList = new LinkedList<BlockadeWall>();

        // 12 cases
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoard b = board_;
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
     * Add the walls that don't block your own paths, but do block the opponent shortest paths.
     * @param pos to start from.
     * @param paths our friendly paths (do not add wall  if it intersects one of these paths).
     * @param direction to move one space (one of EAST, WEST, NORTH, SOUTH).
     * @return the accumulated list of walls.
     */
    private List<BlockadeWall> checkAddWallsForDirection(BlockadeBoardPosition pos, List<Path> paths,
                                                         Direction direction, List<BlockadeWall> wallsList) {
        BlockadeBoard b = board_;
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
     * @param wallsToCheck
     * @param paths
     * @return wallsList list of walls that are blocking paths.
     */
    private List<BlockadeWall> getBlockedWalls(List<BlockadeWall> wallsToCheck, List<Path> paths,
                                               List<BlockadeWall> wallsList)  {
        for (BlockadeWall wall : wallsToCheck) {
            if (wall != null && !arePathsBlockedByWall(paths, wall, board_))
                wallsList.add(wall);
        }

        return wallsList;
    }


    /**
     *Add valid wall placements to the east.
     *Also verify not intersecting a horizontal wall.
     */
    private void addWallsForEast(BlockadeBoardPosition eastPos,
                                                      BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (eastPos!=null && !pos.isEastBlocked())  {

            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board_);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board_);
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board_);
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
            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board_);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board_);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board_);
            if (northPos !=  null && !northWestPos.isEastBlocked()
                && !(northWestPos.isSouthBlocked() && northWestPos.getSouthWall() == northPos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(westPos, northWestPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board_);
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
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board_);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board_);
            if (westPos != null && !northWestPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == northWestPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall( northPos, northWestPos) );
            }
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board_);
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
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board_);
            BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, board_);
            if (eastPos != null && !eastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == southPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(pos, eastPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board_);
            if (westPos!=null && !westPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == southWestPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(pos, westPos) );
            }
         }
    }


    /**
     * @param paths are any of these paths blocked by the specified wall?
     * @param wall that we check to see if blocking any paths
     * @return true if the wall is blocking any of the paths.
     */
    private static  boolean arePathsBlockedByWall(List<Path> paths, BlockadeWall wall, BlockadeBoard b)
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
                                                    BlockadeBoardPosition bottomLeft ) {
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
        else {  // bottomWall is true
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
    private List<BlockadeWall> handleDirectionCase(BlockadeBoardPosition pos, int rowOffset, int colOffset,
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
}
