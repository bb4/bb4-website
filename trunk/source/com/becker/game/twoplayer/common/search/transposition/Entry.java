package com.becker.game.twoplayer.common.search.transposition;

import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 * An entry in the transposition table.
 * We could also store a key that is more accurate than than the Zobrist key to detect if there is a collision.
 *
 * @author Barry Becker
 */
public class Entry {

    public TwoPlayerMove bestMove;
    public int upperValue;
    public int lowerValue;
    public int depth;

    /**
     * Constructor.
     */
    public Entry(TwoPlayerMove bestMove, int depth, int lowerValue, int upperValue)
     {
         this.bestMove = bestMove;
         this.upperValue = upperValue;
         this.lowerValue = lowerValue;
         this.depth = depth;
    }

    /**
     * Constructor.
     * Use this version if the upper and lower bounds are the same.
     * We must be at level 0 in this case
     */
    public Entry(TwoPlayerMove bestMove, int value)
     {
         this.bestMove = bestMove;
         this.upperValue = value;
         this.lowerValue = value;       
         this.depth = 0;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("Entry depth=").append(depth);
        bldr.append("bestMove=").append(bestMove);
        bldr.append("range=[").append(lowerValue).append(", ").append(upperValue).append("]");
        return bldr.toString();
    }
}