package com.becker.game.twoplayer.common.search.tree;

import com.becker.common.util.Util;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.List;

/**
 *  Represents a move/node in the game tree.
 *  Each SearchTreeNode contains a Move as its userObject.
 *  When showing the game tree graphicall, these variables can take a lot of space
 *  since they are in every node in the tree. Still it is better to have them here than
 *  in the move structure so that when we are not in debug mode the space is not used.
 *
 *  @author Barry Becker
 */
public class SearchTreeNode extends DefaultMutableTreeNode
{
    private static final long serialVersionUID = 1L;

    /**
     *  true if this move is the leaf of a pruned path in the game tree.
     */
    private boolean pruned_;

    /**
     *  store the alpha value(for debug printing).
     */
    private double alpha_ = 0;

    /**
     *  store the beta value(for debug printing).
     */
    private double beta_ = 0;

    /**
     * Used to layout the tree
     * roughly based on the num descendants.
     * initialized by GameTreeViewer.
     */
    private int spaceAllocation_ = 0;


    /**
     * provide some useful info about the node, like why it was pruned.
     */
    private String comment_ = null;

    /**
     * location in the boardviewer
     */
    private int xLocation_;
    private int yLocation_;



    /**
     * Default Constructor
     * @param m the user object (like a board move).
     */
    public SearchTreeNode(Object m)
    {
        setUserObject(m);
        pruned_ = false;
    }


    public TwoPlayerMove[] getChildMoves() {
        if (children == null)
            return null;
        TwoPlayerMove[] moves = new TwoPlayerMove[children.size()];
        Enumeration enumeration = children();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode)enumeration.nextElement();
            moves[i++] = (TwoPlayerMove)node.getUserObject();
        }
        return moves;
    }

    /**
     * Add a move to the visual game tree (if parent not null).
     * @param theMove the two player move to add.
     * @param alpha for the added node
     * @param beta beta for the added node.
     * @param i the child index of the added node.
     * @return the childNode that was added.
     */
    public SearchTreeNode addChild(TwoPlayerMove theMove,
                                       int alpha, int beta, int i ) {

        SearchTreeNode child = new SearchTreeNode( theMove );
        child.setAlpha(alpha);
        child.setBeta(beta);
        this.insert( child, i );

        return child;
    }


    /**
     * Show nodes corresponding to pruyned branches in the game tree (if one is used).
     *
     * @param list list of moves that resulted in pruned branches.
     * @param val the worth of the node/move
     * @param thresh the alpha or beta threshold compared to.
     * @param type either PRUNE_ALPHA or PRUNE_BETA - pruned by comparison with Alpha or Beta.
     * @param i th child.
     */
    public void addPrunedChildNodes( List list, int i, int val, int thresh, PruneType type)
    {
        int index = i;
        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            SearchTreeNode child = new SearchTreeNode( theMove );
            child.setPruned(true);
            String sComp = (type == PruneType.ALPHA) ? " < " : " > ";
            child.setComment("Children pruned because " +
                            Util.formatNumber(val) + sComp + Util.formatNumber(thresh) + '.');
            this.insert( child, index );
            index++;
        }
    }

    /**
     *
     * @return the move that the computer expects will be played next
     */
    public SearchTreeNode getExpectedNextNode() {
        if (children == null)
            return null;
        Enumeration enumeration = children();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode)enumeration.nextElement();
            TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
            if (m.isSelected())
                return node;
        }
        return null;
    }


    @Override
    public String toString () {
        Object m = getUserObject();
        if (m==null) return null;

        StringBuffer s = new StringBuffer(m.toString());

        if ( pruned_ )
            s.append( " *PRUNED*" );
        else
            s.append( " a=" + Util.formatNumber(alpha_) + " b=" + Util.formatNumber(beta_) );

        return s.toString();
    }

    public boolean isPruned() {
        return pruned_;
    }

    public void setPruned(boolean pruned) {
        this.pruned_ = pruned;
    }

    public double getAlpha() {
        return alpha_;
    }

    public void setAlpha(double alpha) {
        this.alpha_ = alpha;
    }

    public double getBeta() {
        return beta_;
    }

    public void setBeta(double beta) {
        this.beta_ = beta;
    }

    public int getSpaceAllocation() {
        return spaceAllocation_;
    }

    public void setSpaceAllocation(int spaceAllocation) {
        this.spaceAllocation_ = spaceAllocation;
    }

    public String getComment() {
        return comment_;
    }

    public void setComment(String comment) {
        this.comment_ = comment;
    }

    public int getX() {
        return xLocation_;
    }

    public void setX(int x) {
        this.xLocation_ = x;
    }

    public int getY() {
        return yLocation_;
    }

    public void setY(int y) {
        this.yLocation_ = y;
    }
}
