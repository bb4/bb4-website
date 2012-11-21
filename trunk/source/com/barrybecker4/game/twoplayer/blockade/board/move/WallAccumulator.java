// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.Path;
import com.barrybecker4.game.twoplayer.blockade.board.PathList;

import java.util.LinkedList;
import java.util.List;

/**
 * Find reasonable wall placements for a given pawn placement.
 *
 * @author Barry Becker
 */
class WallAccumulator {

    private BlockadeBoard board;
    BlockadeWallList wallsList;

    /**
     * Constructor
     */
    WallAccumulator(BlockadeBoard board) {
        this.board = board;
        wallsList = new BlockadeWallList();
    }

    BlockadeWallList getAccumulatedList() {
        return wallsList;
    }

    /**
     * Add the walls that don't block your own paths, but do block the opponent shortest paths.
     * @param pos to start from.
     * @param paths our friendly paths (do not add wall  if it intersects one of these paths).
     * @param direction to move one space (one of EAST, WEST, NORTH, SOUTH).
     * @return the accumulated list of walls.
     */
    BlockadeWallList checkAddWallsForDirection(BlockadeBoardPosition pos, PathList paths,
                                                         Direction direction) {
        BlockadeBoard b = board;
        BlockadeWallList wallsToCheck = new BlockadeWallList();
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
                 wallsToCheck = checkWallsForDiagonal(northWestPos, northPos, westPos);
                 break;
            case NORTH_EAST :
                 BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b);
                 wallsToCheck = checkWallsForDiagonal(northPos, northEastPos, pos);
                 break;
            case SOUTH_WEST :
                 BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b);
                 wallsToCheck = checkWallsForDiagonal(westPos, pos, southWestPos);
                 break;
            case SOUTH_EAST :
                 wallsToCheck = checkWallsForDiagonal(pos, eastPos, southPos);
                 break;
        }

        return getBlockedWalls(wallsToCheck, paths);
    }

    /**
     * @param wallsToCheck
     * @param paths
     * @return wallsList list of walls that are blocking paths.
     */
    private BlockadeWallList getBlockedWalls(BlockadeWallList wallsToCheck, PathList paths)  {
        for (BlockadeWall wall : wallsToCheck) {
            if (wall != null && !arePathsBlockedByWall(paths, wall))
                wallsList.add(wall);
        }

        return wallsList;
    }

    /**
     * Add valid wall placements to the east.
     * Also verify not intersecting a horizontal wall.
     */
    private void addWallsForEast(BlockadeBoardPosition eastPos,
                                 BlockadeBoardPosition pos, BlockadeWallList wallsToCheck) {
        if (eastPos!=null && !pos.isEastBlocked())  {

            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board);
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board);
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
     * Add valid wall placements to the west.
     */
    private void addWallsForWest(BlockadeBoardPosition westPos,
                                                       BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (westPos!=null && !westPos.isEastBlocked())  {
            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board);
            if (northPos !=  null && !northWestPos.isEastBlocked()
                && !(northWestPos.isSouthBlocked() && northWestPos.getSouthWall() == northPos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(westPos, northWestPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board);
            if (southPos != null && !southWestPos.isEastBlocked()
                && !(westPos.isSouthBlocked() && westPos.getSouthWall() == pos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(westPos, southWestPos) );
            }
        }
    }

    /**
     * Add valid wall placements to the north.
     */
    private void addWallsForNorth(BlockadeBoardPosition northPos,
                                                        BlockadeBoardPosition pos, List<BlockadeWall> wallsToCheck) {
        if (northPos!=null && !northPos.isSouthBlocked())  {
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board);
            if (westPos != null && !northWestPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == northWestPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall( northPos, northWestPos) );
            }
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board);
            if (northEastPos != null && !northEastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == northPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(northPos, northEastPos) );
            }
        }
    }

    /**
     * Add valid wall placements to the south.
     */
    private void addWallsForSouth(BlockadeBoardPosition southPos,
                                  BlockadeBoardPosition pos , List<BlockadeWall> wallsToCheck) {
        if (southPos != null && !pos.isSouthBlocked()) {
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board);
            BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, board);
            if (eastPos != null && !eastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == southPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(pos, eastPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board);
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
    private boolean arePathsBlockedByWall(PathList paths, BlockadeWall wall) {
        assert (wall != null);
        for (final Path path : paths) {
            if (path.isBlockedByWall(wall, board))
                return true;
        }
        return false;
    }

    /**
     * The 9 wall cases for a diagonal move
     */
    private BlockadeWallList checkWallsForDiagonal(
                                   BlockadeBoardPosition topLeft,
                                   BlockadeBoardPosition topRight,
                                   BlockadeBoardPosition bottomLeft ) {
        boolean leftWall = topLeft.isSouthBlocked();
        boolean rightWall = topRight.isSouthBlocked();
        boolean topWall = topLeft.isEastBlocked();
        boolean bottomWall = bottomLeft.isEastBlocked();
        BlockadeWallList wallsToCheck = new BlockadeWallList();

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
    private BlockadeWallList handleDirectionCase(BlockadeBoardPosition pos, int rowOffset, int colOffset,
                                                          BlockadeWallList wallsToCheck) {
        BlockadeBoardPosition offsetPos =
                    (BlockadeBoardPosition)board.getPosition(pos.getRow() + rowOffset, pos.getCol() + colOffset);
        boolean isVertical = (rowOffset != 0);

        if (offsetPos != null) {
            boolean dirOpen = isVertical? offsetPos.isEastOpen() : offsetPos.isSouthOpen();
            if (dirOpen)
               wallsToCheck.add( new BlockadeWall(pos, offsetPos));
        }
        return wallsToCheck;
    }
}
