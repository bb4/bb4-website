package com.becker.game.common.ui.viewer;

import com.becker.game.common.*;
import com.becker.game.common.board.Board;
import com.becker.game.common.ui.SgfFileFilter;
import com.becker.game.common.ui.panel.GameChangedEvent;
import com.becker.game.common.ui.panel.GameChangedListener;
import com.becker.ui.file.FileChooserUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
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
                                      implements GameViewable, GameChangedListener {
    /** every GameBoardViewer must contain a controller. */
    protected GameController controller_ = null;

    /** for restoring undone moves. */
    protected final MoveList undoneMoves_ = new MoveList();

    private ViewerMouseListener mouseListener_;

    /** for dispatching events */
    private final EventQueue evtq_;
    /** list of listeners for handling those events. */
    private final List<GameChangedListener> gameListeners_ = new ArrayList<GameChangedListener>();

    private JProgressBar progressBar_;

    protected final Cursor waitCursor_ = new Cursor( Cursor.WAIT_CURSOR );
    protected Cursor origCursor_ = null;
    protected JFrame parent_ = null;


    /**
     * Construct the viewer.
     */
    public GameBoardViewer() {
        controller_ = createController();
        controller_.setViewer(this);
        // = createController();  used to do this, but I want only one controller, while I may have several viewers.
        evtq_ = Toolkit.getDefaultToolkit().getSystemEventQueue();
        enableEvents( 0 );

        // this activates tooltip text for the component
        this.setToolTipText( "" );
        ToolTipManager.sharedInstance().setDismissDelay( 100000 );
        origCursor_ = this.getCursor();

        mouseListener_ = createViewerMouseListener();
        addMouseListener( mouseListener_ );
        addMouseMotionListener(mouseListener_);
        // add a listener so that we realize when the computer (or human) has finished making his move
        addGameChangedListener(this);
    }

    protected ViewerMouseListener createViewerMouseListener() {
        return new ViewerMouseListener(this);
    }

    /**
     * @return the game specific controller for this viewer.
     */
    protected abstract GameController createController();

    /**
     * @return our game controller.
     */
    public GameController getController() {
       return controller_;
    }

    /**
     * @param progressBar an optional progress bar for showing progress as the computer thinks about its next move.
     */
    public void setProgressBar(JProgressBar progressBar) {
        progressBar_ = progressBar;
    }

    /**
     * restore a game from a previously saved file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the open.
     */
    public void openGame() {
        JFileChooser chooser = FileChooserUtil.getFileChooser(new SgfFileFilter());
        int state = chooser.showOpenDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
            controller_.restoreFromFile(file.getAbsolutePath());
            sendGameChangedEvent(controller_.getLastMove());
            this.refresh();
        }
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     */
    public void saveGame() {
       saveGame(null);
    }
    
    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     */
    void saveGame( AssertionError ae ) {
        JFileChooser chooser = FileChooserUtil.getFileChooser(new SgfFileFilter());
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION ) {
            // if it does not have the .sgf extension already then add it
            String fPath = file.getAbsolutePath();
            fPath = SgfFileFilter.addExtIfNeeded(fPath, SgfFileFilter.SGF_EXTENSION);
            controller_.saveToFile( fPath, ae );
        }
    }


    /**
     * Cause the board UI to draw itself based on the current state of the game.
     */
    public void refresh() {
        this.repaint();
        // this will paint the component immediately - no good.
        //if (this.getGraphics() != null) {
        //    this.paint( this.getGraphics() );
        //}
    }

    /**
     *  Animate the last move so the player does not lose orientation.
     *  By default this just redraws the board, but for games with complex moves,
     *  we may want to do more.
     */
    protected void showLastMove() {
        this.repaint();
    }

    /**
     * return the game to its original state.
     */
    public void reset() {
        controller_.reset();  //clear what's there and start over
        Board board = controller_.getBoard();
        commonReset(board);
    }

    /**
     * Each board must create its own renderer singleton.
     * @return game viewer specific renderer
     */
    protected abstract GameBoardRenderer getBoardRenderer();

    protected void commonReset(Board board) {
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
    public void setViewOnly( boolean viewOnly ) {
        if ( viewOnly ) {
            removeMouseListener( mouseListener_ );
            removeMouseMotionListener( mouseListener_ );
        }
        else {
            addMouseListener( mouseListener_ );
            addMouseMotionListener(mouseListener_);
        }
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    public void gameChanged(GameChangedEvent evt) {
        GameContext.log(1, "game changed" );
        this.refresh();
    }

    /**
     * This method gets called when the game has changed in some way.
     * Most likely because a move has been played.
     */
    public void sendGameChangedEvent(Move m) {
        GameChangedEvent gce = new GameChangedEvent( m, controller_, this );
        evtq_.postEvent( gce );
    }

    /**
     * @return true if there is a move to undo.
     */
    public final boolean canUndoMove() {
        return  (controller_.getLastMove() != null);
    }

    /**
     * @return true if there is a move to redo.
     */
     public final boolean canRedoMove() {
         return  !undoneMoves_.isEmpty();
     }

    /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    public void showWinnerDialog() {
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
    public void setBackground( Color c ) {
        getBoardRenderer().setBackground(c);
        refresh();
    }

    /**
     * @return c  the board color
     */
    @Override
    public Color getBackground() {
        return getBoardRenderer().getBackground();
    }

    /**
     * @param c  the new color of the board's grid.
     */
    public void setGridColor( Color c ) {
        getBoardRenderer().setGridColor(c);
        refresh();
    }

    /**
     * @return c  the new color of the board's grid.
     */
    public Color getGridColor()  {
        return getBoardRenderer().getGridColor();
    }

    /**
     * This is how the client can register itself to receive these events.
     * @param gcl the listener to add
     */
    public void addGameChangedListener( GameChangedListener gcl ) {
        gameListeners_.add(gcl);
    }

    /**
     * This is how the client can unregister itself to receive these events.
     * @param gcl the listener  to remove
     */
    private void removeGameChangedListener( GameChangedListener gcl ) {
        gameListeners_.remove(gcl);
    }

    /**
     * This overrides Component's processEvent.
     */
    @Override
    public void processEvent( AWTEvent evt ) {
        if ( evt instanceof GameChangedEvent) {
            for (GameChangedListener gcl : gameListeners_) {
                gcl.gameChanged((GameChangedEvent) evt);
            }
        }
        else  {
            // defer to the super's handling
            super.processEvent( evt );
        }
    }

    /**
     * This renders the current state of the Board to the screen.
     */
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponents( g );

        getBoardRenderer().render( g, controller_, this.getWidth(), this.getHeight());
    }

    /**
     * @return the cached game board if we are in the middle of processing.
     */
    public Board getBoard() {
        return controller_.getBoard();
    }

    /**
     * implements the AssertHandler interface.
     * It gets called whenever an assertion fails.
     */
    protected void assertFailed( AssertionError ae ) {
        GameContext.log(1, "An assertion failed. Writing to error file." );
        ae.printStackTrace();
        // make sure the state of the game at the point of the error is displayed.
        this.refresh();
        saveGame();
    }

    /**
     * do any needed cleanup.
     */
    public void dispose() {
        removeMouseListener( mouseListener_ );
        removeMouseMotionListener( mouseListener_);
        removeGameChangedListener( this );
    }
}