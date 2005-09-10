package com.becker.game.twoplayer.go;


import static com.becker.game.twoplayer.go.GoControllerConstants.*;   // jdk 1.5 feature

import ca.dj.jigo.sgf.Point;
import ca.dj.jigo.sgf.SGFException;
import ca.dj.jigo.sgf.SGFGame;
import ca.dj.jigo.sgf.SGFLoader;
import ca.dj.jigo.sgf.tokens.*;
import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameWeights;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.ParameterArray;
import com.becker.sound.MusicMaker;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
 *  1) any suicide move is considered illegal
 *  2) When you pass, your opponent gets a point.
 *     For computer go, this is useful because it forces
 *     decision/completion of groups to help determine life and death and a proper score.
 *  3) the game terminates when the 2 players pass consecutively
 *  4) score = komi (5.5) + stones on board of player + player territory (note: captures not counted)
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
 * If the list grows no more, it will take me 8 months until I'm finished at my current rate.
 * If I were to become unemployed, I would probably complete in one-third the time.
 * In reality, I'll probably never finish. That's ok, I'm not sure I want to. I enjoy doing it.
 *
 * high priority todo:
 *
 *  - add test cases for every little method of every class
 *  - why don't test cases find optimal moves?
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
 *    - if the computer or player resigns, the playerWon vars should be set and the strcngth of the win should be large.
 *    - remember good moves that have not yet been played. On a big board, they will probably remain good moves.
 *      These good moves should be at the head of a list when possible moves are being generated.
 *
 ** Packaging issues
 *    - images on index web page should link directly to applets.
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
 *    - alpha-beta and quiescent setter/getter methods could be properties of the SearchStrategy instead of the game controller.
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
 *
 *
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

    private float komi_ = DEFAULT_KOMI;


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

    protected TwoPlayerOptions createOptions() {

        TwoPlayerOptions options = new TwoPlayerOptions(DEFAULT_LOOKAHEAD, BEST_PERCENTAGE, MusicMaker.SHAMISEN);
        options.setPlayerName(true, GameContext.getLabel("BLACK"));
        options.setPlayerName(false, GameContext.getLabel("WHITE"));
        return options;
    }

    /**
     * this gets the Go specific patterns and weights.
     */
    protected void initializeData()
    {
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
        weights_ = new GameWeights( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
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


    protected boolean processToken(SGFToken token, List moveList) {

        boolean found = false;
        if (token instanceof MoveToken ) {
            moveList.add( createMoveFromToken( (MoveToken) token ) );
            found = true;
        }
        else if (token instanceof AddBlackToken ) {
            addMoves((PlacementListToken)token, moveList);
            found = true;
        }
        else if (token instanceof AddWhiteToken ) {
            addMoves((PlacementListToken)token, moveList);
            found = true;
        }
        else if (token instanceof CharsetToken ) {
            //CharsetToken charsetToken = (CharsetToken) token;
            //System.out.println("charset="+charsetToken.getCharset());
        }
        else if (token instanceof OverTimeToken ) {
            //OverTimeToken charsetToken = (OverTimeToken) token;
            //System.out.println("charset="+charsetToken.getCharset());
        }
        else if (token instanceof TextToken ) {
            TextToken textToken = (TextToken) token;
            System.out.println("text="+textToken.getText());
        } else {
            System.out.println("\nignoring token "+token.getClass().getName());
        }
        return found;
    }

    /**
     * add a sequence of moves all at once.
     * Such as placing handicaps when readin gfrom an sgf file.
     * @param token
     * @param moveList
     */
    private static void addMoves(PlacementListToken token, List moveList) {
        List points = token.getPoints();
        boolean player1 = token instanceof AddBlackToken;

        for (int i=0; i<points.size(); i++)  {
            Point point = (Point)points.get(i);
            moveList.add( new GoMove( point.y, point.x, 0, new GoStone(player1)));
        }
    }


    protected Move createMoveFromToken( MoveToken token)
    {
        if (token.isPass()) {
            return GoMove.createPassMove(0, !token.isWhite());
        }
        return new GoMove( token.getY(), token.getX(), 0, new GoStone(!token.isWhite()));
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

    private int cachedBlackTerritoryEstimate_ = 0;
    private int cachedWhiteTerritoryEstimate_ = 0;
    /**
     * get a territory estimate for player1 or player2
     * When the game is over, this should return a precise value for the amount of territory (not yet filled with captures).
     * So the estimate will be very rough at the beginning of the game, but should get better as more pieces are played.
     *
     * Since this can be called white we are processing, we return cached values in
     * those cases to avoit a ConcurrentModificationException.
     *
     * @param forPlayer1 if true, get the captures for player1, else for player2
     * @return estimate of the amount of territory the player has
     */
    public int getTerritoryEstimate( boolean forPlayer1 )
    {
        Move m = board_.getLastMove();
        if ( m == null )
            return 0;

        if (forPlayer1)  {
            cachedBlackTerritoryEstimate_ = ((GoBoard)board_).getTerritoryEstimate(forPlayer1, true);
            return cachedBlackTerritoryEstimate_;
        }
        else  {
            cachedWhiteTerritoryEstimate_ = ((GoBoard)board_).getTerritoryEstimate(forPlayer1, true);
            return cachedWhiteTerritoryEstimate_;
        }
    }

    /**
     *
     * @param forPlayer1
     * @return the actual score (each empty space counts as one)
     */
    public int getTerritory( boolean forPlayer1 )
    {
        if (forPlayer1)  {
            return((GoBoard)board_).getTerritoryEstimate(forPlayer1, false);
        }
        else  {
            return ((GoBoard)board_).getTerritoryEstimate(forPlayer1, false);
        }
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

    public void setKomi(float komi) {
        komi_ = komi;
    }

    private float getKomi() {
        return komi_;
    }

    /**
     * save the current state of the go game to a file in SGF (4) format
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    public void saveToFile( String fileName, AssertionError ae )
    {
        GameContext.log( 1, "saving state to :" + fileName );

        try {
            FileWriter out = new FileWriter( fileName );
            //PrintWriter foo;
            // SGF header info
            out.write( "(;\n" );
            out.write( "FF[4]\n" );
            out.write( "GM[1]\n" );
            out.write( "CA[UTF-8]\n" );
            out.write( "ST[2]\n" );
            out.write( "RU[japanese]\n" );
            out.write( "SZ["+this.getBoard().getNumRows()+"]\n" );
            out.write( "PB["+this.getPlayer1().getName()+"]\n" );
            out.write( "PW["+this.getPlayer2().getName()+"]\n" );
            out.write( "KM["+getKomi()+"]\n" );
            out.write( "PC[US]\n" );
            out.write( "HA[" + ((GoBoard) board_).getHandicap() + "]\n" );
            out.write( "GN[test1]\n" );
            // out.write("PC[US]"); ?? add the handicap stones if present
            Iterator it = getMoveList().iterator();
            GameContext.log( 2, "movelist size= " + getMoveList().size() );
            while ( it.hasNext() ) {
                GoMove move = (GoMove) it.next();
                out.write( move.getSGFRepresentation() );
            }
            // include error info and stack trace in the comments to help debug
            if ( ae != null ) {
                out.write( "C[" );
                out.write( GoBoardUtil.getGroupsText(((GoBoard) getBoard()).getGroups() ));
                if ( ae.getMessage() != null ) {
                    out.write( ae.getMessage() );
                    //out would need to be a PrintWriter for this to work
                    //rte.printStackTrace(out);
                }
                out.write( "]\n" );
            }
            out.write( ')' );
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public void restoreFromFile( String fileName ) {

        try {
            FileInputStream iStream = new FileInputStream( fileName );
            GameContext.log( 2, "opening " + fileName );
            SGFGame game = SGFLoader.load( iStream );
            restoreGame( game );
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog( null, "file " + fileName + " was not found." + fnfe.getMessage() );
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog( null, "IOException occurrred while reading " + fileName + " :" + ioe.getMessage() );
        } catch (SGFException sgfe) {
            JOptionPane.showMessageDialog( null, "file " + fileName + " had an SGF error while loading: " + sgfe.getMessage() );
            sgfe.printStackTrace();
        }
    }

    protected void parseSGFGameInfo( SGFGame game) {
        Enumeration e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken)token;
                size = sizeToken.getSize();
            }
            else if (token instanceof KomiToken) {
                KomiToken komiToken = (KomiToken) token;
                this.setKomi(komiToken.getKomi());
            }
            else if (token instanceof HandicapToken) {
                //HandicapToken handicapToken = (HandicapToken) token;
                // so we don't guess wrong on where the handicap positions are
                // we will rely on their being an AB command to specifically tell where the handicap stones are
                //GameContext.log(2,"***handicap ="+handicapToken.getHandicap());
                //this.setHandicap(handicapToken.getHandicap());
            }
            else if (token instanceof WhiteNameToken) {
                WhiteNameToken nameToken = (WhiteNameToken) token;
                this.getPlayer2().setName(nameToken.getName());
            }
            else if (token instanceof BlackNameToken) {
                BlackNameToken nameToken = (BlackNameToken) token;
                this.getPlayer1().setName(nameToken.getName());
            }
            else if (token instanceof KomiToken) {
                KomiToken komiToken = (KomiToken) token;
                this.setKomi(komiToken.getKomi());
            }
            else if (token instanceof RuleSetToken) {
                //RuleSetToken ruleToken = (RuleSetToken) token;
                //this.setRuleSet(ruleToken.getKomi());
            }
        }
        this.getBoard().setSize(size, size);
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
        worth = weights.get(CAPTURE_WEIGHT_INDEX).value * (getNumCaptures( true ) - getNumCaptures( false ));
        float n = 2.0f * board.getNumRows();
        // opening = 1.99 - 1.5    middle = 1.5 - 1.01   end = 1.0
        double gameStageBoost = 0.5 + 2.0 * Math.max((n - (float)getNumMoves())/n, 0.0);

        for ( row = 1; row <= board.getNumRows(); row++ ) {    //rows
            for ( col = 1; col <= board.getNumCols(); col++ ) {  //cols
                GoBoardPosition position = (GoBoardPosition) board.getPosition( row, col );
                double badShapeScore;
                double posScore;
                if (position.isInEye())  {
                    if (position.isOccupied()) {
                        // a dead enemy stone in the eye
                        position.setScoreContribution((position.getEye().isOwnedByPlayer1()? 2.0:-2.0));
                    }
                    else {
                        position.setScoreContribution((position.getEye().isOwnedByPlayer1()? 1.0:-1.0));
                    }
                    worth += position.getScoreContribution();
                }
                else if ( position.isOccupied() ) {
                    GoStone stone = (GoStone)position.getPiece();

                    int side = position.getPiece().isOwnedByPlayer1()? 1: -1;
                    // penalize bad shape like empty triangles
                    badShapeScore =
                         -(side * board.formsBadShape(position)
                                * weights.get(BAD_SHAPE_WEIGHT_INDEX).value);

                    // consider where the stones are played
                    // (usually a very low weight is assigned to this unless we are at the start of the game)
                    posScore =
                        (side * gameStageBoost * weights.get(POSITIONAL_WEIGHT_INDEX).value * positionalScore_[row][col]);

                    double s = weights.get(HEALTH_WEIGHT_INDEX).value * stone.getHealth() + posScore + badShapeScore;
                    position.setScoreContribution(Math.max(-1.0, Math.min(1.0, s)));

                    if (GameContext.getDebugMode() > 0)  {
                        stone.badShapeScore = badShapeScore;
                        stone.positionalScore = posScore;
                    }
                    worth += position.getScoreContribution();
                } else {
                    position.setScoreContribution(0);
                }
            }

        }
        worth += board.getTerritoryDelta();

        //GameContext.log(1,"GoController.worth: worth="+worth);
        if ( worth < -WIN_THRESHOLD ) {
            // then the margin is too great
            return -WINNING_VALUE;
        }
        if ( worth > WIN_THRESHOLD ) {
            // then the margin is too great
            return WINNING_VALUE;
        }

        return worth;
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
        int captures = getNumCaptures(!player1) + (player1 ? numDeadWhiteStonesOnBoard_ : numDeadBlackStonesOnBoard_);
        return getTerritory(player1) - captures;
    }

   /**
    * Update the final life and death status of all the stones still on the board.
    * This method must only be called at the end of the game or stones will get prematurely marked as dead.
    * @@ should do in 2 passes. The first can update the health of groups and perhaps remove obviously dead stones.
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
                    GameContext.log(1, "life & death: "+space+" health="+stone.getHealth() +" string health=" +space.getGroup().getRelativeHealth());
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
