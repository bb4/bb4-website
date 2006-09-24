package com.becker.game.twoplayer.common;

import com.becker.common.Util;
import com.becker.common.Worker;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.search.SearchStrategy;
import com.becker.game.twoplayer.common.search.SearchTreeNode;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.ParameterArray;
import com.becker.optimization.Optimizer;
import com.becker.optimization.OptimizationType;
import com.becker.optimization.Optimizee;
import com.becker.sound.MusicMaker;

import java.util.Collections;
import java.util.List;

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for 2 player zero sum games with perfect information.
 * Some examples include chess, checkers, go, othello, pente, blockade, mancala, nine-mens morris, etc.
 * It implements Optimizee because the games derived from this class
 * can be optimized to improve their playing ability.
 *
 * Instance of this class process requests from the GameViewer.
 * Specifically, the GameViewer tells the TwoPlayerController what move the human made,
 * and the TwoPlayerController returns information such as the computer's move.
 *
 *  @author Barry Becker
 */
public abstract class TwoPlayerController extends GameController
{

    // these are the default game constants
    // they may be modified through the ui (see GameOptionsDialog)

    // -- these other constants are game type dependent and could move to subclasses
    /** anything greater than this is considered a won game  */
    public static final double WINNING_VALUE = SearchStrategy.WINNING_VALUE;

    protected boolean player1sTurn_ = true;

    // these weights determine how the computer values each move.
    // they serve as parameters to a game dependent evaluation function
    protected GameWeights weights_;

    // the method the computer will use for searching for the next move.
    private SearchStrategy strategy_;

    // if this becomes non-null we will fill in the game tree for display in a UI.
    private SearchTreeNode root_;

    private Worker worker_;

    // this is true while the computer thinks about its next move.
    private boolean processing_ = false;

    private static final double HUNDRED = 100.0;


