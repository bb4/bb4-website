package com.becker.game.twoplayer.go;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.go.persistence.GoGameExporter;
import com.becker.game.twoplayer.go.persistence.GoGameImporter;
import com.becker.optimization.*;

import java.util.*;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;

/**
 * Defines everything the computer needs to know to play Go
 * If you are not familiar with rules of go or the following terms, see
 * Mathematical Go by Berlekamp and Wolfe (appendix B).
 * terms: liberty, atari, snapback, ko, seki, string, group, dame
 *        eye, false eye, big eye, dead stone, independently alive group,
 *        territory, bent 4, moyo, nobi, ikken tobi, nikken tobi, kogeima
 *
 * I will try to adhere to Modern Chinese rules.
 * Here are the unique aspects of Chinese rules that differ from other common rulesets:
 *  1) Any suicide move is considered illegal.
 *  2) When you pass, your opponent gets a point.
 *     For computer go, this is useful because it forces
 *     decision/completion of groups to help determine life and
 *     death and a proper score.
 *  3) The game terminates when the 2 players pass consecutively.
 *  4) score = komi (5.5) + stones on board of player + player territory
 *     (note: captures not counted)
 *
 * The computer keeps track of hierachies of stones. The pecking order is:
 *   stones - individual spaces. They can be empty or occupied.
 *   strings - tightly connected collections of stones (nobi connections only)
 *   groups - connected collections of strings (nikken tobi, and kogeima)
 *   armies - loosely connected collections of groups (still to do).
 *
 * Things still to do:
 * Estimated days to implement are in ()'s after each item. I have about 4 hours in a day on the weekend.
 * This lists only grows. As I complete one task, I typically add 2 more.
 * In reality, I'll probably never finish. That's ok, I'm not sure I want to. I enjoy doing it.
 * 
 *
 * High priority todo:
 * - We could avoid a lot od subclasses if we just specify game specific classes in the plugin xml 
 *    and then create the classes using reflection in the base class.
 *  - Break out GameBoardRenderer from GameBoardViewer for each game package.
 *  - Add test cases for every little method of every class. Use clover to verify.
 *  - Add tests for GoGroup.calculate*Health
 *  - Why don't test cases find optimal moves?
 *  - fix scoring (allow for different types of rule systems)
 *  - parallelize minimax (http://www.cs.vu.nl/~aske/mtdf.html#abmem)
 *  - 
 * bugs
 *  - Error: can't have no liberties and still be on the board!
 *  - don't play in territory at end of game.
 *  - back up and play black, back up again and play white.
 *  - java.lang.AssertionError: The sum of the child times(23411) cannot be greater than the parent time (23296)
 *  - pause/continue not working in tree dialog.
 *
 *
 ** common algorithm improvements
 *
 *    - accurate scoring when the game is over (score what it can, and allow for player dispute of score).
 *    - implement armies (2)
 *    - use runner up caching (moves that were good in the past are likely to be still good) (2)
 *    - add randomness to computer moves (have option since sometimes its undesirable) (1)
 *    - adhere to chinese rules, add other rulesets as options. (4)
 *    - consider monkey jump connections.
 *    - if the computer or player resigns, the playerWon vars should be set and the strcngth of
 *      the win should be large.
 *    - remember good moves that have not yet been played. On a big board, they will probably remain good moves.
 *      These good moves should be at the head of a list when possible moves are being generated.
 *
 ** Packaging issues
 *    - ant deploy could use significant cleanup.
 *    - images on index web page should link directly to applets (or webstart).
 *    - auto generate images for index page.
 *    - make this text a web page and add a link to it.
 *    - maintain on website as applet and webstart (yahoo does not yet support webstart. See hostway) (1)
 *    - add high level descriptions of class interactions to package level javadoc (i.e. the architecture) (1).
 *    - cleanup all java doc (1)
 *    - remove all circular dependencies (use pasta from optimalJ) (1)
 *    - put game defaults in a config file rather than having as constants in controller classes.
 *    - make opensource (3)
 *    - write a book about it. Targetting teens. (50)
 *
 ** Performance issues
 *    - check performance bottleknecks with profilers like Yourkit and JProfiler.
 *    - cache isInAtari for better performance
 *
 ** Refactoring changes needed
 *    - alpha-beta and quiescent setter/getter methods could be properties of the SearchStrategy
 *      instead of the game controller.
 *    - Bill seems to think that I should remove setSize and reset from the GameBoard api and just use the constructor.
 *    - make client/server for multi-user play. Mostly done. try on IGS.
 *    - use InputVerifier to validate text type ins.
 *
 ** UI features
 *    - add resign button. (1)
 *    - have female voice repeat all text if sound on. (1)
 *    - visualize how pruning works. Animate the game tree rendering. Use VCR like controls to control animation. (2)
 *    - show visualization of all next move values in main and debug windows (1)
 *    - allow player to ask for suggested move (1)
 *    - use kiseido or GoGui for front end? (3)
 *    - Investigate VASSAL framework. Moyoman. Freya Game engine.
 *    - defaults for options should come from config/preferences file rather than hardcode (1) (see jdk1.4 preferences)
 *    - handle time limits and options (2)
 *    - allow undo/redo of moves in a computer vs. computer game.
 *    - quick keys (kbd shortcuts)
 *    - have it play automatically on Kiseido/IGS without intervention (like many faces of go does) (4)
 *    - allow playing over the net (6)
 *
 * Bugs and Verification:
 *  - after reloading an SGF file its the wrong player's turn
 *  - reading an SGF should set the state of the game params like board size
 *  - adding stones and strings to strings and groups that already contain them.
 *  - automatically run suite of regression tests. (2)
 *  - at end of game, computer plays in its own eyes instead of passing.
 *
 * Resolved bugs (check for regressions)
 *  - confirm computing eyes correctly (and health).
 *  - verify correctness of static evaluation weights and worth function (2)
 *  - confirm can play suicidal move when capturing enemy stones (1)
 *  - sound/speech not working (in applet only? missing libs?)
 *  - When the computer plays in your eye, the eye goes away. It should not.
 *  - Do not have any rendering done in anything but ui classes (done) (1).
 *  - switch to using type safe enums instead of int constants (done).
 *  - Game tree shold show expected follow up moves.
 *
 * @see GoBoard
 * @author Barry Becker
 */
