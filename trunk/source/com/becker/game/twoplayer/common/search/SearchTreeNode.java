package com.becker.game.twoplayer.common.search;

import com.becker.common.Util;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 *  this class represents a move/node in the game tree.
 *  Each SearchTreeNode contains a Move as its userObject.
 *  When showing the game tree graphicall, these variables can take a lot of space
 *  since they are in every node in the tree. Still it is better to have them here than
 *  in the move structure so that when we are not in debug mode the space is not used.
 *
 *  @author Barry Becker
 */
public class SearchTreeNode extends DefaultMutableTreeNode
{

    /**
     *  true if this move is the leaf of a pruned path in the game tree.
     */
    public boolean pruned;

    /**
     *  store the alpha value(for debug printing).
     */
    public double alpha = 0;

    /**
     *  store the beta value(for debug printing).
     */
    public double beta = 0;

    /**
     * num descendants including itself (never 0).
     * initialized by GameTreeViewer.
     */
    public int numDescendants = 0;

    /**
     * Used to layout the tree
     * roughly based on the num descendants.
     * initialized by GameTreeViewer.
     */
    public int spaceAllocation = 0;


    /**
     * provide some useful info about the node, like why it was pruned.
     */
    public String comment = null;

    /**
     * location in the boardviewer
     */
    public int x,y;

    // @@ also contain visisbility info here


    // Default Constructor
    public SearchTreeNode(Object m)
    {
        setUserObject(m);
        pruned = false;
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


    public String toString () {
        Object m = getUserObject();
        if (m==null) return null;

        StringBuffer s = new StringBuffer(m.toString());

        if ( pruned )
            s.append( " *PRUNED*" );
        else
            s.append( " a=" + Util.formatNumber(alpha) + " b=" + Util.formatNumber(beta) );

        return s.toString();
    }

}
