package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * This class contains a GameController and displays the current state of the Game.
 * The GameController contains a Board which describes this state.
 * The game specific GameController is created upon construction to be used internally.
 * This class contains all that is needed to render the board and its pieces.
 * There should be no references to swing classes outside the ui subpackage.
 *
 * This class displays the game and takes input from the user.
 * It passes the user's input to the GameController, which in turn tells the GameViewer
 * things such as whether the user's move was legal or not, and also tells the GameViewer
 * what the computer's move is.
 *
 *
 *  note: subclasses must override paintComponent to have the board show up.
 *
 *  @author Barry Becker
 */
public abstract class GameBoardViewer
              extends JPanel
           implements ViewerCallbackInterface, MouseListener, GameChangedListener
{

    protected static final Font VIEWER_FONT = new Font( "SansSerif", Font.PLAIN, 8 );

    protected static final Color LAST_MOVE_INDICATOR_COLOR = new Color( 250, 150, 0 );
    protected static final Stroke LAST_MOVE_INDICATOR_STROKE = new BasicStroke(1);
    // dont allow the cells of the game board to get smaller than this
    protected static final int MINIMUM_CELL_SIZE = 10;


    // every GameBoardViewer must contain one of these
    protected GameController  controller_ = null;

    // the size of a game board cell where the pieces go
    protected int cellSize_;
    protected final Cursor waitCursor_ = new Cursor( Cursor.WAIT_CURSOR );
    // for restoring undone moves
    protected final LinkedList undoneMoves_ = new LinkedList();

    // to move pieces you drag them (if the move is valid)
    protected BoardPosition draggedPiece_ = null;
    // this copy of the dragged piece is only for show
    protected BoardPosition draggedShowPiece_ = null;
    // the color of the board (would be better to use an image)

    // singleton class for rendering the game pieces
    // we use a separate piece rendering class to avoid having ui in the piece class itself.
    // This allows us to more cleanly separate the client pieces from the server.
    // this must be initialized in the derived classes constructor.
    protected GamePieceRenderer pieceRenderer_;

    // defaults for the grid and board colors.
    // The may be changed using the options panel in the ui.
    protected static final Color BACKGROUND_COLOR = GUIUtil.UI_COLOR_SECONDARY3;
    protected static final Color GRID_COLOR = GUIUtil.UI_COLOR_SECONDARY1;
    public static final int BOARD_MARGIN = 6;
    protected Color backgroundColor_ = BACKGROUND_COLOR;
    protected Color gridColor_;

    // for firing events
    private EventQueue evtq_;
    private ArrayList gameListeners_ = new ArrayList();

    protected JProgressBar progressBar_ = null;
    protected Timer timer_ = null;

    protected static Cursor origCursor_ = null;
    protected Frame parent_;



    /**
     * Construct the viewer.
     */
    public GameBoardViewer()
    {
        controller_ = createController();
        controller_.setViewer(this);
        // = createController();  used to do this, but I want only one controller, while I may have several viewers.
        evtq_ = Toolkit.getDefaultToolkit().getSystemEventQueue();
        enableEvents( 0 );
        cellSize_ = getDefaultCellSize();
        gridColor_ = getDefaultGridColor();

        // this activates tooltip text for the component
        this.setToolTipText( "" );
        ToolTipManager.sharedInstance().setDismissDelay( 100000 );
        origCursor_ = this.getCursor();

        addMouseListener( this );
        // add a listener so that we realize when the computer (or human) has finished making his move
        addGameChangedListener(this);
    }

    public void setParentFrame(Frame parent) {
        parent_ = parent;
    }

    /**
     *
     * @return the game specific controller for this viewer.
     */
    protected abstract GameController createController();

    /**
     * @return our game controller
     */
    public GameController getController()
    {
       return controller_;
    }
    /**
     * set an optional progress bar for showing progress as the computer thinks about its next move.
     */
    public void setProgressBar(JProgressBar progressBar)
    {
        progressBar_ = progressBar;
    }

    /**
     * restore a game from a previously saved file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the open
     */
    public void openGame()
    {
        JFileChooser chooser = GUIUtil.getFileChooser();
        chooser.setCurrentDirectory( new File( GameContext.getHomeDir() ) );
        int state = chooser.showOpenDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
            controller_.restoreFromFile(file.getAbsolutePath());
            refresh();
        }
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     */
    public void saveGame( AssertionError ae )
    {
        JFileChooser chooser = GUIUtil.getFileChooser();
        chooser.setCurrentDirectory( new File( GameContext.getHomeDir() ) );
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )
            controller_.saveToFile( file.getAbsolutePath(), ae );
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     */
    public void saveGame()
    {
       saveGame(null);
    }


    protected Color getDefaultGridColor()
    {
        return GRID_COLOR;
    }

    /**
     *  cause the board UI to draw itself based on the current state of the game.
     */
    public void refresh()
    {
        // this will paint the component immediately
        if (this.getGraphics() != null) {
            this.paint( this.getGraphics() );
        }

    }

    /**
     *  animate the last move so the player does not lose orientation.
     *  By default this just redraws the board, but for games with complex moves,
     *  we may want to do more.
     */
    public void showLastMove()
    {
        // this will paint the component immediately
        Graphics g = this.getGraphics();
        if (g!=null)
            this.paint(g);
    }


    /**
     * return the game to its original state.
     */
    public void reset()
    {
        controller_.reset();  //clear what's there and start over
        Board board = controller_.getBoard();
        commonReset(board);
    }

    protected void commonReset(Board board)
    {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        setSize( new Dimension( 2*BOARD_MARGIN + ncols * getCellSize(),
                                2*BOARD_MARGIN + nrows * getCellSize() ));
        setPreferredSize( new Dimension( 2*BOARD_MARGIN + ncols * getDefaultCellSize(),
                                         2*BOARD_MARGIN + nrows * getDefaultCellSize()) );
    }

    /**
     * start over with a new game using the current options.
     */
    public abstract void startNewGame();

    /**
     * @return  the size of a board position cell (must be square).
     */
    protected final int getCellSize()
    {
        return cellSize_;
    }

    /**
     * @return  default cell size (override for specific games).
     */
    protected int getDefaultCellSize()
    {
        return 16;
    }

    /**
     * @return the object that knows how to render the pieces.
     */
    public GamePieceRenderer getPieceRenderer()
    {
        return pieceRenderer_;
    }

    /**
     * in some cases the viewer is used to show games only.
     */
    public void setViewOnly( boolean viewOnly )
    {
        if ( viewOnly )
            removeMouseListener( this );
        else
            addMouseListener( this );
    }


    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    public void gameChanged(GameChangedEvent evt)
    {
        GameContext.log(1,  "game changed" );
        this.refresh();
    }


    /**
     *  This method gets called when the game has changed in some way.
     *  mMost likely because a move has been played.
     */
    public void sendGameChangedEvent(Move m)
    {
        GameChangedEvent gce = new GameChangedEvent( m, controller_, this );
        evtq_.postEvent( gce );
    }



    /**
     * perform a sequence of moves from somewhere in the game;
     * not necessarily the start. We do, however,
     * assume the moves are valid. It is for display purposes only.
     *
     * @param moveSequence the list of moves to make
     */
    public synchronized final void showMoveSequence( java.util.List moveSequence )
    {
        if ( moveSequence == null || moveSequence.size() == 0 )
            return;
        Move firstMove = (Move) moveSequence.get( 0 );
        // the first time we click on a row in the tree, the controller has no moves.
        if ( firstMove.moveNumber == 1 || controller_.getLastMove() == null ) {
            reset();
        }
        else {
            // we keep the original moves and just back up to firstMove.moveNumber.
            Move lastMove = controller_.getLastMove();
            // number of steps to backup is # of most recent real moves minus
            // the first move in the sequence.
            if ( lastMove != null && firstMove != null ) {
                while ( lastMove.moveNumber >= firstMove.moveNumber ) {
                    controller_.undoLastMove();
                    lastMove = controller_.getLastMove();
                    assert lastMove!=null :
                            "firstMove.moveNumber=" + firstMove.moveNumber + " moveSequence=" + moveSequence;
                }
            }
        }

        for ( int i = 0; i < moveSequence.size(); i++ ) {
            Move m =  (Move) moveSequence.get( i );
            controller_.makeMove(m);
        }
        refresh();
    }


    /**
     * @return true if there is a move to undo.
     */
    public final boolean canUndoMove()
     {
         return  (controller_.getLastMove()!=null);
     }

    /**
     * @return true if there is a move to redo.
     */
     public final boolean canRedoMove()
     {
         return  !undoneMoves_.isEmpty();
     }

    /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    protected void showWinnerDialog()
    {
        String message = getGameOverMessage();
        JOptionPane.showMessageDialog( this, message, GameContext.getLabel("GAME_OVER"),
                JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    protected abstract String getGameOverMessage();


    /**
     * @param c  the new color of the board.
     */
    public void setBackground( Color c )
    {
        backgroundColor_ = c;
        refresh();
    }

    /**
     * @return c  the board color
     */
    public Color getBackground()
    {
        return backgroundColor_;
    }

    /**
     * @param c  the new color of the board's grid.
     */
    public void setGridColor( Color c )
    {
        gridColor_ = c;
        refresh();
    }
    /**
     * @return c  the new color of the board's grid.
     */
    public Color getGridColor()
    {
        return gridColor_;
    }

    /**
     * This is how the client can register itself to receive these events.
     * @param gcl the listener to add
     */
    public void addGameChangedListener( GameChangedListener gcl )
    {
        gameListeners_.add(gcl);
    }

    /**
     * This is how the client can unregister itself to receive these events.
     * @param gcl the listener  to remove
     */
    private void removeGameChangedListener( GameChangedListener gcl )
    {
        gameListeners_.remove(gcl);
    }

    /**
     * This overrides Component's processEvent.
     */
    public void processEvent( AWTEvent evt )
    {
        if ( evt instanceof GameChangedEvent ) {
            for (int i=0; i < gameListeners_.size(); i++ ) {
                GameChangedListener gcl = (GameChangedListener)gameListeners_.get(i);
                gcl.gameChanged( (GameChangedEvent) evt );
            }
        }
        else
            super.processEvent( evt );  // defer to the super's handling
    }

    // ---  these methods implement the MouseListener interface   ---
    // make the human move and show it on the screen,
    // then depending on the options, the computer may move.
    // do nothing be default for all these. Subclasses must override some of them.
    public void mouseClicked( MouseEvent e ) {}
    public void mousePressed( MouseEvent e ) {}
    public void mouseReleased( MouseEvent e ) {}
    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}


    /**
     * Compute the cell sizes base on the the dimenions of the viewer
     * The viewer window may be resized causing the cell size to change dynamically
     * @param nrows
     * @param ncols
     * @return
     */
    private int calcCellSize( int nrows, int ncols )
    {
        int size;

        float panelWidth = (float) this.getWidth();
        float panelHeight = (float) this.getHeight();
        float panelAspect = panelWidth / panelHeight;
        float boardAspect = (float) ncols / (float) nrows;

        //GameContext.log(0, "compare "+boardAspect+"("+ncols+","+nrows+") to "+panelAspect+"("+panelWidth+","+panelHeight+") to ");
        if ( boardAspect < panelAspect )
            size = (int) ((panelHeight - 2*BOARD_MARGIN + 1) / nrows);
        else
            size = (int) ((panelWidth - 2*BOARD_MARGIN + 1) / ncols);

        return Math.max( size, MINIMUM_CELL_SIZE );
    }

    protected void drawBackground( Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        g.setColor( backgroundColor_ );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
    }

    /**
     * This renders the current state of the Board to the screen.
     */
    protected void paintComponent( Graphics g )
    {
        int i;
        int xpos, ypos;
        Board board = getBoard();
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        cellSize_ = calcCellSize( nrows, ncols );

        super.paintComponents( g );
        Graphics2D g2 = (Graphics2D)g;

        int gridOffset = 0;
        int start = 0;
        int nrows1 = nrows;
        int ncols1 = ncols;
        if ( offsetGrid() ) {
            gridOffset = cellSize_ / 2;
            nrows1 = nrows - 1;
            ncols1 = ncols - 1;
        }
        int startPos = BOARD_MARGIN + start * cellSize_ + gridOffset;

        int rightEdgePos = BOARD_MARGIN + cellSize_ * ncols1 + gridOffset;
        int bottomEdgePos = BOARD_MARGIN + cellSize_ * nrows1 + gridOffset;

        drawBackground( g, startPos, rightEdgePos, bottomEdgePos );

        g2.setFont( VIEWER_FONT );

        // draw the hatches which deliniate the cells
        g2.setColor( gridColor_ );

        for ( i = start; i <= nrows1; i++ )  //   -----
        {
            ypos = BOARD_MARGIN + i * cellSize_ + gridOffset;
            g2.drawLine( startPos, ypos, rightEdgePos, ypos );
        }
        for ( i = start; i <= ncols1; i++ )  //   ||||
        {
            xpos = BOARD_MARGIN + i * cellSize_ + gridOffset;
            g2.drawLine( xpos, startPos, xpos, bottomEdgePos );
        }

        // now draw both player markers
        drawMarkers( nrows, ncols, g2 );

        // if there is a piece being dragged, draw it
        if ( draggedShowPiece_ != null ) {
            pieceRenderer_.render(g2, draggedShowPiece_, cellSize_, board);
        }

        drawLastMoveMarker(g2);
    }

    /**
     * draw some indication of where the last move was made.
     */
    protected abstract void drawLastMoveMarker(Graphics2D g2);

    /**
     * @return the cached game board if we are in the middle of processing.
     */
    public Board getBoard()
    {
        return controller_.getBoard();
    }

    /**
     * implements the AssertHandler interface.
     * It gets called whenever an assertion fails.
     */
    protected void assertFailed( AssertionError ae )
    {
        GameContext.log(1,  "An assertion failed. Writing to error file." );
        ae.printStackTrace();
        // make sure the state of the game at the point of the error is displayed.
        this.refresh();
        saveGame();
    }

    /**
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {
        Board board = getBoard();
        for ( int i = 1; i <= nrows; i++ )
            for ( int j = 1; j <= ncols; j++ ) {
                pieceRenderer_.render(g2, board.getPosition( i, j ),  cellSize_, board);
            }
    }

    /**
     * whether or not to draw the pieces on cell centers or vertices (like go or pente, but not like checkers).
     */
    protected boolean offsetGrid()
    {
        return false;
    }

    /**
     * Constructs a new Location given a MouseEvent
     *
     * @param e  the row  coordinate.
     */
    public static Location createLocation( MouseEvent e, int cellSize)
    {
        int row = (e.getY()-BOARD_MARGIN)/cellSize + 1;
        int col = (e.getX()-BOARD_MARGIN)/cellSize + 1;
        return  new Location(row, col);
    }


    /**
     * do any needed cleanup.
     */
    public void dispose()
    {
        controller_.dispose();
        removeMouseListener( this );
        removeGameChangedListener( this );
    }
}