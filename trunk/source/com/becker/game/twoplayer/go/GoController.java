package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.analysis.PositionalScoreAnalyzer;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer;
import com.becker.game.twoplayer.go.persistence.GoGameExporter;
import com.becker.game.twoplayer.go.persistence.GoGameImporter;

import java.util.*;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;

/**
 * Defines everything the computer needs to know to play Go.
 *
 * @see package.html for more info.
 * @see GoBoard
 * @author Barry Becker
 */
public final class GoController extends TwoPlayerController
{
    public static final String VERSION = "0.99";

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private PositionalScoreAnalyzer positionalScorer_;

    /** keeps track of dead stones.  */
    private DeadStones deadStones_;


    /**
     * Construct the Go game controller.
     */
    public GoController()
    {
        this( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS, 0);
    }

    /**
     * Construct the Go game controller given dimensions and number of handicap stones.
     * @param nrows
     * @param ncols
     * @param numHandicapStones
     */
    public GoController( int nrows, int ncols, int numHandicapStones )
    {
        board_ = new GoBoard( nrows, ncols, numHandicapStones );
        initializeData();
        positionalScorer_ = new PositionalScoreAnalyzer((GoBoard)board_);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        TwoPlayerOptions options = new GoOptions();
        options.setPlayerName(true, GameContext.getLabel("BLACK"));
        options.setPlayerName(false, GameContext.getLabel("WHITE"));
        return options;
    }

    /**
     * this gets the Go specific patterns and weights.
     */
    @Override
    protected void initializeData()
    {
        deadStones_ = new DeadStones();
        weights_ = new GoWeights();
    }

