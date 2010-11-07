package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;

import javax.swing.*;
import java.awt.*;

/**
 * Draw the entire game tree using a java tree control.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
public final class TextGameTree extends JTree {

    private static final int TREE_WIDTH = 420;
    private static final int ROW_HEIGHT = 16;


    /**
     * Constructor - create the textual game tree.
     */
    public TextGameTree(SearchTreeNode root, GameTreeCellRenderer cellRenderer) {

        super( root );
        try {
            ToolTipManager.sharedInstance().registerComponent(this);
            setBackground(UIManager.getColor( "Tree.textBackground" ));
            setCellRenderer( cellRenderer );
            setPreferredSize( new Dimension( TREE_WIDTH, 900 ) );
            setShowsRootHandles( true );
            putClientProperty( "JTree.lineStyle", "Angled" );
            setRowHeight( ROW_HEIGHT );
            //addTreeExpansionListener( this );
        }
        catch (ArrayIndexOutOfBoundsException e) {
            GameContext.log(0,
                "Error: There was an ArayIndexOutOfBounds exception when creating a JTree from this root node: "+root);
            e.printStackTrace();
        }
    }


    /**
     * refresh the game tree.
     */
    public void refresh() {
        setPreferredSize(new Dimension( TREE_WIDTH, getRowCount() * ROW_HEIGHT ) );
    }
}