public final class GoController extends TwoPlayerController
{

    // a lookup table of scores to attribute to the board positions when calculating the worth
    private float[][] positionalScore_ = null;


    // at the very end of the game we mark dead stones dead.
    private int numDeadBlackStonesOnBoard_ = 0;
    private int numDeadWhiteStonesOnBoard_ = 0;


    //Construct the Go game controller
    public GoController()
    {
        board_ = new GoBoard( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS, 0 );
        initializeData();
    }

    /**
     *  Construct the Go game controller given dimensions and number of handicap stones.
     */
    public GoController( int nrows, int ncols, int numHandicapStones )
    {
        board_ = new GoBoard( nrows, ncols, numHandicapStones );
        initializeData();
    }

    /**
     * @return go game options (uses lazy construction).
     */
    public GameOptions getOptions() {
        if (gameOptions_ == null) {
            TwoPlayerOptions options = new GoOptions();
            options.setPlayerName(true, GameContext.getLabel("BLACK"));
            options.setPlayerName(false, GameContext.getLabel("WHITE"));
            gameOptions_ = options;
        }

        return gameOptions_;
    }

    /**
     * this gets the Go specific patterns and weights.
     */
    protected void initializeData()
    {
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
        weights_ = new GoWeights();
    }

    /**
     * initialize the lookup table of scores to attribute to the board positions when calculating the worth.
     * These weights are counted more heavily at te beggiing of the game.
     */
    private void initializePositionalScoreArray()
    {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        int row, col, rowmin, colmin;
        positionalScore_ = new float[numRows + 1][numCols + 1];

        for ( row = 1; row <= numRows; row++ ) {    //rows
            rowmin = Math.min( row, numRows - row + 1 );
            for ( col = 1; col <= numCols; col++ ) {  //cols
                colmin = Math.min( col, numCols - col + 1 );
                positionalScore_[row][col] = 0.0f; // default neutral value


                int lineNo = Math.min(rowmin, colmin);
                if (lineNo < LINE_VALS.length) {
                    if (rowmin == colmin)  {
                        // corners get emphasized
                        positionalScore_[row][col] = 1.5f * (LINE_VALS[lineNo - 1]);
                    }
                    else {
                        positionalScore_[row][col] = LINE_VALS[lineNo - 1];
                    }
                }
            }
        }
    }

    /**
     * specify the number of handicap stones.
     * @param handicap number of handicap stones to place on the board at star points.
     */
    public void setHandicap( int handicap )
    {
        ((GoBoard) board_).setHandicap( handicap );
        player1sTurn_ = false;
    }