    @Override
    protected GoProfiler getProfiler() {
        return GoProfiler.getInstance();
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
    @Override
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
    @Override
    public int getStrengthOfWin()
    {
        return (int)Math.abs(getFinalScore(true) - getFinalScore(false));
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

    public int getNumDeadStonesOnBoard(boolean forPlayer1)  
    {
        return deadStones_.getNumberOnBoard(forPlayer1);
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

        return ((GoBoard)board_).getTerritoryEstimate(forPlayer1, false);
    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     * @param forPlayer1
     * @return the actual score (each empty space counts as one)
     */
    public int getTerritory( boolean forPlayer1 )
    {
        return((GoBoard) board_).getTerritoryEstimate(forPlayer1, true);
    }

    /**
     * *return the game board back to its initial opening state
     */
    @Override
    public void reset()
    {
        super.reset();
        positionalScorer_ = new PositionalScoreAnalyzer((GoBoard)board_);
        if ( ((GoBoard) board_).getHandicap() > 0 )
            player1sTurn_ = false;
        // make sure the number of dead stones is not carried over.
        deadStones_.clear();
    }

    public void computerMovesFirst()
    {
        List moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights(), true );
        // select the best(first since sorted) move to use
        GoMove m = (GoMove) moveList.get( 0 );

        makeMove( m );
    }

    /**
     * save the current state of the go game to a file in SGF (4) format (standard game format).
     *This should some day be xml (xgf)
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae )
    {
        GoGameExporter exporter = new GoGameExporter(this);
        exporter.saveToFile(fileName, ae);
    }

    @Override
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
    @Override
    protected int worth( Move lastMove, ParameterArray weights )
    {
        int row, col;
        double worth;
        GoBoard board = (GoBoard)board_;
        // adjust for board size - so worth will be comparable regardless of board size.
        double scaleFactor = 361.0 / Math.pow(board.getNumRows(), 2);
        double captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue();
        double captureScore = captureWt * (getNumCaptures( false ) - getNumCaptures( true ));
        float n = 2.0f * board.getNumRows();
        // opening = 1.99 - 1.5;   middle = 1.5 - 1.01;    end = 1.0
        double gameStageBoost = 0.5 + 2.0 * Math.max((n - (float)getNumMoves())/n, 0.0);

        PositionalScore totalScore = new PositionalScore();
        for ( row = 1; row <= board.getNumRows(); row++ ) {
            for ( col = 1; col <= board.getNumCols(); col++ ) {

                totalScore = positionalScorer_.updateScoreForPosition(row, col, gameStageBoost, totalScore, weights);
            }
        }
        double territoryDelta = board.getTerritoryDelta();
        worth = scaleFactor * (totalScore.getPositionScore() + captureScore + territoryDelta);

        if (GameContext.getDebugMode() > 0)  {
            String desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor);
            ((TwoPlayerMove) lastMove).setScoreDescription(desc);
        }

        GameContext.log(3,"GoController.worth: worth="+worth);
        if ( worth < -WIN_THRESHOLD ) {
            // then the margin is too great
            return -WINNING_VALUE;
        }
        else if ( worth > WIN_THRESHOLD ) {
            // then the margin is too great
            return WINNING_VALUE;
        }
        return (int)worth;
    }

    /**
     * It is a takeback move if the proposed move position (row,col) would immdiately replace the last captured piece
     *  and capture the stone that did the capturing.
     * @return true of this is an immediate take-back (not allowed in go - see "rule of ko")
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
    @Override
    public void clearGameOver() {
        super.clearGameOver();

         for ( int row = 1; row <= board_.getNumRows(); row++ ) {    //rows
            for ( int col = 1; col <= board_.getNumCols(); col++ ) {  //cols
                GoBoardPosition space = (GoBoardPosition)board_.getPosition( row, col );
                if (space.isOccupied())  {
                    GoStone stone = (GoStone)space.getPiece();

                    stone.setDead(false);
                }
            }
        }
        deadStones_.clear();
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
        int captures =  deadStones_.getNumberOnBoard(!player1);

        String side = (player1? "black":"white");
        GameContext.log(0, "----");
        GameContext.log(0, "final score for "+ side);
        GameContext.log(0, "getNumCaptures("+side+")="+ getNumCaptures(player1));
        GameContext.log(0, "num dead "+side+" stones on board: "+ captures);

        int p1Territory = getTerritory(player1);
        GameContext.log(0, "getTerritory("+side+")="+p1Territory);
        GameContext.log(0, "captures="+captures);
        GameContext.log(0, "final = terr - captures="+ (p1Territory - captures));
        return p1Territory - captures;
    }


   /**
    * Update the final life and death status of all the stones still on the board.
    * This method must only be called at the end of the game or stones will get prematurely marked as dead.
    * @@ should do in 2 passes.
    * The first can update the health of groups and perhaps remove obviously dead stones.
    */
    public void updateLifeAndDeath()
    {
       ((GoBoard)board_).updateTerritory(true);

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
                        deadStones_.increment(space.getPiece().isOwnedByPlayer1());
                    }
                }
            }
        }
    }


    @Override
    public Searchable createSearchable() {
         return new GoSearchable();
    }



    protected class GoSearchable extends TwoPlayerSearchable {

        private static final int CRITICAL_GROUP_SIZE = 4;

        /**
         * given a move determine whether the game is over.
         * If recordWin is true then the variables for player1/2HasWon can get set.
         *  sometimes, like when we are looking ahead we do not want to set these.
         *
         * @param m the move to check
         * @param recordWin if true then the controller state will record wins
         * @return true if the game is over
         */
        @Override
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
        public final List<? extends TwoPlayerMove> generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List<? extends TwoPlayerMove> moves = generateMoves( lastMove, weights, player1sPerspective );
            GoBoard gb = (GoBoard) board_;
            GoMove lastMovePlayed = (GoMove) lastMove;

            // just keep the moves that take captures
            Iterator<? extends TwoPlayerMove> it = moves.iterator();
            while ( it.hasNext() ) {
                GoMove move = (GoMove) it.next();
                if ( move.getNumCaptures() == 0 || lastMovePlayed.causesAtari(gb) > 0 ) {
                    it.remove();
                }
                else {
                    move.setUrgent(true);
                }
            }
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
        @Override
        public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            GoBoard gb = (GoBoard) board_;
            return (( (GoMove)lastMove ).causesAtari(gb) > CRITICAL_GROUP_SIZE);
        }


        /**
         * generate all possible next moves
         */
        public final List<? extends TwoPlayerMove> generateMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            GoBoard board = (GoBoard) board_;
            GoProfiler prof = getProfiler();
            List<GoMove> moveList = new LinkedList<GoMove>();
            int nCols = board.getNumCols();
            int nRows = board.getNumRows();
            assert (nRows == nCols) : " rows and cols must be the same in go";

            CandidateMoveAnalyzer candidateMoves = new CandidateMoveAnalyzer(board);

            boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

            for (int i = 1; i <= nCols; i++ )
                for (int j = 1; j <= nRows; j++ )
                    // if its a candidate move and not an immediate take-back (which would break the rule of ko)
                    if ( candidateMoves.isCandidateMove( j, i ) && !isTakeBack( j, i, (GoMove) lastMove, board ) ) {
                        GoMove m = GoMove.createGoMove( j, i, lastMove.getValue(), new GoStone(player1) );

                        if ( m.isSuicidal(board) ) {
                            GameContext.log( 2, "The move was a suicide (can't add it to the list): " + m );
                        }
                        else {
                            prof.stopGenerateMoves();
                            board.makeMove( m );
                            prof.startGenerateMoves();
                            // this value is not likely to change much except local to last move,
                            // anyway we could cache that?
                            prof.startCalcWorth();
                            m.setValue(worth( m, weights, player1sPerspective ));
                            prof.stopCalcWorth();
                            // now revert the board
                            prof.stopGenerateMoves();
                            board.undoMove();
                            prof.startGenerateMoves();
                            moveList.add( m );
                        }
                    }

            moveList = (List<GoMove>) getBestMoves(player1, moveList, player1sPerspective);

            // if we are well into the game, include a passing move.
            // if none of the generated moves have an inherited value better than the passing move
            // (which just uses the value of the current move) then we should pass
            if (getNumMoves() > nCols+nRows)  {
                moveList.add(moveList.size(), GoMove.createPassMove(lastMove.getValue(), player1));
            }
            prof.stopGenerateMoves();

            return moveList;
        }
    }

}
