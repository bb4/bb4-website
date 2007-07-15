package com.becker.game.twoplayer.blockade;

import java.util.List;

/**
 * Utility class for holding the different paths lengths.
 *
 * @author Barry Becker
 */
public class PathLengths {
    
    int shortestLength = Integer.MAX_VALUE;
    int secondShortestLength = Integer.MAX_VALUE;
    int furthestLength = 0;
    
    boolean isValid = true;
   
    /**
     */
    public PathLengths() {}

    /**
     * Update the values of the shortest, secondShortest and furthest.
     * @param paths
     */
    public void updatePathLengths(List<Path> paths)
    {
        // if we don't have NUM_HOMES paths then this set of path lengths is invalid.
        // probably the move and corresponding wall placement was not valid, or we landed on a home.
        if (paths.size() < BlockadeBoard.NUM_HOMES) {
            isValid = false;
            return;
        }
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
    
    /**
     *There must be at least one path from every pawn to every opponent base.
     *@return false if there are not enough paths. 
     */
    public boolean isValid() {
        return isValid;
    }
    
    /**
     * Serialize.
     */
    public String toString() {
       return "shortestLength=" + shortestLength+
               " secondShortestLength =" + secondShortestLength+
               " furthestLength=" + furthestLength;
    }
}
