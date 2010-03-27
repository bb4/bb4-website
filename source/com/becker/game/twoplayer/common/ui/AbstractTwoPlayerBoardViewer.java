package com.becker.game.twoplayer.common.ui;

import com.becker.common.util.Util;
import com.becker.game.common.Board;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.common.ui.GamePieceRenderer;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerViewable;
import com.becker.optimization.parameter.ParameterArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;

/**
 * This class contains a TwoPlayerController and displays the current state of the Game.
 * The TwoPlayerController contains a Board which describes this state.
 * The game specific TwoPlayerController is created upon construction to be used internally.
 * This class delegates to a boardRenderer to render the board and its pieces.
 * There should be no references to swing classes outside the ui subpackage.
 *     This class sends a GameChangedEvent after each move in case there are other
 * components (like the GameTreeViewer) that need to update based on the new board state.
 * Since the computer can take a long time to think about its move before playing it, that
 * computation is handled asynchronously in a separate thread. The way it works is that the
 * TwoPlayerBoardViewer requests the next move from the controller (controller.requestComputerMove(p1)).
 * The controller spawns a new thread to actually do the search for the next best move.
 * When the next best move has been found, the controller calls computerMoved on the viewer
 * (using the TwoPlayerViewerCallbackInterface that it implement) to let it know that the move has been
 * found. The instructions in the computerMoved method are called using SwingUtilities.invokeLater()
 * so that they get executed on the event dispatch thread as soon as the event dispatch
 * thread is not busy doing something else (like refreshing the visible board).
 * A progress bar is used to show how close the computer is to playing its next move.
 * The progressbar updates by polling the controller for its search progress.
 * If you open the GameTreeDialog to see the game tree, there are buttons to pause,
 * step through, and continue processing the search as it is happenning.
 *
 * This class displays the game and takes input from the user.
 * It passes the user's input to the TwoPlayerController, which in turn tells the GameViewer
 * things such as whether the user's move was legal or not, and also tells the GameViewer
 * what the computer's move is.
 *
 *  @author Barry Becker
 */
