package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchWindow;

import java.util.List;
import java.util.Random;

/**
 * An entry in the transposition table.
 * We could also store a key that is more accurate than than the Zobrist key to detect if there is a collision.
 *
 * @author Barry Becker
 */
public class UctNode {

    public int numWins;
    public int numVisits;
    public TwoPlayerMove move;
    // current favorite among children
    public UctNode bestNode;
    public List<UctNode> children;

    /** not sure what this is for. See http://senseis.xmp.net/?UCT */
    private static final double DENOM_CONST = 5.0;
    private static final double BIG = 100;

    private static final Random RAND = new Random();

    /**
     * Constructor.
     */
    public UctNode(TwoPlayerMove move) {
         this.move = move;
    }

    public void updateWin(boolean player1Won) {
        if (move.isPlayer1() == player1Won) {
            numWins++;
        }
    }

    public double getWinRate() {
        return (double) numWins / numVisits;
    }

    /**
     * Set the bestNode to the child with the highest winrate
     */
    public void setBestNode() {
        if (children != null)  {
            for (UctNode child : children) {
                if (bestNode == null || child.getWinRate() > bestNode.getWinRate()) {
                    bestNode = child;
                }
            }
        }
    }

    /**
     * This is the secret sauce at the core of the UCT algorithm.
     * @param exploreExploitRatio bigger values mean more exploration as opposted to exploitation of known good moves.
     * @param parentVisits the number of times our parent node has been visited.
     * @return magive uct value which is somewhat related to the winRate
     *    and a ratio of our visits and out next siblings visits.
     */
    public double calculateUctValue(double exploreExploitRatio, int parentVisits) {
        if (numVisits > 0) {
            return getWinRate() + exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (5.0*numVisits));
        }
        else {
            // always play a random unexplored move first.
            // something bigger than any win rate, yet randomly different from other big values.
            return BIG + BIG *RAND.nextDouble();
        }
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("Move=").append(move);
        bldr.append("numVisits=").append(numVisits);
        bldr.append("numWins=").append(numWins);
        return bldr.toString();
    }
}