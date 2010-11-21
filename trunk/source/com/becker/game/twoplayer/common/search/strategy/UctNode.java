package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.NodeAttributes;

import java.util.List;
import java.util.Random;

/**
 * An entry in the transposition table.
 * We could also store a key that is more accurate than than the Zobrist key to detect if there is a collision.
 *
 * @author Barry Becker
 */
public class UctNode {

    /** The number of times we have won a random game that starts from this node. */
    public int numWins;

    /** The number of times we have visited this node in the tree. */
    public int numVisits;

    /** The move this node represents. */
    public TwoPlayerMove move;

    /** current favorite among child nodes. */
    public UctNode bestNode;

    /** List of childe nodes (moves)  */
    public List<UctNode> children;

    /** We are the childIndexth child of our parent. */
    public byte childIndex;

    /** not sure what this is for. See http://senseis.xmp.net/?UCT. Make a param. */
    private static final double DENOM_CONST = 5.0;

    /** Some big number. */
    private static final double BIG = 100;

    /** Ensure reproducible random results if all game params stay the same. */
    private static final Random RAND = new Random(0);

    /**
     * Constructor.
     * @param move the move we represent
     * @parma i child Index of parent node.
     */
    public UctNode(TwoPlayerMove move, int i) {
        this.move = move;
        this.childIndex = (byte)i;
    }

    /**
     * Incriment our number of wins we won again at this node.
     * @param player1Won true if player 1 won
     */
    public void updateWin(boolean player1Won) {
        if (move.isPlayer1() == player1Won) {
            numWins++;
        }
    }

    /**
     * @return ratio of wins to visits.
     */
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
     * @return the uct value which is somewhat related to the winRate
     *    and a ratio of our visits and out next siblings visits.
     */
    public double calculateUctValue(double exploreExploitRatio, int parentVisits) {
        if (numVisits > 0) {
            return getWinRate() + exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (DENOM_CONST * numVisits));
        }
        else {
            // always play a random unexplored move first.
            // something bigger than any win rate, yet randomly different from other big values.
            return BIG + BIG * RAND.nextDouble();
        }
    }

    public NodeAttributes getAttributes() {
        NodeAttributes attributes = new NodeAttributes();
        attributes.put("visits", Integer.toString(numVisits));
        attributes.put("wins", Integer.toString(numWins));
        return attributes;
    }

    public String toString() {
        return move.toString() + " " + getAttributes().toString();
    }
}