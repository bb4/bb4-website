package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.NodeAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * An entry in the transposition table.
 * We could also store a key that is more accurate than than the Zobrist key to detect if there is a collision.
 *
 * @author Barry Becker
 */
public class UctNode {

    /** The number of times we have visited this node in the tree. */
    public int numVisits;

    /** The move this node represents. */
    public TwoPlayerMove move;

    /** current favorite among child nodes. */
    public UctNode bestNode;

    /** The number of times we have won a random game that starts from this node. */
    private int numWins;

    /** List of childe nodes (moves)  */
    private List<UctNode> children;

    /**
     * not sure what this is for. See http://senseis.xmp.net/?UCT. Make a param.
     * Seems to make the exploreExploit constant balance at 1.
     */
    private static final double DENOM_CONST = 5.0;

    /** Some big number. */
    private static final double BIG = 1000;

    /** Ensure reproducible random results if all game params stay the same. */
    private static final Random RAND = new Random(0);

    /**
     * Constructor.
     * @param move the move we represent
     */
    public UctNode(TwoPlayerMove move) {
        this.move = move;
    }

    /**
     * Increment our number of wins we won again at this node.
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
        return (numVisits == 0) ? 0 : (double)numWins / (double)numVisits;
    }

    public List<UctNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children != null;
    }

    /**
     * Add the children to the node.
     * @param moves child moves to add.
     */
    public void addChildren(MoveList moves) {

        children = new LinkedList<UctNode>();
        for (Move m : moves) {
            children.add(new UctNode((TwoPlayerMove) m));
        }
    }

    /**
     * Set the bestNode to the child with the highest winrate
     */
    public void setBestNode() {
        if (hasChildren())  {
            for (UctNode child : children) {
                if (bestNode == null || child.getWinRate() > bestNode.getWinRate()) {
                    bestNode = child;
                }
            }
        }
    }

    /**
     * This is the secret sauce at the core of the UCT algorithm.
     * See http://www-958.ibm.com/software/data/cognos/manyeyes/visualizations/uct-search-parameters
     * For analysis of effect of parameters on UCT value returned.
     *
     * @param exploreExploitRatio bigger values mean more exploration as opposted to exploitation of known good moves.
     * @param parentVisits the number of times our parent node has been visited.
     * @return the uct value which is somewhat related to the winRate
     *    and a ratio of our visits and out next siblings visits.
     */
    public double calculateUctValue(double exploreExploitRatio, int parentVisits) {
        if (numVisits > 0) {
            return getWinRate() + exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (DENOM_CONST * numVisits));
            //System.out.println("wr="+ getWinRate() +
            //        "+"+ exploreExploitRatio+" * " + Math.sqrt(Math.log(parentVisits)) + "/" +(DENOM_CONST * numVisits) + " =" + v);
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