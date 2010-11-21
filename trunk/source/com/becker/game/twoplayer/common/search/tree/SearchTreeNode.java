package com.becker.game.twoplayer.common.search.tree;

import com.becker.common.util.Util;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchWindow;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *  Represents a move/node in the game tree.
 *  Each SearchTreeNode contains a Move as its userObject.
 *  When showing the game tree graphically, these variables can take a lot of space
 *  since they are in every node in the tree. Still it is better to have them here than
 *  in the move structure so that when we are not in debug mode the space is not used.
 *
 *  @author Barry Becker
 */
public class SearchTreeNode extends DefaultMutableTreeNode
{
    private static final long serialVersionUID = 1L;

    public NodeAttributes attributes;

    /**
     * Used to layout the tree. Roughly based on the num descendants.
     * initialized by GameTreeViewer.
     */
    private int spaceAllocation_ = 0;

    /**
     * location in the boardviewer
     */
    private Point position_;

    /**
     * Default Constructor
     * @param m a twoplayer board move.
     */
    public SearchTreeNode(TwoPlayerMove m) {
        setUserObject(m);
        this.attributes = new NodeAttributes();
    }


    /**
     * Default Constructor
     * @param m a twoplayer board move.
     * @param attributes set of name value pairs describing the node.
     */
    public SearchTreeNode(TwoPlayerMove m, NodeAttributes attributes) {
        setUserObject(m);
        this.attributes = attributes;
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
     * @param i the child index of the added node.
     * @return the childNode that was added.
     *
    public SearchTreeNode addChild(TwoPlayerMove theMove,
                                   NodeAttributes attributes, int i ) {

        SearchTreeNode child = new SearchTreeNode( theMove, attributes );
        this.insert( child, i );
        return child;
    }*/

    /**
     * See if the specified move is already a child
     * @param theMove specified move to check
     * @return the corresponding search node if it is a child.
     */
    public SearchTreeNode hasChild(TwoPlayerMove theMove) {
        Enumeration enumeration = children();
        int i=0;
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode)enumeration.nextElement();
            if (theMove.equals(node.getUserObject())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Show nodes corresponding to pruned branches in the game tree (if one is used).
     *
     * @param list list of moves that resulted in pruned branches.
     * @param i th child.
     * @param attributes list of name values to show.
     */
    public void addPrunedChildNodes( List list, int i, NodeAttributes attributes)
    {
        int index = i;
        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            SearchTreeNode child = new SearchTreeNode( theMove, attributes );
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

        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode)enumeration.nextElement();
            TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
            if (m.isSelected())
                return node;
        }
        return null;
    }
    /*
    public TwoPlayerMove getMove() {
        return (TwoPlayerMove) this.getUserObject();
    } */

    public boolean isPruned() {
        return attributes.pruned;
    }

    @Override
    public String toString () {
        Object m = getUserObject();
        if (m == null) return null;

        StringBuffer s = new StringBuffer(m.toString());   
        s.append(attributes.toString());

        return s.toString();
    }


    public int getSpaceAllocation() {
        return spaceAllocation_;
    }

    public void setSpaceAllocation(int spaceAllocation) {
        this.spaceAllocation_ = spaceAllocation;
    }

    public Point getPosition() {
        return position_;
    }

    public void setLocation(int x, int y) {
        position_ = new Point(x, y);
    }
}
