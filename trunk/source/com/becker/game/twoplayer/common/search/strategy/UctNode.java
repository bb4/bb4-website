package com.becker.game.twoplayer.common.search.strategy;

import com.becker.common.format.FormatUtil;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.WinProbabilityCaclulator;
import com.becker.game.twoplayer.common.search.tree.NodeAttributes;

import java.util.LinkedList;
import java.util.List;

/**
 * A node in the in memory UCT tree created during search.
 *
 * @author Barry Becker
 */
public class UctNode {

    /** The number of times we have visited this node in the tree. */
    private int numVisits;

    /** The move this node represents. */
    public TwoPlayerMove move;

    /** The number of times we have won a random game that starts from this node. */
    private float numWins;

    /** List of child nodes (moves)  */
    private List<UctNode> children;

    /**
     * not sure what this is for. See http://senseis.xmp.net/?UCT. Make a param.
     * Seems to make the exploreExploit constant balance at 1.
     */
    private static final double DENOM_CONST = 5.0;

    /** Some big number. */
    private static final double BIG = 1000;

    /**
     * Constructor.
     * @param move the move we represent
     */
    public UctNode(TwoPlayerMove move) {
        this.move = move;
    }

    public int getNumVisits() {
        return numVisits;
    }

    /**
     * Increment our number of wins we won again at this node.
     * A tie counts as only half a win.
     * @param player1Score if 1 then p1 won; if 0, then p1 lost, else 0.5 - considered a tie (inconclusive)
     */
    public void update(double player1Score) {
        numVisits++;
        numWins += (move.isPlayer1()) ? player1Score : 1.0-player1Score;
    }

    /**
     * Note that the winRate returned is for the player who is about to move.
     * @return ratio of wins to visits. Return a tie score if never visited.
     */
    public float getWinRate() {
        return (numVisits == 0) ?
                WinProbabilityCaclulator.getChanceOfPlayer1Winning(move.getValue()) :
                numWins / (float)numVisits;
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
     * @return the number of children added
     */
    public int addChildren(MoveList moves) {
        int numKids = 0;
        children = new LinkedList<UctNode>();
        for (Move m : moves) {
            UctNode newNode = new UctNode((TwoPlayerMove) m);
            children.add(newNode);
            numKids++;
        }
        return numKids;
    }

    /**
     * @return the bestNode to the child with the highest winrate
     */
    public TwoPlayerMove findBestChildMove() {
        UctNode bestNode = null;
        if (hasChildren())  {
            for (UctNode child : children) {
                if (bestNode == null || child.getWinRate() > bestNode.getWinRate()) {
                    bestNode = child;
                }
            }
        }
        return bestNode == null? null : bestNode.move;
    }

    /**
     * This is the secret sauce at the core of the UCT algorithm.
     * See http://www-958.ibm.com/software/data/cognos/manyeyes/visualizations/uct-search-parameters
     * For analysis of effect of parameters on UCT value returned.
     *
     * @param exploreExploitRatio bigger values mean more exploration as opposed to exploitation of known good moves.
     * @param parentVisits the number of times our parent node has been visited.
     * @return the uct value which is somewhat related to the winRate
     *    and a ratio of our visits and out next siblings visits.
     */
    public double calculateUctValue(double exploreExploitRatio, int parentVisits) {
        if (numVisits > 0) {
            double uct = exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (DENOM_CONST * numVisits));
            return getWinRate() + uct;
        }
        else {
            // always play a random unexplored move first.
            // Something bigger than any win rate, yet higher if better move.
            return BIG + getWinRate();
        }
    }

    public NodeAttributes getAttributes() {
        NodeAttributes attributes = new NodeAttributes();
        attributes.put("visits", Integer.toString(numVisits));
        attributes.put("wins", Float.toString(numWins));
        attributes.put("winRate", FormatUtil.formatNumber(getWinRate()));
        return attributes;
    }

    public String toString() {
        return move.toString() + " " + getAttributes().toString();
    }

    /** print the tree rooted at this node */
    public void printTree() {
        System.out.println("ROOT -------------------");
        printTree("", this.numVisits);
    }

    /**
     * print the tree rooted at this node.
     * @param indent amount to indent
     */
    private void printTree(String indent, int parentVisits) {
        System.out.println(indent + this.toString()
                + " uct=" + FormatUtil.formatNumber(this.calculateUctValue(1.0, parentVisits)));
        if (hasChildren()) {
            for (UctNode child : this.getChildren()) {
                child.printTree(indent + "  ", this.numVisits);
            }
        }
    }
}