    /**
     * @return true if the computer is to make the first move.
     */
    public boolean doesComputerMoveFirst()
    {
        int handicap = ((GoBoard) board_).getHandicap();
        return ((!getPlayer1().isHuman() && (handicap == 0)) ||
                (getPlayer1().isHuman() && (handicap > 0)));
    }

    /**
     * Measure is determined by the score (amount of territory + captures)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public double getStrengthOfWin()
    {
        return Math.abs(getFinalScore(true) - getFinalScore(false));
    }

    /**
     * get the number of black (player1=true) or white (player1=false) stones captured.
     * @param player1sStones if true, get the captures for player1, else for player2.
     * @return num captures
     */
    public int getNumCaptures( boolean player1sStones )
    {
        return ((GoBoard) getBoard()).getNumCaptures(player1sStones);
    }

    /**
     * get a territory estimate for player1 or player2
     * When the game is over, this should return a precise value for the amount of territory
     * (not yet filled with captures).
     * So the estimate will be very rough at the beginning of the game, but should get better as more pieces are played.
     *
     * Since this can be called while we are processing, we return cached values in
     * those cases to avoid a ConcurrentModificationException.
     *
     * @param forPlayer1 if true, get the captures for player1, else for player2
     * @return estimate of the amount of territory the player has
     */
    public int getTerritoryEstimate( boolean forPlayer1 )
    {
        Move m = board_.getLastMove();
        if ( m == null )
            return 0;
        
        return ((GoBoard)board_).getTerritoryEstimate(forPlayer1, true);    
    }

    /**
     *
     * @param forPlayer1
     * @return the actual score (each empty space counts as one)
     */
    public int getTerritory( boolean forPlayer1 )
    {
        return((GoBoard) board_).getTerritoryEstimate(forPlayer1, false);
    }

    // return the game board back to its initial openning state
    public void reset()
    {
        super.reset();
        initializePositionalScoreArray();
        if ( ((GoBoard) board_).getHandicap() > 0 )
            player1sTurn_ = false;
        // make sure the number of dead stones is not carried over.
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
    }

    public void computerMovesFirst()
    {
        // create a bogus previous move
        GoMove lastMove = GoMove.createGoMove( 4, 4, 1, new GoStone(getPlayer1().isHuman()));

        List moveList = getSearchable().generateMoves( lastMove, weights_.getPlayer1Weights(), true );
        // select the best(first since sorted) move to use
        GoMove m = (GoMove) moveList.get( 0 );

        makeMove( m );
    }

    protected void initializeGobalProfilingStats()
    {
        board_.initializeGobalProfilingStats();
    }

    protected void showProfileStats( long totalTime, int numMoves )
    {
        super.showProfileStats( totalTime, numMoves );
        GoBoard.getProfiler().print();
    }