    /**
     * Construct the game controller.
     */
    public TwoPlayerController()
    {
        //options_ = createOptions();
        createPlayers();
    }


    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = new TwoPlayerOptions(4, 50, MusicMaker.TAIKO_DRUM);
        }
        return gameOptions_;
    }

    public TwoPlayerOptions getTwoPlayerOptions() {

        return (TwoPlayerOptions) getOptions();
    }

    public TwoPlayerViewerCallbackInterface get2PlayerViewer()
    {
       return (TwoPlayerViewerCallbackInterface)viewer_;
    }


    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        if (isProcessing()) {
            pause();
            if (worker_!=null) {
                worker_.interrupt();
                processing_ = false;
                // make the move even though we did not finish computing it
                get2PlayerViewer().computerMoved((Move)worker_.get());
            }
            Util.sleep(100);
        }
        super.reset();
        getPlayer1().setWon(false);
        getPlayer2().setWon(false);
        player1sTurn_ = true;
    }

    private void createPlayers()
    {
        Player[] players = new Player[2];
        players[0] = new Player(getTwoPlayerOptions().getPlayerName(true), null, true);
        players[1] = new Player(getTwoPlayerOptions().getPlayerName(false), null, false);
        setPlayers(players);
    }


    /**
     * @return the amount of progress (in precentage terms) that we have made toward finding the next computer move.
     */
    public final SearchStrategy getSearchStrategy()
    {
       return strategy_;
    }


    /**
     * @return true if it is currently player1's turn.
     */
    public final boolean isPlayer1sTurn()
    {
        return player1sTurn_;
    }

    /**
     * @return true if player2 is a computer player
     */
    public final Player getCurrentPlayer()
    {
        return player1sTurn_? getPlayer1() : getPlayer2();
    }


    /**
     * @return the player who went first.
     */
    public Player getPlayer1()
    {
        return players_[0];
    }

    /**
     * @return the player who went second.
     */
    public Player getPlayer2()
    {
        return players_[1];
    }

    /**
     * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
     * The chance of player2 winning = 1 - chance of p1 winning.
     * @return estimated chance of player one winning the game
     */
    public final double getChanceOfPlayer1Winning()
    {
        if ( board_.getLastMove() == null )
            return 0.5f;
        // @@ is this right?
        // we can use this formula to estimate the outcome:
        double inherVal = ((TwoPlayerMove)board_.getLastMove()).getInheritedValue();
        if ( Math.abs( inherVal ) > WINNING_VALUE )
            GameContext.log( 1, "TwoPlayerController: warning: the score for p1 is greater than WINNING_VALUE(" +
                    WINNING_VALUE + ")  inheritedVal=" + inherVal );

        double val = Math.min( Math.abs( inherVal ), WINNING_VALUE );
        double prob = (val) / (WINNING_VALUE);

        return prob;
    }

    /**
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public double getStrengthOfWin()
    {
        if (!( getPlayer1().hasWon() || getPlayer2().hasWon()))
            return 0.0;
        return 50.0 / (float)getNumMoves();
    }



    /**
     * @return  suggested default weights for the computer to use when playing.
     */
    public ParameterArray getDefaultWeights()
    {
        return weights_.getDefaultWeights();
    }

    /**
     * this returns a reference to the weights class for editing
     * @return  contains the weights used for computer player1 and 2.
     */
    public final GameWeights getComputerWeights()
    {
        return (weights_);
    }


    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    public Move undoLastMove()
    {
        TwoPlayerMove m = (TwoPlayerMove) board_.undoMove();
        if (m != null) {
            player1sTurn_ = m.isPlayer1();
        }
        return m;
    }

    /**
     * @return true if the computer is supposed to make the first move.
     */
    public boolean doesComputerMoveFirst()
    {
        return !getPlayer1().isHuman();
    }


    /**
     * the computer will search for and make its move.
     * The search for the best computer move happens on a separate thread so the UI does not lock up.
     * @param player1 if true then the computer moving is player1
     * @return the move the computer selected (may return null if no move possible)
     */
    private TwoPlayerMove findComputerMove( boolean player1 )
    {
        ParameterArray weights;
        player1sTurn_ = player1;

        long time = 0;
        if ( GameContext.isProfiling() ) {
            time = System.currentTimeMillis();
            initializeGobalProfilingStats();
        }

        assert (!getMoveList().isEmpty()) : "Error: null before search";
        TwoPlayerMove m = (TwoPlayerMove) getMoveList().getLast();
        TwoPlayerMove p = m.copy();

        if ( player1 )
            weights = weights_.getPlayer1Weights();
        else
            weights = weights_.getPlayer2Weights();
        if ( root_ != null ) {
            root_.removeAllChildren(); // clear it out
            p.setSelected(true);
            root_.setUserObject( p );
        }

        /////////////////////// SEARCH //////////////////////////////////////////////////////
        strategy_ = SearchStrategy.createSearchStrategy(getTwoPlayerOptions().getSearchStrategyMethod(), getSearchable());
        TwoPlayerMove selectedMove =
                strategy_.search( p, weights, getSearchable().getLookAhead(), 0,
                                  Double.MAX_VALUE, Double.MIN_VALUE, root_ );
        /////////////////////////////////////////////////////////////////////////////////////

        if ( selectedMove != null ) {
            makeMove( selectedMove );
            GameContext.log( 2, "computer move :" + selectedMove.toString() );
            //getMoveList().add( selectedMove );
            //player1sTurn_ = !player1;
        }


        if ( GameContext.isProfiling() ) {
            showProfileStats( System.currentTimeMillis() - time, strategy_.getNumMovesConsidered() );
        }

        return selectedMove;
    }


    /**
     * Export some usefule performance profile statistics in the log.
     * @param totalTime
     * @param numMovesConsidered
     */
    protected void showProfileStats( long totalTime, int numMovesConsidered )
    {
        GameContext.log( 0, "----------------------------------------------------------------------------------" );
        GameContext.log( 0, "There were " + numMovesConsidered + " moves considered." );
        GameContext.log( 0, "The total time for the computer to move was : " +
                Util.formatNumber((float)totalTime/1000) + " seconds." );
    }


    /**
     * set up stats profiling state if needed.
     * does nothing by default.
     */
    protected void initializeGobalProfilingStats()
    {}

    /**
     * record the human's move p.
     * @param m the move the player made
     * @return the same move with some of the fields filled in
     */
    public final Move manMoves( Move m )
    {
        // we use the default weights because we just need to know if the game is over
        makeMove( m );
        m.setValue(worth( m, weights_.getDefaultWeights() ));
        return m;
    }

    /**
     * this makes an arbitrary move (assumed valid) and adds it to the move list.
     * Calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *  @param m the move to play.
     */
    public void makeMove( Move m )
    {
        board_.makeMove( m );
        //getMoveList().add( m );
        player1sTurn_ = !((TwoPlayerMove)m).isPlayer1();
    }


    /**
     * When this method is called the game controller will asynchronously search for the next move
     * for the computer to make. It returns immediately (unless the computer is playing itself).
     * Usually returns false, but will return true if it is a computer vs computer game, and the
     * game is over.
     * @param isPlayer1
     * @return true if the game is over.
     * @throws AssertionError thrown if something bad happened while searching.
     */
    public boolean requestComputerMove(boolean isPlayer1) throws AssertionError
    {
        return requestComputerMove(isPlayer1, getTwoPlayerOptions().isAutoOptimize());
    }

    /**
     *
     * @param isPlayer1
     * @param synchronous if true then the method does not return until the next move has been found.
     * @return true if the game is over
     * @throws AssertionError  if something bad happenned.
     */
    public boolean requestComputerMove(final boolean isPlayer1, boolean synchronous) throws AssertionError
    {
        // launch a separate thread to do the search for the next move.
        worker_ = new Worker() {

            private Move move_ = null;

            public Object construct() {
                processing_ = true;

                move_ = findComputerMove( isPlayer1 );

                return move_;
            }

            public void finished() {
                processing_ = false;
                if (get2PlayerViewer() != null)  {
                    get2PlayerViewer().computerMoved(move_);
                }
            }
        };

        worker_.start();

        if (synchronous) {
            // this blocks until the value is available.
            TwoPlayerMove m = (TwoPlayerMove)worker_.get();
            //refresh();
            return getSearchable().done( m, true );
        }
        return false;
    }

    /**
     * Let the computer play against itself for a long time as it optimizes its parameters.
     * @return the resulting optimized parameters.
     */
    public ParameterArray runOptimization() {
        Optimizer optimizer = new Optimizer( this.getOptimizee(), getTwoPlayerOptions().getAutoOptimizeFile() );

        ParameterArray optimizedParams;
        optimizedParams =
                optimizer.doOptimization( OptimizationType.HILL_CLIMBING,
                                          getComputerWeights().getDefaultWeights(),
                                          WINNING_VALUE);
       return optimizedParams;
    }

    /**
     *  @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing()
    {
        return processing_;
    }



    public void pause()
    {
        if (getSearchStrategy() == null) {
            GameContext.log(1, "There is no search to pause" );
            return;
        }
        getSearchStrategy().pause();
        GameContext.log(1, "search strategy paused." );
    }

    public boolean isPaused()
    {
        return getSearchStrategy().isPaused();
    }


    /**
     *  @return if positive then computer1 won, else computer2 won.
     *   the magnitude of this returned value indicates how much it won by.
     */
    private double runComputerVsComputer()
    {
        boolean done = false;
        reset();
        computerMovesFirst();

        if (viewer_ != null)  {
            get2PlayerViewer().showComputerVsComputerGame();
        }
        else {
            // running in a batch mode where the viewer is not available
            while ( !done ) {
                done = getSearchable().done(findComputerMove( false ), true);
                // if done the final move was played
                if ( !done ) {
                    done = getSearchable().done(findComputerMove( true ), true);
                }
            }
        }
        if (getPlayer1().hasWon())
            return getStrengthOfWin();
        else
            return -getStrengthOfWin();
    }

    /**
     * if desired we can set a game tree root. If non-null then this
     * tree will get filled in as the search is conducted. It can then be
     * viewed in the GameTreeDialog or some other UI.
     *
     * Here's how the GameTreeDialog is able to show the game tree:
     * When the user indicates that they want to see the GameTreeDialog,
     * the game panel gives the tree from the GameTreeDialog to the Controller:
     * controller_.setGameTreeRoot( treeDialog_.getRootNode() );
     * Then whenever a move by either party occurs, the GameTreeDialog recieves
     * a game changed event. The GameTreeDialog renders the tree that was build up during search.
     * It already has a reference to the root of the tree.
     * If this method is never called, root_ remains null, and the controller
     * knows that it should not bother to create the tree when searching.
     */
    public final void setGameTreeRoot( SearchTreeNode root )
    {
        root_ = root;
    }


    public boolean isDone()
    {
        return getSearchable().done((TwoPlayerMove)board_.getLastMove(), false);
    }


    /**
     *  Statically evaluate a boards state to compute the value of the last move
     *  from player1's perspective.
     *  This function is a key function that must be created for each type of game added.
     *  If evaluating from player 1's perpective, then good moves for p1 are given a positive score.
     *  If evaluating from player 2's perpective, then good moves for p2 are given a positive score.
     *
     *  @param lastMove  the last move made
     *  @param weights  the polynomial weights to use in the polynomial evaluation function
     *  @param player1sPerspective if true, evaluate the board from p1's perspective, else p2's.
     *  @return the worth of the board from the specified players point of view
     */
    public final double worth( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        double value = worth( lastMove, weights );
        return (player1sPerspective) ? value : -value;
    }

    /** evaluates from player 1's perspective
     */
    protected abstract double worth( Move lastMove, ParameterArray weights );

    /**
     * take the list of all possible next moves and return just the top bestPercentage of them
     * @param player1 true if its player one's turn
     * @param moveList the list of all generated moves
     * @param player1sPerspective if true than bestMoves are from player1s perspective
     */
    protected final List getBestMoves( boolean player1, List moveList, boolean player1sPerspective )
    {

        // sort the list so the better moves appear first.
        // This is a terrific improvement when used in conjunction with
        // alpha-beta pruning
        Collections.sort( moveList );

        // reverse the order so the best move (using static board evaluation) is first
        if ( player1 == player1sPerspective )
           Collections.reverse( moveList );

        // We could potentially eliminate the best move doing this.
        // A move which has a low score this time might actually lead to the best move later.
        int numMoves = moveList.size();

        List bestMoveList = moveList;
        int best = (int) ((float) getTwoPlayerOptions().getPercentageBestMoves() / HUNDRED * numMoves);
        if ( best < numMoves )
            bestMoveList = moveList.subList( 0, best );

        //GameContext.log(2, "generated top moves are :  " + moveList );
        return bestMoveList;
    }



    public final Optimizee getOptimizee() {
        return new TwoPlayerOptimizee();
    }


    private class TwoPlayerOptimizee implements Optimizee {

        /**
         * If true is returned, then compareFitness will be used and evaluateFitness will not
         * otherwise the reverse will be true.
         * @return return true if we evaluate the fitness by comparison
         */
        public boolean  evaluateByComparison()
        {
            return true;
        }

        /**
         * Attributes a measure of fitness to the specified set of parameters.
         * There's no good way for a game playing program to do this because it
         * can only evaluate itself relative to another player.
         * see compareFitness below.
         * @param params the set of parameters to misc
         * @return the fitness measure. The higher the better
         */
        public double evaluateFitness( ParameterArray params )
        {
           return 0.0;
        }

        public double getOptimalFitness() {
            return 0;
        }

        /**
         * @return the number of factors we take into consideration when optimizing.
         */
        public int getNumParameters() {
            return weights_.getDefaultWeights().size();
        }

        /**
         * Compares to sets of game parameters.
         * It does this by playing the computer against itself. One computer player has the params1
         * weights and the other computer player uses the params2 weights.
         * If the player using params1 wins then a positive value proportional to the strength of the win is returned.
         *
         * @param params1 set of weight for one of the sides
         * @param params2 set of weights for the other side
         * @return the amount that params1 are better than params2. May be negative if params1 are better.
         */
        public double compareFitness( ParameterArray params1, ParameterArray params2 )
        {
            // to remove the advantage we get from playing first, 2 runs are done
            // The first one where params1 plays first, and the second where params2 plays first.
            // This should remove the bias.

            weights_.setPlayer1Weights(params1);
            weights_.setPlayer2Weights(params2);
            double run1 = runComputerVsComputer();

            weights_.setPlayer1Weights(params2);
            weights_.setPlayer2Weights(params1);
            double run2 = runComputerVsComputer();

            return (run1 - run2);
        }
    }



    public abstract Searchable getSearchable();

    public abstract class TwoPlayerSearchable implements Searchable {

        /**
         * @return the number of moves/plys to lookahead while searching
         */
        public final int getLookAhead() {
            return getTwoPlayerOptions().getLookAhead();
        }

        /**
         * @return  whether to use alpha beta pruning while searching
         */
        public final boolean getAlphaBeta() {
            return getTwoPlayerOptions().getAlphaBeta();
        }

        /**
         * @return whether or not the quiescent search option is being used by the search strategy
         */
        public final boolean getQuiescence()
        {
            return getTwoPlayerOptions().getQuiescence();
        }


        /**
         * @param m the move to play.
         */
        public final void makeInternalMove( TwoPlayerMove m )
        {

            if (getNumMoves() > 0)
                assert(((TwoPlayerMove)board_.getLastMove()).isPlayer1() != m.isPlayer1()):
                        "can't go twice in a row m="+m+" getLastMove()="+board_.getLastMove() +" movelist = "+getMoveList();

            board_.makeMove( m );

            // should show in game tree dlg if present
            /* @@ this is not working because the gameTree dialog does not have the current search state
            if ( viewer_ != null && getShowComputerAnimation() ) {
                viewer_.refresh();
            }*/
        }

        /**
         * takes back the most recent move.
         * @param m
         */
        public final void undoInternalMove( TwoPlayerMove m )
        {
            board_.undoMove();
        }

        /**
         * given a move, determine whether the game is over.
         * If recordWin is true, then the variables for player1/2HasWon can get set.
         *  sometimes, like when we are looking ahead we do not want to set these.
         * @param m the move to check. If null then return true.
         * @param recordWin if true then the controller state will record wins
         */
        public boolean done( TwoPlayerMove m, boolean recordWin )
        {
            if (getNumMoves() == 0)
                return false;
            if (getNumMoves() > 0 && m == null) {
                GameContext.log(0, "Game done because there are no more moves");
                return true; // because their were no more moves apparently.
            }
            if (getPlayer1().hasWon() || getPlayer1().hasWon())
                return true;

            boolean won = (Math.abs( m.getValue() ) >= WINNING_VALUE);
            if ( won && recordWin ) {
                if ( m.getValue() >= WINNING_VALUE )
                    getPlayer1().setWon(true);
                else
                    getPlayer2().setWon(true);
            }
            return ( getNumMoves() >= board_.getMaxNumMoves() || won);
        }

        /**
         * returns true if the specified move caused one or more opponent pieces to become jeopardized
         */
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            return false;
        }

    }
}