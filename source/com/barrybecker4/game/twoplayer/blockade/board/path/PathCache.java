// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;


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
            System.out.println("cacheMiss");
            cachedPaths = paths;
        }
        else {
            System.out.println("cacheHit pl=" + paths.getTotalPathLength() +" cpl=" + cachedPaths.getTotalPathLength());
            assert (paths.getTotalPathLength() == cachedPaths.getTotalPathLength())
                : (paths  + "\n was not equal to \n" + cachedPaths + "\n on board=" + board);
        }  */
    }

    public PathList getShortestPaths() {
        return cachedPaths;
    }

    /**
     * The cache is broken if any recently placed wall blocks one of our cached paths.
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



