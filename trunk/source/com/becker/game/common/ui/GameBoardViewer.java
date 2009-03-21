package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

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
 *  note: subclasses must override paintComponent to have the board show up.
 *
 *  @author Barry Becker
 */
public abstract class GameBoardViewer extends JPanel
                                                                  implements ViewerCallbackInterface,
                                                                                      MouseListener, GameChangedListener
{

    /** every GameBoardViewer must contain a controller. */
    protected GameController controller_ = null;

    /** for restoring undone moves. */
    protected final LinkedList<Move> undoneMoves_ = new LinkedList<Move>();

    private static JFileChooser chooser_ = null;

    // for firing events
    private final EventQueue evtq_;
    private final List<GameChangedListener> gameListeners_ = new ArrayList<GameChangedListener>();

    protected JProgressBar progressBar_ = null;
    protected Timer timer_ = null;

    protected final Cursor waitCursor_ = new Cursor( Cursor.WAIT_CURSOR );
    protected static Cursor origCursor_ = null;
    protected Frame parent_ = null;


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
     * @return the game specific controller for this viewer.
     */
    protected abstract GameController createController();

    /**
     * @return our game controller.
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
     * Derived classes should implement the details of the open.
     */
    public void openGame()
    {
        JFileChooser chooser = getFileChooser();
        int state = chooser.showOpenDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
            //lastDirectoryAccessed_ = file.getAbsolutePath();
            controller_.restoreFromFile(file.getAbsolutePath());
            sendGameChangedEvent(controller_.getLastMove());
        }
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     */
    public void saveGame( AssertionError ae )
    {
        JFileChooser chooser = getFileChooser();
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION ) {
            // if it does not have the .sgf extension already then add it
            String fPath = file.getAbsolutePath();
            fPath = SgfFileFilter.addExtIfNeeded(fPath, SgfFileFilter.SGF_EXTENSION);
            //if (!fPath.endsWith('.' + SgfFileFilter.SGF_EXTENSION))
            //    fPath += '.' + SgfFileFilter.SGF_EXTENSION;
            controller_.saveToFile( fPath, ae );
        }
    }

    private static JFileChooser getFileChooser() {
        if (chooser_ == null) {
            chooser_ = GUIUtil.getFileChooser();
            chooser_.setCurrentDirectory( new File( GameContext.getHomeDir() ) );
            chooser_.setFileFilter(new SgfFileFilter());
        }
        return chooser_;
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     */
    public void saveGame()
    {
       saveGame(null);
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
        if (g!=null) {
            this.paint(g);
        }
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

    /**
     * Each board must create its own renderer singleton.
     * @return gamve viewer specific renderer
     */
    protected abstract GameBoardRenderer getBoardRenderer();

    protected void commonReset(Board board)
    {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();

        setSize( getBoardRenderer().getSize(nrows, ncols));
        setPreferredSize( getBoardRenderer().getPreferredSize(nrows, ncols));
    }

    /**
     * start over with a new game using the current options.
     */
    public abstract void startNewGame();


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
        GameContext.log(1, "game changed" );
        this.refresh();
    }


    /**
     *  This method gets called when the game has changed in some way.
     *  Most likely because a move has been played.
     */
    public void sendGameChangedEvent(Move m)
    {
        GameChangedEvent gce = new GameChangedEvent( m, controller_, this );
        evtq_.postEvent( gce );
    }


    /**
     * @return true if there is a move to undo.
     */
    public final boolean canUndoMove()
    {
        return  (getBoard().getLastMove()!=null);
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
    @Override
    public void setBackground( Color c )
    {
        getBoardRenderer().setBackground(c);
        refresh();
    }

    /**
     * @return c  the board color
     */
    @Override
    public Color getBackground()
    {
        return getBoardRenderer().getBackground();
    }

    /**
     * @param c  the new color of the board's grid.
     */
    public void setGridColor( Color c )
    {
        getBoardRenderer().setGridColor(c);
        refresh();
    }

    /**
     * @return c  the new color of the board's grid.
     */
    public Color getGridColor()
    {
        return getBoardRenderer().getGridColor();
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
    @Override
    public void processEvent( AWTEvent evt )
    {
        if ( evt instanceof GameChangedEvent ) {
            for (GameChangedListener gcl : gameListeners_) {
                gcl.gameChanged((GameChangedEvent) evt);
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
     * This renders the current state of the Board to the screen.
     */
    @Override
    protected void paintComponent( Graphics g )
    {
        super.paintComponents( g );

        getBoardRenderer().render( g, controller_, this.getWidth(), this.getHeight());
    }

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
        GameContext.log(1, "An assertion failed. Writing to error file." );
        ae.printStackTrace();
        // make sure the state of the game at the point of the error is displayed.
        this.refresh();
        saveGame();
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