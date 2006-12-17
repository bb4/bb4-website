package com.becker.game.twoplayer.common.ui;

import com.becker.common.Util;
import com.becker.game.common.Board;
import com.becker.game.common.GameContext;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerViewerCallbackInterface;
import com.becker.game.twoplayer.common.search.SearchTreeNode;
import com.becker.ui.legend.ContinuousColorLegend;
import com.becker.ui.GUIUtil;
import com.becker.ui.GradientButton;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Draw the entire game tree using a java tree control.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the GameTreeViewer at the bottom.
 *
 * @author Barry Becker
 */
public final class GameTreeDialog extends JDialog
                               implements ActionListener,
        GameChangedListener,
        MouseMotionListener,
        TreeExpansionListener
{
    /**
     * the options get set directly on the game controller that is passed in.
     */
    private TwoPlayerController controller_;

    private JScrollPane scrollPane_ = null;
    private GameTreeViewer treeViewer_ = null;
    private JTree textTree_ = null;
    private SearchTreeNode root_ = null;
    private int oldChainLength_ = 0;

    private final GradientButton pauseButton_ = new GradientButton();
    private final GradientButton stepButton_ = new GradientButton();
    private final GradientButton continueButton_ = new GradientButton();
    private final GradientButton closeButton_ = new GradientButton();

    private final JLabel infoLabel_ = new JLabel();

    private static final int ROW_HEIGHT = 11;
    private static final int TREE_WIDTH = 420;

    private Board board_ = null;

    // the viewer in the debug window
    private TwoPlayerViewerCallbackInterface boardViewer_ = null;

    // the controller that is actually being played
    private TwoPlayerController mainController_ = null;

    GameTreeCellRenderer cellRenderer_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public GameTreeDialog( JFrame parent, TwoPlayerBoardViewer boardViewer, GameTreeCellRenderer cellRenderer )
    {
        super( parent );
        boardViewer_ = boardViewer;
        controller_ = (TwoPlayerController)boardViewer.getController();
        board_ = controller_.getBoard();
        cellRenderer_ = cellRenderer;

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (OutOfMemoryError oom) {
            GameContext.log( 0, "we ran out of memory!" );
            GameContext.log( 0, GUIUtil.getStackTrace( oom ) );
            throw oom;
        }
        pack();
    }


    /**
     * ui initialization of the tree control.
     */
    private void initUI()
    {
        JPanel mainPanel = new JPanel();
        JPanel previewPanel = new JPanel();

        setTitle( "Game Tree" );

        setResizable( true );
        mainPanel.setLayout( new BorderLayout() );

        root_ = new SearchTreeNode(null);
        textTree_ = createTree( root_ );

        TwoPlayerPieceRenderer pieceRenderer =
                (TwoPlayerPieceRenderer)((TwoPlayerBoardViewer)boardViewer_).getPieceRenderer();
        treeViewer_ =
                new GameTreeViewer( root_, controller_.getTwoPlayerOptions().getLookAhead(),
                                    cellRenderer_.getColorMap(), pieceRenderer);
        treeViewer_.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        treeViewer_.setPreferredSize(new Dimension(500, 120));


        scrollPane_ = new JScrollPane();
        scrollPane_.setViewportView( textTree_);
        scrollPane_.setPreferredSize( new Dimension( 400, 600 ) );
        scrollPane_.setMinimumSize(new Dimension(200, 120));

        previewPanel.setLayout( new BorderLayout() );

        ((TwoPlayerBoardViewer)boardViewer_).setPreferredSize( new Dimension( 200, 500 ) );

        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        infoLabel_.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5,5,5,5)));
        ContinuousColorLegend colorLegend =
                new ContinuousColorLegend("Relative Score for Player", cellRenderer_.getColorMap(), true);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.add(infoLabel_, BorderLayout.CENTER);
        infoPanel.add(colorLegend, BorderLayout.SOUTH);

        viewerPanel.add( (TwoPlayerBoardViewer)boardViewer_, BorderLayout.CENTER);
        viewerPanel.add( infoPanel, BorderLayout.SOUTH);


        previewPanel.add( viewerPanel, BorderLayout.CENTER );


        JSplitPane topSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, scrollPane_, previewPanel );
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, topSplitPane, treeViewer_ );

        mainPanel.add( splitPane, BorderLayout.CENTER );

        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        pauseButton_.setText( "Pause" );
        pauseButton_.setToolTipText( "Pause processing" );
        pauseButton_.addActionListener( this );

        stepButton_.setText( "Step" );
        stepButton_.setToolTipText( "Step forward through the search computation" );
        stepButton_.addActionListener( this );
        stepButton_.setEnabled(false);

        continueButton_.setText( "Continue" );
        continueButton_.setToolTipText( "Continue searching for the next move" );
        continueButton_.addActionListener( this );
        continueButton_.setEnabled(false);

        closeButton_.setText( "Close" );
        closeButton_.setToolTipText( "Hide the Game Tree Viewer" );
        closeButton_.addActionListener( this );

        buttonsPanel.add( pauseButton_ );
        buttonsPanel.add( stepButton_ );
        buttonsPanel.add( continueButton_ );
        buttonsPanel.add( closeButton_ );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
    }

    /**
     * start over from scratch.
     */
    public void reset()
    {
        if (textTree_!=null) {
            textTree_.removeMouseMotionListener(this);
            //textTree_.removeMouseListener(this);
        }
        root_ = new SearchTreeNode(null);
        boardViewer_.reset();
        textTree_ = createTree( root_ );
        treeViewer_.setRoot(root_, 0);
    }

    public SearchTreeNode getRootNode()
    {
        return root_;
    }

    public void treeExpanded( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
        //treeViewer_.setRoot(root_, controller_.getLookAhead());
    }

    public void treeCollapsed( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
        //treeViewer_.setRoot(root_, controller_.getLookAhead());
    }

    /**
     * called when the game has changed.
     * @param gce the event spawned when the game changed.
     */
    public void gameChanged( GameChangedEvent gce )
    {
        mainController_ = (TwoPlayerController)gce.getController();
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
    }

    /**
     * show whatever portion of the game tree that has been searched so far
     */
    private synchronized void showCurrentGameTree()
    {
        textTree_ = createTree( root_ );
        scrollPane_.setViewportView( textTree_ );

        treeViewer_.setRoot(root_, mainController_.getTwoPlayerOptions().getLookAhead());

        textTree_.expandRow( 0 );

        //textTree.addMouseListener( this);
        textTree_.addMouseMotionListener( this );

        // make the viewer shows the game so far
        setMoveList( mainController_.getMoveList() );

        //textTree_.setVisibleRowCount(textTree_.getRowCount());
        refresh();
    }


    /**
     * create the game tree representation
     * @param root of the game tree
     * @return the the java tree control itself
     */
    private JTree createTree( SearchTreeNode root )
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
     * @param e
     */
    private void selectCallback( MouseEvent e )
    {
        JTree t = (JTree) e.getSource();

        if (mainController_.isProcessing() && !mainController_.isPaused())  {
            // avoid concurrency problems
            return;
        }

        int row = t.getRowForLocation( e.getX(), e.getY() );
        if ( row != -1 ) {
            TreePath path = t.getPathForRow( row );

            treeViewer_.highlightPath( path );

            int chainLength = path.getPathCount();
            Object[] nodes = path.getPath();
            SearchTreeNode lastNode = (SearchTreeNode)nodes[chainLength-1];
            java.util.List moveList = new LinkedList();
            TwoPlayerMove m = null;
            for ( int i = 0; i < chainLength; i++ ) {
                SearchTreeNode node = (SearchTreeNode) nodes[i];
                m = (TwoPlayerMove) node.getUserObject();
                if ( m == null )
                    return; // no node here
                short trans = 0;
                if ( i > 0 ) {
                    trans = (short) (50 + 30 * i);
                }
                if ( trans > 240 )
                    trans = 240;

                m.setTransparency(trans);
                moveList.add( m );
            }
            // also show the children of the final move in a special way (if there are any)
            SearchTreeNode finalNode = (SearchTreeNode) nodes[chainLength-1];

            TwoPlayerBoardViewer viewer = (TwoPlayerBoardViewer)boardViewer_;

            //viewer.showMoveSequence( moveList, oldChainLength_ );
            viewer.showMoveSequence( moveList, oldChainLength_, finalNode.getChildMoves() );

            // remember the old chain length so we know how much to back up next time
            oldChainLength_ = chainLength;

            TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
            String passSuffix = m.isPassingMove() ? " (Pass)" : "";
            String entity = "Human's move";
            Color c = renderer.getPlayer2Color();
            if ( m.isPlayer1() )
                c = renderer.getPlayer1Color();
            if ( (m.isPlayer1() && !controller_.getPlayer1().isHuman()) ||
                 (!m.isPlayer1() && !controller_.getPlayer2().isHuman()) )
                entity = "Computer's move";

            StringBuffer sBuf = new StringBuffer("<html>");
            sBuf.append("<font size=\"+1\" color="+GUIUtil.getHTMLColorFromColor(c) +
                        " bgcolor=#99AA99>" + entity + passSuffix + "</font><br>");
            sBuf.append("Static value = " + Util.formatNumber(m.getValue()) +"<br>");
            sBuf.append("Inherited value = " + Util.formatNumber(m.getInheritedValue()) +"<br>");
            sBuf.append("Alpha = "+Util.formatNumber(lastNode.getAlpha())+"<br>");
            sBuf.append("Beta = "+Util.formatNumber(lastNode.getBeta())+"<br>");
            if (lastNode.getComment()!=null)
                sBuf.append(lastNode.getComment()+"<br>");
            sBuf.append("Number of descendants = "+lastNode.getNumDescendants()+"<br>");
            if (m.isUrgent())
                sBuf.append( "<font color=#FF6611>Urgent move!</font>");
            sBuf.append("</html>");
            infoLabel_.setText(sBuf.toString());
        }
    }

    /**
     *  initialize the tree previewer to show the moves made so far.
     */
    private void setMoveList( java.util.List moveList )
    {
        boardViewer_.reset();
        // make sure that these are all permanent moves
        Iterator it = moveList.iterator();
        while ( it.hasNext() ) {
            TwoPlayerMove m = (TwoPlayerMove) it.next();
            // make the last one permanent looking.
            m.setTransparency((short) 0);
        }
        // show in this debug window, and not the main viewer window.
        ((TwoPlayerBoardViewer)boardViewer_).showMoveSequence( moveList );

    }

    /**
     * called when the ok button is clicked.
     */
    private void pause()
    {
        // if we set the root to null, then it doesn't have to build the tree
        pauseButton_.setEnabled(false);
        continueButton_.setEnabled(true);
        stepButton_.setEnabled(true);

        mainController_.pause();
        showCurrentGameTree();
    }

    /**
     * called when the ok button is clicked.
     */
    private void step()
    {
        mainController_.get2PlayerViewer().step();
        showCurrentGameTree();
    }

    /**
     * called when the ok button is clicked.
     */
    private void continueProcessing()
    {
        GameContext.log(1,  "continue" );
        pauseButton_.setEnabled(true);
        continueButton_.setEnabled(false);
        stepButton_.setEnabled(false);
        mainController_.get2PlayerViewer().continueProcessing();
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source.equals(pauseButton_) ) {
            pause();
        }
        else if ( source.equals(stepButton_) ) {
            step();
        }
        else if ( source.equals(continueButton_) ) {
            continueProcessing();
        }
        else {  // closeButton_
            close();
        }
    }


    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e)
    {
        selectCallback( e );
    }

    /**
     * refresh the game tree.
     */
    private void refresh()
    {
        textTree_.setPreferredSize(
                new Dimension( TREE_WIDTH, textTree_.getRowCount() * ROW_HEIGHT ) );
        paint( getGraphics() );
    }


    protected void processWindowEvent( WindowEvent e )
    {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            dispose();
        }
        super.processWindowEvent( e );
    }


    /**
     * called when the ok button is clicked.
     */
    private void close()
    {
        // if we set the root to null, then it doesnt have to build the tree
        controller_.setGameTreeRoot( null );
        setVisible(false);
    }

}