    /**
     * save the current state of the go game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    public void saveToFile( String fileName, AssertionError ae )
    {
        GoGameExporter exporter = new GoGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }


    public void restoreFromFile( String fileName ) {
        GoGameImporter importer = new GoGameImporter(this);
        importer.restoreFromFile(fileName);
    }


    /**
     *  Statically evaluate the board position.
     *  The most naive thing we could do here is to simply return the sum of the captures
     *  for player1 - sum of the captures for player2.
     *  However for go, since search is not likely to be that useful given
     *  the huge branch factor, we need to heavily rely on a sophisticated evaluation.
     *    So what we do is have every space on the board have a score representing
     *  how strongly it is controlled by player1 (black).  If the score is 1.00, then that
     *  position is inside or part of an unconditionally alive group owned by player1 (black)
     *  or it is inside a dead white group.
     *  If the score is -1.00 then its player 2's(white) unconditionally alive group
     *  or black's dead group. A blank dame might have a score
     *  of 0. A white stone might have a positive score if its part of a white group
     *  which is considered mostly dead.
     *
     *  Here are some ideas for things to influence the evaluation:
     *   - give 10 points for every stone on the board and every stone captured
     *   - give bonus for star points and other special points
     *   - give credit for stone influence in its neighborhood
     *   - weight stones by how alive they are
     *
     *  @return statically evaluated value of the board.
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        int row, col;
        double worth;
        GoBoard board = (GoBoard)board_;
        // adjust for board size - so worth will be comparable regardless of board size.
        double scaleFactor = 361.0 / Math.pow(board.getNumRows(), 2);
        double captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue();
        double captureScore = captureWt * (getNumCaptures( true ) - getNumCaptures( false ));
        float n = 2.0f * board.getNumRows();
        // opening = 1.99 - 1.5;   middle = 1.5 - 1.01;    end = 1.0
        double gameStageBoost = 0.5 + 2.0 * Math.max((n - (float)getNumMoves())/n, 0.0);

        PositionalScore totalScore = new PositionalScore();
        for ( row = 1; row <= board.getNumRows(); row++ ) {    //rows
            for ( col = 1; col <= board.getNumCols(); col++ ) {  //cols

                GoBoardPosition position = (GoBoardPosition) board.getPosition( row, col );
                double positionalScore = gameStageBoost * positionalScore_[row][col];
                PositionalScore score = calcPositionalScore(position, weights, positionalScore, board);
                totalScore.incrementBy(score);
                position.setScoreContribution(score.getPositionScore());
            }
        }
        double territoryDelta = board.getTerritoryDelta();
        worth = scaleFactor * (totalScore.getPositionScore() + captureScore + territoryDelta);

        if (GameContext.getDebugMode() > 0)  {
            String desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor);
            ((TwoPlayerMove) lastMove).setScoreDescription(desc);
        }

        //GameContext.log(1,"GoController.worth: worth="+worth);
        if ( worth < -WIN_THRESHOLD ) {
            // then the margin is too great
            return -WINNING_VALUE;
        }
        else if ( worth > WIN_THRESHOLD ) {
            // then the margin is too great
            return WINNING_VALUE;
        }
        return worth;
    }

    /**
     * @return the score contribution from a single point on the board
     */
    private static PositionalScore calcPositionalScore(GoBoardPosition position, ParameterArray weights,
                                                       double positionalScore, GoBoard board) {

        PositionalScore score = new PositionalScore();

        if (position.isInEye())  {
            if (position.isOccupied()) {
                // a dead enemy stone in the eye
                score.deadStoneScore = position.getEye().isOwnedByPlayer1()? 2.0:-2.0;
            }
            else {
                score.eyeSpaceScore = position.getEye().isOwnedByPlayer1()? 1.0:-1.0;
            }
        }
        else if ( position.isOccupied() ) {
            GoStone stone = (GoStone)position.getPiece();

            int side = position.getPiece().isOwnedByPlayer1()? 1: -1;
            // penalize bad shape like empty triangles
            score.badShapeScore = -(side * GoBoardUtil.formsBadShape(position, board)
                                   * weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue());

            // Usually a very low weight is assigned to where stone is played unless we are at the start of the game.
            score.posScore = side * weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue() * positionalScore;
            score.healthScore =  weights.get(GoWeights.HEALTH_WEIGHT_INDEX).getValue() * stone.getHealth();

            if (GameContext.getDebugMode() > 1)  {
                stone.setPositionalScore(score);
            }
        }

        score.calcPositionScore();
        return score;
    }


