// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.simulation.liquid.config.Wall;


/**
 * Maintain a list of best/shortest paths from a specific position.
 *
 * @author Barry Becker
 */
public class PathCache {

    /** Cache the most recent shortest paths to opponent homes so we do not have to keep recomputing them. */
    private PathList cachedPaths = null;

    /**
     * Update the cached list of shortest paths if necessary
     */
    public void update(BlockadeBoardPosition pos, BlockadeBoard board) {

        PathList paths = board.findShortestPaths(pos);
        cachedPaths = paths;
         /*
        if (isPathCacheBroken(board)) {
            //PathList paths = board.findShortestPaths(pos);
            cachedPaths = paths;
        }
        else {
            assert (paths.equals(cachedPaths))
                : (paths  + "\n was not equal to \n" + cachedPaths + "\n on board=" + board);
        }  */
    }

    public PathList getShortestPaths() {
        return cachedPaths;
    }

    /**
     * The cache is broken if the last wall placed blocks one of our cached paths.
     */
    private boolean isPathCacheBroken(BlockadeBoard board) {

        // if nothing cached, we need to create the cache.
        if (cachedPaths == null) {
            return true;
        }
        // broken if any of the paths to opponent home is blocked by a recent wall.
        for (Path path : cachedPaths) {
            if (path.isBlocked(board)) {
                return true;
            }
        }
        return false;
    }
}



