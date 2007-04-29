package com.becker.game.twoplayer.blockade;

import java.util.List;

/**
 * Utility class for holding the difference paths lengths.
 *
 * @author Barry Becker
 */
class PathLengths {
    
    int shortestLength = Integer.MAX_VALUE;
    int secondShortestLength = Integer.MAX_VALUE;
    int furthestLength = 0;

    public PathLengths() {}

    public String toString() {
       return "shortestLength=" + shortestLength+
               " secondShortestLength =" + secondShortestLength+
               " furthestLength=" + furthestLength;
    }


    public void updatePathLengths(List[] moves)
    {
        for (final List newVar : moves) {
            int len = newVar.size();
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
