package com.becker.game.twoplayer.common;

import com.becker.common.concurrency.Worker;
import com.becker.common.util.Util;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.becker.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.transposition.ZobristHash;
import com.becker.game.twoplayer.common.search.tree.GameTreeViewable;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.Optimizer;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.optimization.Optimizee;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for 2 player zero sum games with perfect information.
 * Some examples include chess, checkers, go, othello, pente, com.becker.game.twoplayer.blockade, mancala, nine-mens morris, etc.
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
    /** anything greater than this is considered a won game  */
    public static final int WINNING_VALUE = SearchStrategy.WINNING_VALUE;

    /** not really infinty, but close enough for our purposes. */
    private static final int INFINITY = SearchStrategy.INFINITY;

    private static final double HUNDRED = 100.0;

    protected boolean player1sTurn_ = true;

    /** these weights determine how the computer values each move.
     * they serve as parameters to a game dependent evaluation function. */
    protected GameWeights weights_;

    /** the method the computer will use for searching for the next move.  */
    private SearchStrategy strategy_;

    /** if this becomes non-null, we will fill in the game tree for display in a UI. */
    private GameTreeViewable gameTreeListener_;

    /** Worker represents a separate thread for computing the next move. */
    private Worker worker_;

    /** this is true while the computer thinks about its next move. */
    private boolean processing_ = false;

    private Searchable searchable_;


    /**
     * Construct the game controller.
     */
    public TwoPlayerController()
    {
        createPlayers();
    }

    @Override
    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = createOptions();
        }
        return gameOptions_;
    }

    /**
     * @return custom set of search and game options.
     */
    protected abstract TwoPlayerOptions createOptions();

    /**
     * These options define the search algorithm and other settings.
     * @return game options
     */
    public TwoPlayerOptions getTwoPlayerOptions() {

        return (TwoPlayerOptions) getOptions();
    }

    public TwoPlayerViewable get2PlayerViewer()
    {
       return (TwoPlayerViewable)viewer_;
    }

    /**
     * Return the game board back to its initial openning state
     */
    @Override
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

    /**
     * save the current state of the game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae )
    {
        TwoPlayerGameExporter exporter = new TwoPlayerGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }

    @Override
    public void restoreFromFile( String fileName ) {
        TwoPlayerGameImporter importer = new TwoPlayerGameImporter(this);
        importer.restoreFromFile(fileName);
        TwoPlayerMove m = (TwoPlayerMove)getLastMove();
        if (m != null)
            m.setValue( worth( m, weights_.getDefaultWeights()));
    }

    /**
     * create the 2 players.
     */
    private void createPlayers()
    {
        List<Player> players = new ArrayList<Player>(2);
        players.add(new Player(getTwoPlayerOptions().getPlayerName(true), null, true));
        players.add(new Player(getTwoPlayerOptions().getPlayerName(false), null, false));
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
        return players_.get(0);
    }

    /**
     * @return the player who went second.
     */
    public Player getPlayer2()
    {
        return players_.get(1);
    }
    
    /**
     *
     * @param moveList list of moves
     * @return a randome move from the list of genereated moves.
     */
    protected TwoPlayerMove getRandomMove(List moveList) {
       
        int r = (int) (RANDOM.nextFloat() * moveList.size());
        return (TwoPlayerMove) moveList.get( r );
    }

    /**
     * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
     * The chance of player2 winning = 1 - chance of p1 winning.
     * @return estimated chance of player one winning the game
     */
    public final double getChanceOfPlayer1Winning()
    {
        // if true then too early in the game to tell.
        TwoPlayerMove lastMove = (TwoPlayerMove) board_.getLastMove();
        if (board_.getMoveList().size() < 4 )
            return 0.5f;

        assert(lastMove != null);
        // @@ is this right?
        // we can use this formula to estimate the outcome:       
        double inherVal = lastMove.getInheritedValue();
        if ( Math.abs( inherVal ) > WINNING_VALUE )
            GameContext.log( 1, "TwoPlayerController: warning: the score for p1 is greater than WINNING_VALUE(" +
                    WINNING_VALUE + ")  inheritedVal=" + inherVal );

        double val = Math.min( Math.abs( inherVal ), WINNING_VALUE );
        return val / WINNING_VALUE;
    }

    /**
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin()
    {
        if (!( getPlayer1().hasWon() || getPlayer2().hasWon()))
            return 0;
        return 50 / getNumMoves();
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
        return weights_;
    }

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    @Override
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
     * Currently online play not available for 2 player games - coming soon!
     * @return false
     */
    @Override
    public boolean isOnlinePlayAvailable() {
        return false;
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
            initializeProfilingStats();
        }

        assert (!getMoveList().isEmpty()) : "Error: null before search";
        TwoPlayerMove move = (TwoPlayerMove) getMoveList().getLast();
        TwoPlayerMove lastMove = move.copy();

        if ( player1 )
            weights = weights_.getPlayer1Weights();
        else
            weights = weights_.getPlayer2Weights();
        if ( gameTreeListener_ != null ) {
            gameTreeListener_.resetTree(lastMove);
        }
        TwoPlayerMove selectedMove = searchForNextMove(weights, lastMove);

        if ( selectedMove != null ) {
            makeMove( selectedMove );
            GameContext.log( 2, "computer move :" + selectedMove.toString() );
        }

        if ( GameContext.isProfiling() ) {
            showProfileStats( System.currentTimeMillis() - time, strategy_.getNumMovesConsidered() );
        }

        return selectedMove;
    }

    /**
     * **** SEARCH ******
     * @return the best move to use as the next move.
     */
    private TwoPlayerMove searchForNextMove(ParameterArray weights, TwoPlayerMove lastMove) {
        strategy_ = getTwoPlayerOptions().getSearchOptions().getSearchStrategy(getSearchable(), weights);

        SearchTreeNode root = null;
        if (gameTreeListener_ != null) {
            strategy_.setGameTreeEventListener(gameTreeListener_);
            root = gameTreeListener_.getRootNode();
        }

        return strategy_.search( lastMove, root );
    }


    /**
     * Export some usefule performance profile statistics in the log.
     * @param totalTime total elapsed time.
     * @param numMovesConsidered number of moves inspected during search.
     */
    protected void showProfileStats( long totalTime, long numMovesConsidered )
    {
        GameContext.log( 0, "----------------------------------------------------------------------------------" );
        GameContext.log( 0, "There were " + numMovesConsidered + " moves considered." );
        GameContext.log( 0, "The total time for the computer to move was : " +
                Util.formatNumber((float)totalTime/1000) + " seconds." );
        board_.getProfiler().print();
    }

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
    @Override
    public void makeMove( Move m )
    {
        board_.makeMove( m );
        player1sTurn_ = !((TwoPlayerMove)m).isPlayer1();
    }


    /**
     * When this method is called the game controller will asynchronously search for the next move
     * for the computer to make. It returns immediately (unless the computer is playing itself).
     * Usually returns false, but will return true if it is a computer vs computer game, and the
     * game is over.
     * @param isPlayer1 true if is player one to move.
     * @return true if the game is over.
     * @throws AssertionError thrown if something bad happened while searching.
     */
    public boolean requestComputerMove(boolean isPlayer1) throws AssertionError
    {
        return requestComputerMove(isPlayer1, getTwoPlayerOptions().isAutoOptimize());
    }

    /**
     * Request the next computer move. It will be the best move that the computer can find.
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

            @Override
            public Object construct() {
                processing_ = true;

                move_ = findComputerMove( isPlayer1 );

                return move_;
            }

            @Override
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
            // We could probably use a Future here.
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
                optimizer.doOptimization( OptimizationStrategyType.HILL_CLIMBING,
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
     * if desired we can set a game tree listener. If non-null then this
     * will be updated as the search is conducted. The GameTreeDialog
     * is an example of something that implements this interface and can
     * be used to view the game tree as the search is progressing.
     *
     * Here's how the GameTreeDialog is able to show the game tree:
     * When the user indicates that they want to see the GameTreeDialog,
     * the game panel gives the GameTreeDialog to the Controller:
     * controller_.setGameTreeListener( treeDialog_ );
     * Then whenever a move by either party occurs, the GameTreeDialog recieves
     * a game tree event. The GameTreeDialog renders the tree that was build up during search.
     * It already has a reference to the root of the tree.
     * If this method is never called, the controller knows
     * that it should not bother to create the tree when searching.
     */
    public final void setGameTreeListener( GameTreeViewable gameTreeListener )
    {
        gameTreeListener_ = gameTreeListener;
    }

    /**
     * {@inheritDoc}}
     */
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
    public final int worth( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        int value = worth( lastMove, weights );
        return (player1sPerspective) ? value : -value;
    }

    /**
     * Evaluates from player 1's perspective
     * @return an integer value for the worth of the move.
     *  must be between -SearchStrategy.WINNING_VALUE and SearchStrategy.WINNING_VALUE.
     */
    protected abstract int worth( Move lastMove, ParameterArray weights );

    /**
     * Take the list of all possible next moves and return just the top bestPercentage of them 
     * (or 10 moves, whichever is greater).
     *
     * sort the list so the better moves appear first.
     * This is a terrific improvement when used in conjunction with alpha-beta pruning.
     *
     * @param player1 true if its player one's turn
     * @param moveList the list of all generated moves
     * @param player1sPerspective if true than bestMoves are from player1s perspective
     * @return the best moves in order of how good they are.
     */
    protected final List<? extends TwoPlayerMove> getBestMoves(boolean player1, List<? extends TwoPlayerMove> moveList,
                                                               boolean player1sPerspective )
    {
        Collections.sort( moveList );

        // reverse the order so the best move (using static board evaluation) is first
        SearchOptions searchOptions = ((TwoPlayerOptions) getOptions()).getSearchOptions();
        SearchStrategyType searchType = searchOptions.getSearchStrategyMethod();
        if ( searchType.sortAscending(player1, player1sPerspective)) {
           Collections.reverse( moveList );
        }

        // We could potentially eliminate the best move doing this.
        // A move which has a low score this time might actually lead to the best move later.
        int numMoves = moveList.size();

        List<? extends TwoPlayerMove> bestMoveList = moveList;
        int best = (int) ((float) searchOptions.getPercentageBestMoves() / HUNDRED * numMoves) + 1;
        if ( best < numMoves && numMoves > searchOptions.getMinBestMoves())  {
            bestMoveList = moveList.subList(0, best);
        }
        return bestMoveList;
    }

    public final Optimizee getOptimizee() {
        return new TwoPlayerOptimizee();
    }

    /**
     * Two sets of gameweights that can be optimized by repetitive play.
     */
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

        /**
         *  @return if positive then computer1 won, else computer2 won.
         *   the magnitude of this returned value indicates how much it won by.
         */
        private double runComputerVsComputer()
        {
            boolean done = false;
            reset();
            computerMovesFirst();

            if (get2PlayerViewer() != null)  {
                get2PlayerViewer().showComputerVsComputerGame();
            }
            else {
                // run in batch mode where the viewer is not available.
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
    }


    public Searchable getSearchable() {
        if (searchable_ == null)
            searchable_ = createSearchable();
        return searchable_;
    }

    public abstract Searchable createSearchable();


    public abstract class TwoPlayerSearchable implements Searchable {

        /** Used to generate hashkeys. */
        ZobristHash hash;


        public TwoPlayerSearchable() {
            TwoPlayerBoard b = (TwoPlayerBoard)board_;
            hash =  new ZobristHash(b);
        }

        public SearchOptions getSearchOptions() {
            return ((TwoPlayerOptions) gameOptions_).getSearchOptions();
        }

        /**
         * @param m the move to play.
         */
        public final void makeInternalMove( TwoPlayerMove m )
        {
            TwoPlayerBoard b = (TwoPlayerBoard) board_;
            TwoPlayerMove lastMove = (TwoPlayerMove)b.getLastMove();
            if (getNumMoves() > 0)
                assert(lastMove.isPlayer1() != m.isPlayer1()):
                        "can't go twice in a row m="+m+" getLastMove()="+ lastMove + " movelist = " + getMoveList();

            board_.makeMove( m );

            // should show in game tree dlg if present
            /* @@ this is not working because the gameTree dialog does not have the current search state
            if ( viewer_ != null && getShowComputerAnimation() ) {
                viewer_.refresh();
            }*/

            BoardPosition pos = b.getPosition(m.getToLocation());
            //assert pos != null : "pos was null at " + m.getToLocation() + " pass="+  m.isPassingMove();
            hash.applyMove(m, b.getStateIndex(pos));
        }

        /**
         * takes back the most recent move.
         * @param m  move to undo
         */
        public final void undoInternalMove( TwoPlayerMove m ) {
            TwoPlayerBoard b = (TwoPlayerBoard) board_;
            hash.applyMove(m, b.getStateIndex(b.getPosition(m.getToLocation())));
            b.undoMove();  
        }

        /**
         * given a move, determine whether the game is over.
         * If recordWin is true, then the variables for player1/2HasWon can get set.
         *  sometimes, like when we are looking ahead we do not want to set these.
         * @param lastMove the move to check. If null then return true. This is typically the last move played.
         * @param recordWin if true then the controller state will record wins
         */
        public boolean done( TwoPlayerMove lastMove, boolean recordWin ) {
            // the game can't be over if no moves have been made yet.
            if (getNumMoves() == 0) {
                return false;
            }
            if (getNumMoves() > 0 && lastMove == null) {
                GameContext.log(0, "Game is over because there are no more moves");
                return true;
            }
            if (getPlayer1().hasWon() || getPlayer2().hasWon())
                return true;

            boolean won = (Math.abs( lastMove.getValue() ) >= WINNING_VALUE);
            if ( won && recordWin ) {
                if ( lastMove.getValue() >= WINNING_VALUE )
                    getPlayer1().setWon(true);
                else
                    getPlayer2().setWon(true);
            }
            return ( getNumMoves() >= board_.getMaxNumMoves() || won);
        }

        /**
         * @return true if the specified move caused one or more opponent pieces to become jeopardized
         */
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective ) {
            return false;
        }

        /**
         * @return get a hash key that represents this board state (with negligibly small chance of conflict)
         */
        public Long getHashKey() {
            return hash.getKey();
        }
    }
}