public abstract class AbstractTwoPlayerBoardViewer extends GameBoardViewer
                      implements GameChangedListener, TwoPlayerViewable
{

    private static final int PROGRESS_UPDATE_DELAY = 700;
    private static final int PROGRESS_STEP_DELAY = 100;
    private static final short FUTURE_MOVE_TRANSP = 190;

    /**
     * Show this cached board if we are in the middle of processing the next one
     * (to avoid concurrency problems)
     */
    private Board cachedGameBoard_ = null;

    /** becomes true when stepping through the search.   */
    private boolean stepping_ = false;

    /** we occasionally want to show the conputers considered next moves in the ui. */
    private TwoPlayerMove[] nextMoves_;


    /**
     * Construct the viewer.
     */
    public AbstractTwoPlayerBoardViewer()
    {
        controller_.setViewer(this);
    }

    /**
     * @return our game controller
     */
    public TwoPlayerController get2PlayerController()
    {
       return (TwoPlayerController)controller_;
    }

    /**
     * set an optional progress bar for showing progress as the computer thinks about its next move.
     */
    @Override
    public void setProgressBar(JProgressBar progressBar)
    {
        progressBar_ = progressBar;
    }

    public TwoPlayerMove[] getNextMoves() {
        return nextMoves_;
    }

    public void setNextMoves(TwoPlayerMove[] nextMoves) {
        nextMoves_ = nextMoves;
    }

    public GamePieceRenderer getPieceRenderer() {
        return getBoardRenderer().getPieceRenderer();
    }


    /**
     * run many games and use hill-climbing to find optimal weights.
     */
    private void runOptimization()
    {
        ParameterArray optimizedParams = get2PlayerController().runOptimization();

        JOptionPane.showMessageDialog(this, GameContext.getLabel("OPTIMIZED_WEIGHTS_TXT")+
                optimizedParams, GameContext.getLabel("OPTIMIZED_WEIGHTS"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * return the game to its original state.
     */
    @Override
    public void reset()
    {
        controller_.reset();  //clear what's there and start over
        Board board = getBoard();
        commonReset(board);
    }

    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame()
    {
        reset();
        TwoPlayerController c = get2PlayerController();
        if (get2PlayerController().getTwoPlayerOptions().isAutoOptimize())
            runOptimization();

        if (c.allPlayersComputer() ) {
            c.computerMovesFirst();
            doComputerMove( false );
        }
        else if ( c.doesComputerMoveFirst() ) {
            // computer vs human opponent
            c.computerMovesFirst();
            refresh();
        }
        // for all other cases a human moves first
        // see the mouseClicked callback method for details
    }


    /**
     * register the humans move.
     * @param m the move to make.
     */
    private boolean manMoves( TwoPlayerMove m )
    {
        // this method will fill in some of m's structure
        TwoPlayerController c = get2PlayerController();
        if ( GameContext.getUseSound() ) {
            GameContext.getMusicMaker().playNote( c.getTwoPlayerOptions().getPreferredTone(), 45, 0, 200, 1000 );
        }
        // need to clear the cache, otherwise we may render a stale board.
        cachedGameBoard_ = null;
        c.manMoves( m );
        refresh();
        sendGameChangedEvent( m );
        return c.getSearchable().done( m, true );
    }


    public void showComputerVsComputerGame()
    {
        boolean done = false;
        while ( !done ) {
            done = doComputerMove( false );
            // if done the final move was placed
            if ( !done ) {
                done = doComputerMove( true );
            }
        }
    }


    /**
     * make the computer move and show it on the screen.
     * Since this can take a very long time we will show the user a progress bar
     * to give feedback.
     *   The computer needs to search through vast numbers of moves to find the best one.
     * This will happen asynchrounously in a separate thread so that the event dispatch
     * thread can return immediately and not lock up the user interface (UI).
     *   Some moves can be complex (like multiple jumps in checkers). For these
     * We animate these types of moves so the human player does not get disoriented.
     *
     * @param isPlayer1 if the computer player now moving is player 1.
     * @return done always returns false unless auto optimizing
     */
    protected boolean doComputerMove( boolean isPlayer1 )
    {
        setCursor( waitCursor_ );

        if (progressBar_ != null) {
            // initialize the progress bar if there is one.
            progressBar_.setValue(0);
            progressBar_.setVisible(true);

            // start a thread to update the progress bar at fixed time intervals
            // The timer gets killed when the worker thread is done searching.
            timer_ = new Timer(PROGRESS_UPDATE_DELAY, new TimerListener());

            timer_.start();
        }

        try {
            // this will spawn the worker thread and return immediately
            get2PlayerController().requestComputerMove( isPlayer1);
        }
        catch  (AssertionError ae) {
            // if any errors occur during search, I want to save the state of the game to
            // a file so the error can be easily reproduced.
            assertFailed( ae );
        }

        return false;
    }


    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds.
     */
    public final void step()
    {
        if (timer_ != null) {
            timer_.setDelay(PROGRESS_STEP_DELAY);
            timer_.restart();
            stepping_ = true;
            get2PlayerController().getSearchStrategy().continueProcessing();
        }
        else {
            GameContext.log(0,  "step error : timer is null" );
        }
    }

    /**
     * resume computation
     */
    public final void continueProcessing()
    {
        if (get2PlayerController().getSearchStrategy()!=null) {
            timer_.setDelay(PROGRESS_UPDATE_DELAY);
            get2PlayerController().getSearchStrategy().continueProcessing();
        }
    }


    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    private class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt) {
            if (get2PlayerController().getSearchStrategy() == null) return;
            int percentDone = get2PlayerController().getSearchStrategy().getPercentDone();
            progressBar_.setValue( percentDone );
            String numMoves = Util.formatNumber(get2PlayerController().getSearchStrategy().getNumMovesConsidered());
            String note = GameContext.getLabel("MOVES_CONSIDERED") + ' '
                   + numMoves + "  ("+ percentDone +"%)";

            progressBar_.setToolTipText(note);
            progressBar_.setString(note);

            if (stepping_) {
                stepping_ = false;
                get2PlayerController().pause();
            }
        }
    }


    /**
     * Called when the controller has found the computer's move (usually after a long asynchronous search).
     *  The runnable body will run on the event-dispatch thread when the search has completed.
     * @param m the move that was selected by the computer.
     */
    public void computerMoved(final Move m)
    {
        final Runnable postMoveCleanup = new PostMoveCleanup(m);

      SwingUtilities.invokeLater(postMoveCleanup);
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    @Override
    public void gameChanged(GameChangedEvent evt)
    {
        TwoPlayerController c = get2PlayerController();
        // note: we don't show the winner dialog if we are optimizing the weights.
        if (c.getSearchable().done( (TwoPlayerMove)evt.getMove(), true) && !c.getTwoPlayerOptions().isAutoOptimize())
            showWinnerDialog();
        else {
            if (get2PlayerController().allPlayersComputer()) {
                continuePlay((TwoPlayerMove)evt.getMove());
            }
        }
    }

    /**
      * let the computer go next if one of the players is a computer.
      *
      * @param m the current move
      * @return false if the game is at an end, otherwise return true
      */
     protected final boolean continuePlay( TwoPlayerMove m )
     {
         boolean done = false;
         TwoPlayerController contoller = get2PlayerController();
         if (contoller.allPlayersComputer()) {
             refresh();
             doComputerMove( !m.isPlayer1() );
         }
         else {
             if ( contoller.isPlayer1sTurn() ) {
                 assert !contoller.isProcessing();
                 done = manMoves( m );
                 if ( !contoller.getPlayer2().isHuman() && !done )
                     doComputerMove( false );
             }
             else { // player 2s turn
                 done = manMoves( m );
                 if ( !contoller.getPlayer1().isHuman() && !done )
                     doComputerMove( true );
             }

         }
         return !done;
         // we should check the memory here
     }


    /**
     * some moves require that the human players be given some kind of notification.
     * @param m the last move made
     */
    public void warnOnSpecialMoves( TwoPlayerMove m )
    {
        if (m == null)
            return;
        if (m.isPassingMove() && !get2PlayerController().allPlayersComputer())
            JOptionPane.showMessageDialog( this,
                    GameContext.getLabel("COMPUTER_PASSES"),
                    GameContext.getLabel("INFORMATION"),
                    JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * return the game to its state before the last human move.
     */
    public void undoLastManMove()
    {
        TwoPlayerController c = get2PlayerController();
        if ( c.allPlayersComputer() )
            return;
        Move move = c.undoLastMove();
        if ( move != null ) {
            undoneMoves_.add( move );
            if ( !c.allPlayersHuman() ) {
                undoneMoves_.add( c.undoLastMove() );
            }
            refresh();
        }
        else
            JOptionPane.showMessageDialog( this,
                    GameContext.getLabel("NO_MOVES_TO_UNDO"),
                    GameContext.getLabel("WARNING"),
                    JOptionPane.WARNING_MESSAGE );
    }

    /**
     * redo the last human player's move.
     */
    public void redoLastManMove()
    {
        TwoPlayerController c = get2PlayerController();
        if ( undoneMoves_.isEmpty() ) {
            JOptionPane.showMessageDialog( null,
                    GameContext.getLabel("NO_MOVES_TO_REDO"),
                    GameContext.getLabel("WARNING"),
                    JOptionPane.WARNING_MESSAGE );
            return;
        }
        if ( c.allPlayersComputer() )
            return;
        c.makeMove(undoneMoves_.removeLast());
        if ( !c.allPlayersHuman() ) {
            c.makeMove(undoneMoves_.removeLast());
        }
        refresh();
    }



    public final synchronized void showMoveSequence( List moveSequence )
    {
        showMoveSequence( moveSequence, getController().getNumMoves() );
    }

    public final synchronized void showMoveSequence( List moveSequence, int numMovesToBackup)
    {
        showMoveSequence( moveSequence, getController().getNumMoves(), null);
    }


    /**
     * perform a sequence of moves from somewhere in the game;
     * not necessarily the start. We do, however,
     * assume the moves are valid. It is for display purposes only.
     *
     * @param moveSequence the list of moves to make
     * @param numMovesToBackup number of moves to undo before playing this move sequence
     * @param nextMoves all the child moves of the final move in the sequence
     *       (see subclass implementations for game specific usages)
     */
    public final synchronized void showMoveSequence( List moveSequence,
                                               int numMovesToBackup, TwoPlayerMove[] nextMoves )
    {
        if ( moveSequence == null || moveSequence.size() == 0 )
            return;
        Move firstMove = (Move) moveSequence.get( 0 );
        // the first time we click on a row in the tree, the controller has no moves.
        Move lastMove = getBoard().getLastMove();
        if ( lastMove == null ) {
            reset();
        }
        else {
            // we keep the original moves and just back up to firstMove.moveNumber.
            // number of steps to backup is # of most recent real moves minus
            // the first move in the sequence.
            int ct = 0;
            if (firstMove != null ) {
                while ( ct < numMovesToBackup ) {
                    getController().undoLastMove();
                    // I suppose this is possible
                    if (getBoard().getLastMove() == null) {
                        throw new IllegalArgumentException("Reached the end after backing up "
                                + ct + " out of " + numMovesToBackup + " steps." +
                                "\n moveSequence=" + moveSequence);
                    }
                    ct++;
                }
            }
        }

        int firstFuture = 0;
        for ( int i = 0; i < moveSequence.size(); i++ ) {
            TwoPlayerMove m =  (TwoPlayerMove) moveSequence.get( i );
            if (m.isFuture()) {
                if (firstFuture == 0) {
                    firstFuture = i;
                }
                m.getPiece().setAnnotation(Integer.toString(i - firstFuture + 1));
                m.getPiece().setTransparency(FUTURE_MOVE_TRANSP);
            }
            getController().makeMove(m);
        }
        setNextMoves(nextMoves);
        refresh();
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage()
    {
        String message;
        TwoPlayerController c = get2PlayerController();

        boolean p1won = c.getPlayer1().hasWon();
        boolean p2won = c.getPlayer2().hasWon();

        if ( !p1won && !p2won )
            message = GameContext.getLabel("TIE_MSG");
        else {
            assert (!(p1won && p2won)) : "Both players cannot be winners!";
            MessageFormat formatter = new MessageFormat(GameContext.getLabel("WON_MSG"));
            Object[] args = new String[5];
            args[0] = p1won? GameContext.getLabel("YOU") : GameContext.getLabel("THE_COMPUTER");
            args[1] = p1won? c.getPlayer1().getName() : c.getPlayer2().getName();
            args[2] = Integer.toString(c.getNumMoves());
            args[3] = Util.formatNumber(c.getStrengthOfWin());
            message = formatter.format(args);
        }
        return message;
    }


    /**
     * @return the cached game board if we are in the middle of processing.
     */
    @Override
    public Board getBoard()
    {
       TwoPlayerController c = get2PlayerController();
       if (cachedGameBoard_ == null) {
           try {
               cachedGameBoard_ = (Board)c.getBoard().clone();
           }
           catch (CloneNotSupportedException e) {
               e.printStackTrace();
           }
       }
       if (c.isProcessing() && !c.getTwoPlayerOptions().isAutoOptimize()) {
           return cachedGameBoard_;
       }
       else {
           return c.getBoard();
       }
    }

    private class PostMoveCleanup implements Runnable {
        private final Move m;

        public PostMoveCleanup(Move m) {
            this.m = m;
        }

        public void run() {

            timer_.stop();

            setCursor( origCursor_ );
            if ( GameContext.getUseSound() )
                GameContext.getMusicMaker().playNote(
                        get2PlayerController().getTwoPlayerOptions().getPreferredTone(), 45, 0, 200, 1000 );
            showLastMove();
            cachedGameBoard_ = null;
            if (!get2PlayerController().getTwoPlayerOptions().isAutoOptimize()) {
                // show a popup for certain exceptional cases.
                // For example, in chess we warn on a checking move.
                warnOnSpecialMoves( (TwoPlayerMove) m);
                sendGameChangedEvent(m);
            }
            if (progressBar_ != null) {
                progressBar_.setValue(0);
                progressBar_.setString("");
            }
       }
    }
}