    /**
     * it is a takeback move if the proposed move position (row,col) would immdiately replace the last captured piece
     *  and capture the stone that did the capturing.
     */
    public static boolean isTakeBack( int row, int col, GoMove lastMove, GoBoard board )
    {
        if ( lastMove == null ) return false;

        CaptureList captures = lastMove.getCaptures();
        if ( captures != null && captures.size() == 1 ) {
            GoBoardPosition capture = (GoBoardPosition) captures.getFirst();
            if ( capture.getRow() == row && capture.getCol() == col ) {
                GoBoardPosition lastStone = (GoBoardPosition) board.getPosition( lastMove.getToRow(), lastMove.getToCol() );
                if ( lastStone.getNumLiberties( board ) == 1 && lastStone.getString().getMembers().size() == 1 ) {
                    GameContext.log( 2, "it is a takeback " );
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * clear the game over state in case the user decides to undo moves
     */
    public void clearGameOver() {
         for ( int row = 1; row <= board_.getNumRows(); row++ ) {    //rows
            for ( int col = 1; col <= board_.getNumCols(); col++ ) {  //cols
                GoBoardPosition space = (GoBoardPosition)board_.getPosition( row, col );
                if (space.isOccupied())  {
                    GoStone stone = (GoStone)space.getPiece();

                    stone.setDead(false);
                }
            }
        }
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
    }

    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score
     */
    public double getFinalScore(boolean player1)
    {
        if (isProcessing()) {
            GameContext.log(0,  "Error: tried to get Score() while processing!");
            return 0;
        }
        int captures = getNumCaptures(player1) + (player1 ? numDeadWhiteStonesOnBoard_ : numDeadBlackStonesOnBoard_);

        String side = (player1? "black":"white");
        System.out.println("----");
        System.out.println("final score for "+ side);
        System.out.println("getNumCaptures("+side+")="+ getNumCaptures(player1));
        System.out.println("num dead "+side+" stones on board= "+ (player1 ? numDeadWhiteStonesOnBoard_ : numDeadBlackStonesOnBoard_));

        System.out.println("getTerritory("+side+")="+getTerritory(player1));
        System.out.println("captures="+captures);
        System.out.println("final = terr - captures="+ (getTerritory(player1) - captures));
        return getTerritory(player1) - captures;
    }

    public int getNumDeadStonesOnBoard(boolean black) {
        return black ?  numDeadBlackStonesOnBoard_ : numDeadWhiteStonesOnBoard_;
    }

   /**
    * Update the final life and death status of all the stones still on the board.
    * This method must only be called at the end of the game or stones will get prematurely marked as dead.
    * @@ should do in 2 passes.
    * The first can update the health of groups and perhaps remove obviously dead stones.
    */
    public void updateLifeAndDeath()
    {
       // the last 2 moves must passes so
       List moves = getMoveList();
       GoMove lastMove; // = (GoMove)moves.get(moves.size()-1);

       // if we are loading saved games, then winner might have won by time or forfeit
       // - in which case the last 2 moves are not passes.
       //GoMove nextToLastMove = (GoMove)moves.get(moves.size()-2);
       //assert(lastMove.isPassingMove());
       //assert(nextToLastMove.isPassingMove());
       assert(moves.size() > 3);

       // we need to get the third to last move.
       //JOptionPane.showConfirmDialog((Component)viewer_, "before udpate territory");
       lastMove = (GoMove)moves.get(moves.size()-3);
       GoBoardPosition lastStone = (GoBoardPosition)board_.getPosition(lastMove.getToRow(), lastMove.getToCol());
       ((GoBoard)board_).updateTerritory(lastStone);
       //JOptionPane.showConfirmDialog((Component)viewer_, "udpated territory");

       for ( int row = 1; row <= board_.getNumRows(); row++ ) {    //rows
            for ( int col = 1; col <= board_.getNumCols(); col++ ) {  //cols
                GoBoardPosition space = (GoBoardPosition)board_.getPosition( row, col );
                if (space.isOccupied())  {
                    GoStone stone = (GoStone)space.getPiece();
                    int side = (stone.isOwnedByPlayer1() ? 1: -1);
                    GameContext.log(1, "life & death: "+space+" health="+stone.getHealth()
                                       +" string health=" +space.getGroup().getRelativeHealth());
                    if (side*stone.getHealth() < 0)  {
                        // then the stone is more dead than alive, so mark it so
                        GameContext.log(1, "setting "+space+" to dead");
                        stone.setDead(true);
                        if (space.getPiece().isOwnedByPlayer1())
                            numDeadBlackStonesOnBoard_++;
                        else
                            numDeadWhiteStonesOnBoard_++;
                    }
                }
            }
        }
    }


    public Searchable getSearchable() {
         return new GoSearchable();
    }


    protected class GoSearchable extends TwoPlayerSearchable {

        /**
         * given a move determine whether the game is over.
         * If recordWin is true then the variables for player1/2HasWon can get set.
         *  sometimes, like when we are looking ahead we do not want to set these.
         *
         * @param m the move to check
         * @param recordWin if true then the controller state will record wins
         * @return true if the game is over
         */
        public final boolean done( TwoPlayerMove m, boolean recordWin )
        {
            boolean gameOver = false;
            // if the last 2 moves are passing moves then the game is over
            List moves = getMoveList();

            if (m == null) {
                gameOver = true;
            }
            else if ( m.isPassingMove() && moves.size() > 2 ) {
                GoMove secondToLast = (GoMove) moves.get( moves.size() - 2 );
                if ( secondToLast.isPassingMove() ) {
                    GameContext.log( 0, "Done: The last 2 moves were passes :" + m + ", " + secondToLast );

                    if (recordWin) {
                        if (getFinalScore(true) > getFinalScore(false))
                            getPlayer1().setWon(true);
                        else
                            getPlayer2().setWon(true);
                    }
                    gameOver = true;
                }
            }
            if (!gameOver) {
                // try normal handling
                gameOver = super.done( m, recordWin );
            }

            if (gameOver && recordWin) {
                //we should not call this twice
                //assert(numDeadBlackStonesOnBoard_==0 && numDeadWhiteStonesOnBoard_==0):" should not update life and death twice.";
                GameContext.log(0, " Error: should not update life and death twice.");

                // now that we are finally at the end of the game,
                // update the life and death of all the stones still on the board
                GameContext.log(1,  "about to update life and death." );
                updateLifeAndDeath();
            }

            return gameOver;
        }


        /**
         * return any moves that take captures or get out of atari.
         *
         * @param lastMove
         * @param weights
         * @param player1sPerspective
         * @return list of urgent moves
         */
        public final List generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List moves = generateMoves( lastMove, weights, player1sPerspective );
            GoBoard gb = (GoBoard) board_;
            GoMove lastMovePlayed = (GoMove) lastMove;

            // just keep the moves that take captures
            Iterator it = moves.iterator();
            while ( it.hasNext() ) {
                GoMove move = (GoMove) it.next();
                if ( move.getNumCaptures() == 0 || lastMovePlayed.causesAtari(gb) > 0 ) {
                    it.remove();
                }
                else {
                    move.setUrgent(true);
                }
            }
            //if (moves.size() > 0)
            //    GameContext.log(2,"gocontroller: the number of urgent moves are:"+moves.size());
            return moves;
        }

        /**
         * returns true if the specified move caused one or more opponent pieces to become jeopardized
         * For go, if the specified move caused a group to become in atari, then we return true.
         *
         * @param lastMove
         * @param weights
         * @param player1sPerspective
         * @return true if the last move created a big change in the score
         */
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            GoBoard gb = (GoBoard) board_;
            return (( (GoMove)lastMove ).causesAtari(gb) > 5);
        }


        /**
         * generate all possible next moves
         */
        public final List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            GoBoard board = (GoBoard) board_;
            GoBoard.getProfiler().start(GoProfiler.GENERATE_MOVES);
            List moveList = new LinkedList();
            int i,j;
            int nCols = board.getNumCols();
            int nRows = board.getNumRows();
            assert (nRows == nCols) : " rows and cols must be the same in go";

            board.determineCandidateMoves();

            boolean player1 = true;
            if ( lastMove != null ) {
                player1 = !(lastMove.isPlayer1());
            }

            for ( i = 1; i <= nCols; i++ )      //cols
                for ( j = 1; j <= nRows; j++ )    //rows
                    // if its a candidate move and not an immediate takeback (which would break the rule of ko)
                    if ( board.isCandidateMove( j, i ) && !isTakeBack( j, i, (GoMove) lastMove, board ) ) {
                        GoMove m = GoMove.createGoMove( j, i, lastMove.getValue(), new GoStone(player1) );

                        if ( m.isSuicidal(board) ) {
                            GameContext.log( 2, "The move was a suicide (can't add it to the list): " + m );
                        }
                        else {
                            GoBoard.getProfiler().stop(GoProfiler.GENERATE_MOVES);
                            board.makeMove( m );
                            GoBoard.getProfiler().start(GoProfiler.GENERATE_MOVES);
                            // this value is not likely to change much except local to last move,
                            // anyway we could cache that?
                            GoBoard.getProfiler().start(GoProfiler.CALC_WORTH);
                            m.setValue(worth( m, weights, player1sPerspective ));
                            GoBoard.getProfiler().stop(GoProfiler.CALC_WORTH);
                            // now revert the board
                            GoBoard.getProfiler().stop(GoProfiler.GENERATE_MOVES);
                            board.undoMove();
                            GoBoard.getProfiler().start(GoProfiler.GENERATE_MOVES);
                            moveList.add( m );
                        }
                    }

            moveList = getBestMoves( player1, moveList, player1sPerspective );

            // if we are well into the game, include a passing move.
            // if none of the generated moves have an inherited value better than the passing move
            // (which just uses the value of the current move) then we should pass
            if (getNumMoves() > nCols+nRows)  {
                moveList.add(moveList.size(), GoMove.createPassMove(lastMove.getValue(), player1));
            }
            GoBoard.getProfiler().stop(GoProfiler.GENERATE_MOVES);

            return moveList;
        }
    }

}
