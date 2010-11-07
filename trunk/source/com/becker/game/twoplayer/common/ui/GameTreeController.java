package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.Board;
import com.becker.game.common.GameContext;
import com.becker.game.common.MoveList;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerViewable;
import com.becker.game.twoplayer.common.search.strategy.SearchWindow;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.ui.dialogs.AbstractDialog;
import com.becker.ui.legend.ContinuousColorLegend;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for handling events related to the display and interaction with the visual game tree.
 * WIP
 *
 * @author Barry Becker
 */
public final class GameTreeController implements GameTreeViewable, MouseMotionListener {

    /** the controller that is actually being played in the normal view. */
    private TwoPlayerController mainController_;

    private volatile GameTreeViewer treeViewer_;
    private volatile TextGameTree textTree_;
    private GameTreeInfoPanel infoPanel_;
    private volatile SearchTreeNode root_;
    private int oldChainLength_ = 0;

    private static final boolean SHOW_SUCCESSIVE_MOVES  = true;


    /** the viewer in the debug window. */
    private volatile TwoPlayerViewable boardViewer_;


    /**
     * constructor - create the tree dialog.
     */
    public GameTreeController(TwoPlayerController mainController,  GameTreeViewer treeViewer,
                              AbstractTwoPlayerBoardViewer boardViewer, TextGameTree textTree,
                              GameTreeInfoPanel infoPanel) {
        mainController_ = mainController;
        treeViewer_ = treeViewer;
        textTree_ = textTree;
        infoPanel_ = infoPanel;
        boardViewer_ = boardViewer;
    }

    /**
     * start over from scratch.
     */
    public synchronized void reset() {
        if (textTree_!=null) {
            textTree_.removeMouseMotionListener(this);
        }
        root_ = new SearchTreeNode(null);
        boardViewer_.reset();
        treeViewer_.setRoot(root_);
    }

    public synchronized SearchTreeNode getRootNode() {
        return root_;
    }


    /**
     * called when a particular move in the game tree has been selected by the user (by clicking on it or mouse-over).
     */
    private synchronized void selectCallback( MouseEvent e ) {

        JTree tree = (JTree) e.getSource();

        if (mainController_.isProcessing())  {
            // avoid concurrency problems
            return;
        }

        int row = tree.getRowForLocation( e.getX(), e.getY() );
        if ( row == -1 ) return;

        TreePath path = tree.getPathForRow( row );
        treeViewer_.highlightPath( path );

        int chainLength = path.getPathCount();
        Object[] nodes = path.getPath();
        SearchTreeNode lastNode = (SearchTreeNode)nodes[chainLength-1];
        List<TwoPlayerMove> moveList = new LinkedList<TwoPlayerMove>();
        TwoPlayerMove m = null;
        for ( int i = 0; i < chainLength; i++ ) {
            SearchTreeNode node = (SearchTreeNode) nodes[i];
            m = (TwoPlayerMove) node.getUserObject();
            if ( m == null )
                return; // no node here
            moveList.add( m );
        }

        AbstractTwoPlayerBoardViewer viewer = (AbstractTwoPlayerBoardViewer)boardViewer_;
        if (SHOW_SUCCESSIVE_MOVES) {
            // add expected successive moves to show likely outcome.
            moveList = addSuccessiveMoves(moveList, lastNode);
        }
        GameContext.log(3, "chainlen before="+chainLength+" after="+moveList.size());
        chainLength = moveList.size();
        viewer.showMoveSequence( moveList, oldChainLength_, lastNode.getChildMoves() );

        // remember the old chain length so we know how much to back up next time
        oldChainLength_ = chainLength;

        infoPanel_.setText(viewer, m, lastNode);
    }

    /**
     * Add to the list all the moves that we expect are most likely to occur given the current game state.
     * This is how the computer expects the game to play out.
     * @return the list of successive moves.
     */
    private static List<TwoPlayerMove> addSuccessiveMoves(List<TwoPlayerMove> moveList, SearchTreeNode finalNode) {

        SearchTreeNode nextNode = finalNode.getExpectedNextNode();
        while (nextNode !=  null)  {
            TwoPlayerMove m = ((TwoPlayerMove)nextNode.getUserObject()).copy();
            m.setFuture(true);
            moveList.add(m);
            nextNode = nextNode.getExpectedNextNode();
        }
        return moveList;
    }


    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
        selectCallback( e );
    }


    /* --------------- GameTreeViewable implementation ------------------*/
    // we need these methods to occur on the event dispatch thread to avoid
    // threading conflicts that could occur during concurrent rendering.

    public void addNode(final SearchTreeNode parent, final SearchTreeNode child, final int i) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.insert(child, i);
            }
        });
    }

    public void addPrunedNodes(final MoveList list, final SearchTreeNode parent,
                               final int i, final int val, final SearchWindow window) {
        // make a defensive copy of the list because we may modify it.
        final MoveList listCopy = new MoveList(list);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.addPrunedChildNodes(listCopy, i, val, window);
            }
        });
    }

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

