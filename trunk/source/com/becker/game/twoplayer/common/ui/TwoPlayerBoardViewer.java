package com.becker.game.twoplayer.common.ui;

import com.becker.common.Util;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.*;
import com.becker.optimization.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;

/**
 * This class contains a TwoPlayerController and displays the current state of the Game.
 * The TwoPlayerController contains a Board which describes this state.
 * The game specific TwoPlayerController is created upon construction to be used internally.
 * This class contains all that is needed to render the board and its pieces.
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
 *
 *  note: subclasses must override paintComponent to have the board show up.
 *
 *  @author Barry Becker
 */
public abstract class TwoPlayerBoardViewer extends GameBoardViewer
                      implements MouseListener, GameChangedListener, TwoPlayerViewerCallbackInterface
{

    private static final int PROGRESS_UPDATE_DELAY = 700;
    private static final int PROGRESS_STEP_DELAY = 100;


    // show this cached board if we are in the middle of processing the next one
    // (to avoid concurrency problems)
    private Board cachedGameBoard_ = null;

    private static Cursor origCursor_ = null;

    // becomes true when stepping through the search
    private boolean stepping_ = false;


    /**
     * Construct the viewer.
     */
    public TwoPlayerBoardViewer()
    {
        super();
        controller_.setViewer(this);
    }

    /**
     *
     * @return the game specific controller for this viewer.
     */
    protected abstract GameController createController();

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
    public void setProgressBar(JProgressBar progressBar)
    {
        progressBar_ = progressBar;
    }


    /**
     * run many games and use hill-climbing to find optimal weights.
     */
    private void runOptimization()
    {
        Optimizer optimizer = new Optimizer( get2PlayerController(), get2PlayerController().getAutoOptimizeFile() );

        ParameterArray optimizedParams;
        optimizedParams =
                optimizer.doOptimization( OptimizationType.HILL_CLIMBING,
                                          get2PlayerController().getComputerWeights().getDefaultWeights(),
                                          TwoPlayerController.WINNING_VALUE);

        JOptionPane.showMessageDialog(this, GameContext.getLabel("OPTIMIZED_WEIGHTS_TXT")+
                optimizedParams, GameContext.getLabel("OPTIMIZED_WEIGHTS"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * return the game to its original state.
     */
    public void reset()
    {
        controller_.reset();  //clear what's there and start over
        Board board = getBoard(); //controller_.getBoard();
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
    public final void startNewGame()
    {
        reset();
        TwoPlayerController c = get2PlayerController();
        if (get2PlayerController().isAutoOptimize())
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
        if ( GameContext.getUseSound() ) {
            GameContext.getMusicMaker().playNote( GameContext.getPreferredTone(), 45, 0, 200, 1000 );
        }
        // need to clear the cache, otherwise we may render a stale board.
        cachedGameBoard_ = null;
        get2PlayerController().manMoves( m );
        refresh();
        sendGameChangedEvent( m );
        return get2PlayerController().done( m, true );
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
    protected boolean doComputerMove( final boolean isPlayer1 )
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
            get2PlayerController().requestComputerMove( isPlayer1 );
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
    class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt) {
            int percentDone = get2PlayerController().getSearchStrategy().getPercentDone();
            progressBar_.setValue( percentDone );
            String note = GameContext.getLabel("MOVES_CONSIDERED")
                   + get2PlayerController().getSearchStrategy().getNumMovesConsidered()
                   + "  ("+ percentDone +"%)";

            progressBar_.setToolTipText(note);
            progressBar_.setString(note);

            if (stepping_) {
                stepping_ = false;
                get2PlayerController().pause();
            }
        }
    }


    /**
     * called when the controller has found the computer's move (usually after a long asynchronous search).
     *  The runnable body will run on the event-dispatch thread when the search has completed.
     * @param m the move that was selected by the computer.
     */
    public void computerMoved(final Move m)
    {
        final Runnable doComputerMoved = new Runnable() {
            public void run() {

                timer_.stop();

                setCursor( origCursor_ );
                if ( GameContext.getUseSound() )
                    GameContext.getMusicMaker().playNote( GameContext.getPreferredTone(), 45, 0, 200, 1000 );
                showLastMove();
                cachedGameBoard_ = null;
                if (!get2PlayerController().isAutoOptimize()) {
                    // show a popup for certain exceptional cases.
                    // For example, in chess we warn on a checking move.
                    warnOnSpecialMoves( (TwoPlayerMove)m );
                    sendGameChangedEvent( m );
                }
                if (progressBar_ != null) {
                    progressBar_.setValue(0);
                    progressBar_.setString("");
                }
           }
      };

      SwingUtilities.invokeLater(doComputerMoved);
    }

    protected void drawLastMoveMarker(Graphics2D g2)
    {
        // this draws a small indicator on the last move to show where it was played
        TwoPlayerMove last = (TwoPlayerMove)controller_.getLastMove();
        if ( last != null ) {
            g2.setColor( LAST_MOVE_INDICATOR_COLOR );
            g2.setStroke(LAST_MOVE_INDICATOR_STROKE);
            int xpos = BOARD_MARGIN + (last.getToCol() - 1) * cellSize_ + 1;
            int ypos = BOARD_MARGIN + (last.getToRow() - 1) * cellSize_ + 1;
            g2.drawOval( xpos, ypos, cellSize_ - 2, cellSize_ - 2 );
        }
    }


    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    public void gameChanged(GameChangedEvent evt)
    {
        // note: we don't show the winner dialog if we are optimizing the weights.
        if (get2PlayerController().done( (TwoPlayerMove)evt.getMove(), true) && !get2PlayerController().isAutoOptimize())
            this.showWinnerDialog();
        else {
            if (get2PlayerController().allPlayersComputer())
              continuePlay((TwoPlayerMove)evt.getMove());
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
         TwoPlayerController c = get2PlayerController();
         if (c.allPlayersComputer()) {
             refresh();
             doComputerMove( !m.player1 );
         }
         else {
             if ( c.isPlayer1sTurn() ) {
                 assert !c.isProcessing();
                 done = manMoves( m );
                 if ( !c.getPlayer2().isHuman() && !done )
                     doComputerMove( false );
             }
             else { // player 2s turn
                 done = manMoves( m );
                 if ( !c.getPlayer1().isHuman() && !done )
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
        c.makeMove( (Move) undoneMoves_.removeLast() );
        if ( !c.allPlayersHuman() ) {
            c.makeMove( (Move) undoneMoves_.removeLast() );
        }
        refresh();
    }




    /**
     * @return   the message to display at the completion of the game.
     */
    protected String getGameOverMessage()
    {
        String message;
        TwoPlayerController c = get2PlayerController();
        //String winner = "";
        boolean p1won = c.getPlayer1().hasWon();
        boolean p2won = c.getPlayer2().hasWon();

        if ( !p1won && !p2won )
            message = GameContext.getLabel("TIE_MSG");
        else {
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
       if (c.isProcessing() && !c.isAutoOptimize()) {
           return cachedGameBoard_;
       }
       else {
           return c.getBoard();
       }
    }
}