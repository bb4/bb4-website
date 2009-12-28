package com.becker.game.twoplayer.common.ui;

import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.PruneType;
import com.becker.ui.dialogs.AbstractDialog;
import com.becker.ui.legend.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Draw the entire game tree using a java tree control.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
public final class GameTreeDialog extends AbstractDialog
                               implements GameChangedListener, GameTreeViewable,
                                                   MouseMotionListener, TreeExpansionListener
{
    /** the options get set directly on the game controller that is passed in. */
    private TwoPlayerController controller_;

    private JScrollPane scrollPane_;
    private GameTreeViewer treeViewer_;
    private JTree textTree_;
    private SearchTreeNode root_;
    private int oldChainLength_ = 0;
    private GameTreeButtons gameTreeButtons_;

    private GameTreeInfoPanel infoPanel_;

    private static final int TREE_WIDTH = 420;
    private static final boolean SHOW_SUCCESSIVE_MOVES  = true;

    private Board board_ = null;

    /** the viewer in the debug window. */
    private TwoPlayerViewable boardViewer_;

    /** the controller that is actually being played in the normal view. */
    private TwoPlayerController mainController_;

    private GameTreeCellRenderer cellRenderer_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public GameTreeDialog( JFrame parent, AbstractTwoPlayerBoardViewer boardViewer, GameTreeCellRenderer cellRenderer )
    {
        super( parent );
        initialize(boardViewer, cellRenderer);
    }

    public synchronized void initialize(AbstractTwoPlayerBoardViewer boardViewer, GameTreeCellRenderer cellRenderer) {
        boardViewer_ = boardViewer;
        controller_ = (TwoPlayerController)boardViewer.getController();
        board_ = controller_.getBoard();
        cellRenderer_ = cellRenderer;
        //treeViewer_.setPieceRenderer();
        showContent();
    }

    /**
     * ui initialization of the tree control.
     */
    protected JComponent createDialogContent()
    {
        setTitle( "Game Tree" );
        root_ = new SearchTreeNode(null);
        textTree_ = createTree( root_ );

        JPanel mainPanel = new JPanel(new BorderLayout() );

         TwoPlayerPieceRenderer pieceRenderer =
                (TwoPlayerPieceRenderer)((AbstractTwoPlayerBoardViewer)boardViewer_).getPieceRenderer();
        treeViewer_ =
                new GameTreeViewer(root_, cellRenderer_.getColorMap(), pieceRenderer);
        treeViewer_.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        treeViewer_.setPreferredSize(new Dimension(500, 120));

        scrollPane_ = new JScrollPane();
        scrollPane_.setViewportView( textTree_);
        scrollPane_.setPreferredSize( new Dimension( 400, 600 ) );
        scrollPane_.setMinimumSize(new Dimension(200, 120));

        // the graphical tree goes below the top split pane.
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, createTopSplitPane(), treeViewer_ );

        mainPanel.add( splitPane, BorderLayout.CENTER );

        gameTreeButtons_ = new GameTreeButtons(this);
        mainPanel.add( gameTreeButtons_, BorderLayout.SOUTH );

        return mainPanel;
    }


    public synchronized JSplitPane createTopSplitPane() {

        JPanel previewPanel = new JPanel(new BorderLayout());

        ((AbstractTwoPlayerBoardViewer)boardViewer_).setPreferredSize( new Dimension( 200, 500 ) );

        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        infoPanel_ = new GameTreeInfoPanel();

        ContinuousColorLegend colorLegend =
                new ContinuousColorLegend("Relative Score for Player", cellRenderer_.getColorMap(), true);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(infoPanel_, BorderLayout.CENTER);
        infoPanel.add(colorLegend, BorderLayout.SOUTH);

        // this goes to the right of the test tree view
        viewerPanel.add( (AbstractTwoPlayerBoardViewer)boardViewer_, BorderLayout.CENTER);
        viewerPanel.add( infoPanel, BorderLayout.SOUTH);
        previewPanel.add( viewerPanel, BorderLayout.CENTER );

        return new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, scrollPane_, previewPanel );
    }

    /**
     * start over from scratch.
     */
    public synchronized void reset()
    {
        if (textTree_!=null) {
            textTree_.removeMouseMotionListener(this);
        }
        root_ = new SearchTreeNode(null);
        boardViewer_.reset();
        textTree_ = createTree( root_ );
        treeViewer_.setRoot(root_);
    }

    public synchronized SearchTreeNode getRootNode()
    {
        return root_;
    }

    public synchronized void treeExpanded( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
    }

    public synchronized void treeCollapsed( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
    }

    /**
     * called when the game has changed.
     * @param gce the event spawned when the game changed.
     */
    public synchronized void gameChanged( GameChangedEvent gce )
    {
        mainController_ = (TwoPlayerController)gce.getController();
        gameTreeButtons_.setMainController(mainController_);
        // it is possible that the size of the game has changed since the game tree controller
        // was initialized. Make sure that it is synched up.
        Board board = mainController_.getBoard();
        if ( board.getNumRows() != board_.getNumRows() || board.getNumCols() != board_.getNumCols() ) {
            board_.setSize( board.getNumRows(), board.getNumCols() );
        }

        // can't do it if we are in the middle of searching
        if (mainController_.isProcessing())  {
            return;
        }

        showCurrentGameTree();
        // reset the chain length
        oldChainLength_ = 0;
    }

    /**
     * show whatever portion of the game tree that has been searched so far.
     */
    public synchronized void showCurrentGameTree()
    {
        textTree_ = createTree( root_ );
        scrollPane_.setViewportView( textTree_ );

        treeViewer_.setRoot(root_);
        if (textTree_ !=null)  {
            textTree_.expandRow( 0 );
            textTree_.addMouseMotionListener( this );

            // make the viewer shows the game so far
            setMoveList( mainController_.getMoveList() );

            //textTree_.setVisibleRowCount(textTree_.getRowCount());
            refresh();
        }
    }


    /**
     * create the game tree representation
     * @param root of the game tree
     * @return the the java tree control itself
     */
    private synchronized JTree createTree( SearchTreeNode root )
    {
        JTree tree = null;

        try {
            tree = new JTree( root );

            ToolTipManager.sharedInstance().registerComponent( tree );

            tree.setBackground(UIManager.getColor( "Tree.textBackground" ));
            tree.setCellRenderer( cellRenderer_ );
            tree.setPreferredSize( new Dimension( TREE_WIDTH, 900 ) );
            tree.setShowsRootHandles( true );
            tree.putClientProperty( "JTree.lineStyle", "Angled" );
            tree.setRowHeight( ROW_HEIGHT );
            tree.addTreeExpansionListener( this );
        }
        catch (ArrayIndexOutOfBoundsException e) {
            GameContext.log(0,
                "Error: There was an ArayIndexOutOfBounds exception when creating a JTree from this root node: "+root);
            e.printStackTrace();
        }

        return tree;
    }


    /**
     * called when a particular move in the game tree has been selected by the user (by clicking on it or mouse-over).
     */
    private synchronized void selectCallback( MouseEvent e )
    {
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

    /**
     *  initialize the tree previewer to show the moves made so far.
     */
    private synchronized void setMoveList( List moveList )
    {
        boardViewer_.reset();
        // make sure that these are all permanent moves (what was this for?
        //Iterator it = moveList.iterator();
        //while ( it.hasNext() ) {
        //    it.next();
        //}
        // show in this debug window, and not the main viewer window.
        ((AbstractTwoPlayerBoardViewer)boardViewer_).showMoveSequence( moveList );
    }


    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e)
    {
        selectCallback( e );
    }

    /**
     * refresh the game tree.
     */
    private synchronized void refresh()
    {
        textTree_.setPreferredSize(
                new Dimension( TREE_WIDTH, textTree_.getRowCount() * ROW_HEIGHT ) );
        paint( getGraphics() );
    }


    /**
     * called when the ok button is clicked.
     */
    @Override
    public void close()
    {
        // if we set the root to null, then it doesnt have to build the tree
        controller_.setGameTreeListener( null );
        super.close();
    }

    /* --------------- GameTreeViewable implmentation ------------------*/
    // we need these methods to occur on the event dispatch thread to avoid
    // threading conflicts that could occur during concurrent rendering.


    public void addNode(final SearchTreeNode parent, final SearchTreeNode child, final int i) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.insert(child, i);
            }
        });
    }

    public void addPrunedNodes(final List list,  final SearchTreeNode parent,
                                                    final int i, final int val, final int thresh, final PruneType type) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.addPrunedChildNodes(list, i, val, thresh, type);
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

