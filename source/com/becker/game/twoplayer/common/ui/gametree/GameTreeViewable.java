package com.becker.game.twoplayer.common.ui.gametree;

import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchWindow;
import com.becker.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.NodeAttributes;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;

import javax.swing.*;

/**
 * Responsible for handling events related to modifying the nodes in the game tree.
 *
 * We need these methods to occur on the event dispatch thread to avoid
 * threading conflicts that could occur during concurrent rendering.
 *
 * @author Barry Becker
 */
public final class GameTreeViewable implements IGameTreeViewable {

    private volatile SearchTreeNode root_;

    /**
     * constructor - create the tree dialog.
     */
    public GameTreeViewable(TwoPlayerMove m) {
        root_ = new SearchTreeNode(m, new NodeAttributes());
    }

    public SearchTreeNode getRootNode() {
        return root_;
    }


    /**
     * Add a child node at position i to the specified parent node.
     */
    public void addNode(final SearchTreeNode parent, final SearchTreeNode child) {

        parent.add(child);
    }

    /**
     * Add a child node at position i to the specified parent node.
     */
    public void addNode(final SearchTreeNode parent, final SearchTreeNode child, final int i) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.insert(child, i);
            }
        });
    }

    /**
     *  Show the specified list of pruned nodes under the specified parent.
     */
    public void addPrunedNodes(final MoveList list, final SearchTreeNode parent,
                               final int i, final NodeAttributes attributes) {
        // make a defensive copy of the list because we may modify it.
        final MoveList listCopy = new MoveList(list);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.addPrunedChildNodes(listCopy, i, attributes);
            }
        });
    }

    /**
     * Clear all nodes but the root.
     * @param p two player move to set the root to.
     */
    public void resetTree(final TwoPlayerMove p) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                root_.removeAllChildren(); // clear it out
                p.setSelected(true);
                root_.setUserObject( p );
            }
        });        
    }
}

