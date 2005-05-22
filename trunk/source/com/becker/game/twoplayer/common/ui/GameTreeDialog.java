package com.becker.game.twoplayer.common.ui;

import com.becker.common.ColorMap;
import com.becker.common.Util;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.search.SearchTreeNode;
import com.becker.game.twoplayer.common.*;
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

    private final JPanel mainPanel_ = new JPanel();
    private final JPanel previewPanel_ = new JPanel();
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
    private TwoPlayerControllerInterface mainController_ = null;

    private ColorMap colormap_ = null;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param boardViewer
     */
    public GameTreeDialog( JFrame parent, TwoPlayerBoardViewer boardViewer )
    {
        super( parent );
        boardViewer_ = boardViewer;
        controller_ = (TwoPlayerController)boardViewer.getController();
        board_ = controller_.getBoard();
        initColormap();

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {
            GameContext.log( 0, "we ran out of memory!" );
            GameContext.log( 0, GUIUtil.getStackTrace( oom ) );
        }
        pack();
    }

    /**
     * initialize the colormap used to color the gmae tree rows, nodes, and arcs.
     */
    private void initColormap()
    {
        TwoPlayerBoardViewer viewer = (TwoPlayerBoardViewer)boardViewer_;
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        // we will use this colormap for both the text tree and the graphical tree viewers so they have consistent coloring.
        final double[] values_ = {-TwoPlayerController.WINNING_VALUE, -TwoPlayerController.WINNING_VALUE/20.0,
                                              0.0,
                                              TwoPlayerController.WINNING_VALUE/20.0, TwoPlayerController.WINNING_VALUE};
        final Color[] colors_ = {renderer.getPlayer2Color().darker(),
                                            renderer.getPlayer2Color(),
                                            new Color( 160, 160, 160),
                                            renderer.getPlayer1Color(),
                                            renderer.getPlayer1Color().darker()};
        colormap_ = new ColorMap( values_, colors_ );
    }


    /**
     * ui initialization of the tree control.
     */
    private void initUI()
    {
        this.setTitle( "Game Tree" );

        setResizable( true );
        mainPanel_.setLayout( new BorderLayout() );

        root_ = new SearchTreeNode(null);
        textTree_ = createTree( root_ );
        treeViewer_ = new GameTreeViewer( root_, controller_.getLookAhead(), colormap_);
        treeViewer_.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        treeViewer_.setPreferredSize(new Dimension(500, 120));


        scrollPane_ = new JScrollPane();
        scrollPane_.setViewportView( textTree_);
        scrollPane_.setPreferredSize( new Dimension( 400, 600 ) );
        scrollPane_.setMinimumSize(new Dimension(200, 120));

        previewPanel_.setLayout( new BorderLayout() );

        ((TwoPlayerBoardViewer)boardViewer_).setPreferredSize( new Dimension( 200, 500 ) );
        //previewPanel_.setPreferredSize( new Dimension( 200, 600 ) );

        JPanel viewerPanel = new JPanel();
        //JPanel filler = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        infoLabel_.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5,5,5,5)));
        infoLabel_.setVerticalAlignment(JLabel.TOP);
        infoLabel_.setPreferredSize( new Dimension( 200, 260 ) );
        viewerPanel.add( (TwoPlayerBoardViewer)boardViewer_, BorderLayout.CENTER);
        viewerPanel.add( infoLabel_, BorderLayout.SOUTH);


        previewPanel_.add( viewerPanel, BorderLayout.CENTER );

        JSplitPane topSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, scrollPane_, previewPanel_ );
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, topSplitPane, treeViewer_ );

        mainPanel_.add( splitPane, BorderLayout.CENTER );

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
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
    }

    /**
     * start over from scratch.
     */
    public final void reset()
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

    public final SearchTreeNode getRootNode()
    {
        return root_;
    }

    public final void treeExpanded( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
        //treeViewer_.setRoot(root_, controller_.getLookAhead());
    }

    public final void treeCollapsed( TreeExpansionEvent e )
    {
        refresh();
        treeViewer_.refresh();
        //treeViewer_.setRoot(root_, controller_.getLookAhead());
    }

    /**
     * called when the game has changed.
     * @param gce the event spawned when the game changed.
     */
    public final void gameChanged( GameChangedEvent gce )
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
    private void showCurrentGameTree()
    {
        textTree_ = createTree( root_ );
        scrollPane_.setViewportView( textTree_ );

        treeViewer_.setRoot(root_, mainController_.getLookAhead());

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
        // @@ getting ArrayIndexOutOfBounds in DefaultMutableTreeNode/Vector here. bug in jdk1.5?
        JTree tree = new JTree( root );

        ToolTipManager.sharedInstance().registerComponent( tree );
        GameTreeCellRenderer cellRenderer =
                new GameTreeCellRenderer(colormap_);

        tree.setCellRenderer( cellRenderer );
        tree.setPreferredSize( new Dimension( TREE_WIDTH, 900 ) );
        tree.setShowsRootHandles( true );
        tree.putClientProperty( "JTree.lineStyle", "Angled" );
        tree.setRowHeight( ROW_HEIGHT );
        tree.addTreeExpansionListener( this );

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

                m.transparency = trans;
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
            String entity = "Human's move";
            Color c = renderer.getPlayer2Color();
            if ( m.player1 )
                c = renderer.getPlayer1Color();
            if ( (m.player1 && !controller_.getPlayer1().isHuman()) ||
                 (!m.player1 && !controller_.getPlayer2().isHuman()) )
                entity = "Computer's move";

            StringBuffer sBuf = new StringBuffer("<html>");
            sBuf.append("<font size=\"+1\" color="+GUIUtil.getHTMLColorFromColor(c)+" bgcolor=#99AA99>"+entity+"</font><br>");
            sBuf.append("Static value = " + Util.formatNumber(m.value) +"<br>");
            sBuf.append("Inherited value = " + Util.formatNumber(m.inheritedValue) +"<br>");
            sBuf.append("Alpha = "+lastNode.alpha+"<br>");
            sBuf.append("Beta = "+lastNode.beta+"<br>");
            if (lastNode.comment!=null)
                sBuf.append(lastNode.comment+"<br>");
            sBuf.append("Number of descendants = "+lastNode.numDescendants+"<br>");
            if (m.urgent)
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
            m.transparency = 0;
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
        if ( source == pauseButton_ ) {
            pause();
        }
        else if ( source == stepButton_ ) {
            step();
        }
        else if ( source == continueButton_ ) {
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


    protected final void processWindowEvent( WindowEvent e )
    {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            this.dispose();
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
        this.setVisible(false);
    }

}

