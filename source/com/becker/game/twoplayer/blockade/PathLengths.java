package com.becker.game.twoplayer.blockade;

import java.util.List;

/**
 * Utility class for holding the different paths lengths.
 *
 * @author Barry Becker
 */
class PathLengths {
    
    int shortestLength = Integer.MAX_VALUE;
    int secondShortestLength = Integer.MAX_VALUE;
    int furthestLength = 0;

    public PathLengths() {}

    /**
     *
     */
    public String toString() {
       return "shortestLength=" + shortestLength+
               " secondShortestLength =" + secondShortestLength+
               " furthestLength=" + furthestLength;
    }

    /**
     *
     */
    public void updatePathLengths(Path[] paths)
    {
        for (final Path p : paths) {
            int len = p.getLength();
            if (len < shortestLength) {
                secondShortestLength = shortestLength;
                shortestLength = len;
            } else if (len < secondShortestLength) {
                secondShortestLength = len;
            }

            if (len > furthestLength) {
                furthestLength = len;
            }
        }
    }
}
