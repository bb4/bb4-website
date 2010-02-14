package com.becker.game.twoplayer.common.search.tree;

import com.becker.game.twoplayer.common.TwoPlayerMove;
import java.util.List;

/**
 * This interface is implemented by classes that can show the game tree as it is searched.
 * I used to modify the game tree nodes directly during search, but found that I got a lot of intermittent
 * concurrent modification exceptions and npes while showing the game tree. Now events are thrown to
 * indicate changes to the tree should be made during search, and the handler should make the changes
 * to the tree in the eventDispatch thread.
 *
 * @author Barry Becker Date: May 21, 2006
 */
public interface GameTreeViewable {

    /**
     *
     * @return the root node of the search tree.
     */
    SearchTreeNode getRootNode();

    /**
     * Add a node to the viewable search tree.
     * @param parent
     * @param child
     * @param i
     */
    void addNode(SearchTreeNode parent, SearchTreeNode child, int i);

    /**
     * Add a set of pruned nodes to the viewable search tree.
     */
    void addPrunedNodes(List<? extends TwoPlayerMove> list, SearchTreeNode parent,
                        int i, int val, int thresh, PruneType type);

    /**
     * Clear out the visible search tree.
     * @param evt event
     */
    void resetTree(TwoPlayerMove evt);

}
