package com.becker.game.twoplayer.checkers;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.sound.MusicMaker;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines for the computer how it should play checkers
 *
 * @author Barry Becker
 */
public class CheckersController extends TwoPlayerController
{

    private static final int DEFAULT_LOOKAHEAD = 4;

    // the checkers board must be 8*8
    protected static final int NUM_ROWS = 8;
    protected static final int NUM_COLS = 8;

    // normally this would be an argument to generateMoves, but worth is not called directly
    // and I want to avoid adding it to the aqrlists for 3 functions
    protected boolean player1sPerspective_ = true;

    /**
     *  Construct the Checkers game controller.
     */
    public CheckersController()
    {
        initializeData();
        board_ = new CheckersBoard();
    }

    /**
     * this gets the checkers specific weights.
     */
    protected void initializeData()
    {
        weights_ = new CheckersWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions(DEFAULT_LOOKAHEAD, 100, MusicMaker.SITAR);
    }

    /**
     * The computer makes the first move in the game.
     */
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        List moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights(), true );

        assert (!moveList.isEmpty());
        makeMove( getRandomMove(moveList) );

        player1sTurn_ = false;
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public double getStrengthOfWin()
    {
        if (!getPlayer1().hasWon() && !getPlayer2().hasWon())
             return 0.0;
        return worth(board_.getLastMove(), weights_.getDefaultWeights());
    }


    /**
     * given a move determine whether the game is over.
     * If we are at maxMoves, the one with a greater value of pieces wins.
     * If the count of pieces is the same, then it is a draw.
     *
     * @param m the move to check
     * @param recordWin if true then the controller state will record wins
     */
    public boolean done( TwoPlayerMove m, boolean recordWin )
    {
        if (m == null)
            return true;

        boolean won = (Math.abs( m.getValue() ) >= WINNING_VALUE);

        if ( won && recordWin ) {
            if ( m.isPlayer1() )
                getPlayer1().setWon(true);
            else
                getPlayer2().setWon(true);
        }
        if ( getNumMoves() >= board_.getMaxNumMoves() ) {
            won = true;
            if ( recordWin ) {
                if ( Math.abs( m.getValue() ) >= 0 )
                    getPlayer1().setWon(true);
                else
                    getPlayer2().setWon(true);
            }
        }
        return (won);
    }

    /**
     *  The primary way of computing the score for checkers is to just add up the pieces
     *  Kings should count more heavily. How much more is determined by the weights.
     *  We also give a slight bonus for advancement of non-kings to incent them to
     *  become kings.
     *  note: lastMove is not used
     *  @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        int row, col, odd;
        int posScore = 0;
        int negScore = 0;

        for ( row = 1; row <= NUM_ROWS; row++ ) {    //rows
            odd = row % 2;
            for ( int j = 1; j <= 4; j++ ) {  //cols
                col = 2 * j - odd;
                BoardPosition p = board_.getPosition( row, col );
                if ( p.isOccupied() ) {
                    CheckersPiece piece = (CheckersPiece) p.getPiece();
                    if ( piece.isKing()) {
                        if ( piece.isOwnedByPlayer1() )
                            posScore += weights.get(CheckersWeights.KINGED_WEIGHT_INDEX).getValue();
                        else
                            negScore -= weights.get(CheckersWeights.KINGED_WEIGHT_INDEX).getValue();
                    }
                    else { // REGULAR_PIECE
                        if ( piece.isOwnedByPlayer1() ) {
                            posScore += weights.get(CheckersWeights.PIECE_WEIGHT_INDEX).getValue();
                            posScore += weights.get(CheckersWeights.ADVANCEMENT_WEIGHT_INDEX).getValue() * row;
                        }
                        else {
                            negScore -= weights.get(CheckersWeights.PIECE_WEIGHT_INDEX).getValue();
                            negScore -= weights.get(CheckersWeights.ADVANCEMENT_WEIGHT_INDEX).getValue() * (9 - row);
                        }
                    }
                }
            }
        }
        if ( posScore == 0 ) {
            // then there are no more of player 1's pieces
            return -WINNING_VALUE;
        }
        if ( negScore == 0 ) {
            // then there is no more of player 2's pieces
            return WINNING_VALUE;
        }
        return (posScore + negScore);
    }

    /**
     * Check to see if this jump requires additional jumps
     * If it does, we create a new move, because there could potentially be 2 jumps possible
     * from the last position.
     */
    private int checkJumpMove( BoardPosition current,
                               CheckersMove m, int rowInc, int colInc,
                               List<CheckersMove> jumpMoves, ParameterArray weights )
    {
        BoardPosition next = board_.getPosition( current.getRow() + rowInc, current.getCol() + colInc );
        BoardPosition beyondNext = board_.getPosition( current.getRow() + 2 * rowInc, current.getCol() + 2 * colInc );
        // if the adjacent square is an opponent's piece, and the space beyond it
        // is empty, and we have not already capture this peice, then take another jump.
        boolean opponentAdjacent =
                next!=null && next.isOccupied() && (next.getPiece().isOwnedByPlayer1() != m.isPlayer1());
        if ( opponentAdjacent
              && beyondNext!=null && beyondNext.isUnoccupied()
              && (m.captureList != null) && (!m.captureList.alreadyCaptured( next )) ) {
            // then there is another jump. We must take it.
            CheckersMove mm = (CheckersMove) m.copy();  // base it on the original jump
            mm.setToRow(beyondNext.getRow());
            mm.setToCol(beyondNext.getCol());
            mm.captureList.add( next.copy() );
            // next.setPiece(null); ?

            boolean justKinged = false;   // ?? may be superfluous
            GameContext.log( 2, "calling findJumpMoves on " +
                    beyondNext + " rowinc=" + rowInc + "length of capturelist=" + mm.captureList.size() );
            if ( (mm.getPiece().getType() == CheckersPiece.REGULAR_PIECE) &&
                    ((mm.isPlayer1() && mm.getToRow() == NUM_ROWS)
                    || (!mm.isPlayer1() && mm.getToRow() == 1)) ) {
                mm.kinged = true;
                justKinged = true;
                mm.getPiece().setType(CheckersPiece.KING);
                GameContext.log( 2, "KINGED: " + mm );
            }
            else
                mm.kinged = false;

            List<CheckersMove> list;
            // we cannot make more jumps if we just got kinged.
            if (!justKinged) {    // may be superfluous
                list = findJumpMoves( beyondNext, rowInc, mm, weights );
                assert ( list!=null );
                jumpMoves.addAll( list );
                return list.size();
            }
        }
        return 0;  // no additional move added
    }

    /**
     * Find all the possible jumps using the specified piece.
     * Remember that in checkers, all possible jumps in a row must be taken.
     * For example, you can not do just a double jump if a triple jump is possible.
     * This cannot return null since there is at least the first jump.
     * When jumping we remove the piece and add it to the captureList so they
     * won't be taken twice in the same move. At the end we return the captured
     * pieces to the board so the state is not change.
     */
    private List<CheckersMove> findJumpMoves( BoardPosition current,
                                      int rowInc, CheckersMove m,
                                      ParameterArray weights )
    {
        List<CheckersMove> jumpMoves = new LinkedList<CheckersMove>();
        // if there are jumps beyond this we have to make them.
        // We have at least the current jump m.

        // once moreJumps becomes true we must add additional moves
        boolean moreJumps = false;

        // first check the forward moves
        if ( checkJumpMove( current, m, rowInc, -1, jumpMoves, weights ) > 0 )
            moreJumps = true;
        if ( checkJumpMove( current, m, rowInc, 1, jumpMoves, weights ) > 0 )
            moreJumps = true;

        // note you cannot continue making jumps from the point at which you are kinged.
        if ( m.getPiece().getType() == CheckersPiece.KING && !m.kinged ) {
            if ( checkJumpMove( current, m, -rowInc, -1, jumpMoves, weights ) > 0 )
                moreJumps = true;
            if ( checkJumpMove( current, m, -rowInc, 1, jumpMoves, weights ) > 0 )
                moreJumps = true;
        }

        if ( !moreJumps ) { // base case of recursion
            // we can finally add the move after we evaluate its worth
            board_.makeMove( m );
            m.setValue(worth( m, weights, player1sPerspective_ ));
            board_.undoMove();

            jumpMoves.add( m );

            return jumpMoves;
        }
        return jumpMoves;
    }

    /**
     * Find all the moves piece p can make in a given diagonal direction
     *
     * @param pos the piece to check
     * @param moveList add the potential moves to this existing list
     * @return the number of moves added
     */
    private int addMovesForDirection( BoardPosition pos, List<CheckersMove> moveList,
                                      int rowInc, int colInc, TwoPlayerMove lastMove, ParameterArray weights )
    {
        CheckersMove m;
        BoardPosition next = board_.getPosition( pos.getRow() + rowInc, pos.getCol() + colInc );
        if ( next!=null && next.isUnoccupied() ) {
            assert ( pos!=null): "pos is null" ;
            double val = 0;
            if ( lastMove != null ) {
                // then not the first move of the game
                val = lastMove.getValue();
            }
            m = CheckersMove.createMove( pos.getRow(), pos.getCol(),
                    (pos.getRow() + rowInc), (pos.getCol() + colInc),
                    null, val, pos.getPiece().copy() );

            // no need to evaluate it since there were no captures
            moveList.add( m );
            // only one move added
            return 1;
        }
        // if just a simple move was not possible, we check for jump(s)
        BoardPosition beyondNext = board_.getPosition( pos.getRow() + 2 * rowInc, pos.getCol() + 2 * colInc );
        if ( next!=null && next.isOccupied() &&
               (next.getPiece().isOwnedByPlayer1() != pos.getPiece().isOwnedByPlayer1()) &&
                beyondNext!=null && beyondNext.isUnoccupied()) {
            CaptureList capture = new CaptureList();
            capture.add( next.copy() );
            // make it blank so a king doesn't loop back and take it again.
            // next.setPiece(null);
            m = CheckersMove.createMove( pos.getRow(), pos.getCol(), beyondNext.getRow(), beyondNext.getCol(),
                    capture, lastMove.getValue(),  pos.getPiece().copy() );

            List<CheckersMove> jumps = findJumpMoves( beyondNext, rowInc, m, weights );
            moveList.addAll( jumps );

            return jumps.size();
        }
        return 0; // no moves added
    }

    /**
     * Find all the moves a piece p can make and insert them into moveList.
     *
     * @param p the piece to check.
     * @param moveList add the potential moves to this existing list.
     * @param weights to use.
     * @return the number of moves added.
     */
    public int addMoves( BoardPosition p, List<CheckersMove> moveList, TwoPlayerMove lastMove, ParameterArray weights )
    {
        int direction = -1;
        if ( p.getPiece().isOwnedByPlayer1() )
            direction = 1;

        int numMovesAdded = 0;
        int initialNumMoves = moveList.size();
        // check left and right forward diagonals
        numMovesAdded += addMovesForDirection( p, moveList, direction, -1, lastMove, weights );
        numMovesAdded += addMovesForDirection( p, moveList, direction, 1, lastMove, weights );

        // if its a KING we need to check the other direction too
        CheckersPiece piece = (CheckersPiece) p.getPiece();
        if ( piece.isKing() ) {
            int numKingMoves = 0;
            numKingMoves += addMovesForDirection( p, moveList, -direction, -1, lastMove, weights );
            numKingMoves += addMovesForDirection( p, moveList, -direction, 1, lastMove, weights );

            // we also need to verify that we are not cycling over previous moves (not allowed).
            // check moves in the list against the move 4 moves back if the same, we must remove it
            // we can skip if there were captures, since captures cannot be undone.
            int numMoves = getNumMoves();

            if ( numMoves - 4 > 1 ) {
                CheckersMove moveToCheck = (CheckersMove) getMoveList().get( numMoves - 4 );
                if ( moveToCheck.captureList == null ) {
                    int i = 0;

                    while ( i < numKingMoves ) {
                        CheckersMove m = moveList.get( initialNumMoves + numMovesAdded + i );
                        GameContext.log( 1, "lastMove="+ lastMove);
                        assert ( m.isPlayer1() == moveToCheck.isPlayer1()):
                                "player ownership not equal comparing \n"+m+" with \n"+moveToCheck;
                        if ( m.captureList == null &&
                                m.getToRow() == moveToCheck.getToRow() &&
                                m.getToCol() == moveToCheck.getToCol() &&
                                m.getFromRow() == moveToCheck.getFromRow() &&
                                m.getFromCol() == moveToCheck.getFromCol() &&
                                m.getPiece().getType() == moveToCheck.getPiece().getType() ) {
                            GameContext.log(0, "found a cycle. new score = " + m.getValue() +
                                    " old score=" + moveToCheck.getValue() + " remove move= " + m );
                            moveList.remove( m );
                            numKingMoves--;
                            break;
                        }
                        else {
                            i++;
                        }
                    }
                }
            }
            numMovesAdded += numKingMoves;
        }
        return numMovesAdded;
    }



    public Searchable getSearchable() {
        return new CheckersSearchable();
    }


    public class CheckersSearchable extends TwoPlayerSearchable {

        /**
         *  generate all possible next moves
         */
        public List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List<CheckersMove> moveList = new LinkedList<CheckersMove>();
            int j, row,col;
            player1sPerspective_ = player1sPerspective;

            boolean player1 = (lastMove != null)?  !(lastMove.isPlayer1()) : true;

            // scan through the board positions. For each each piece of the current player's,
            // add all the moves that it can make.
            for ( row = 1; row <= NUM_ROWS; row++ ) {
                int odd = row % 2;
                for ( j = 1; j <= 4; j++ ) {
                    col = 2 * j - odd;
                    BoardPosition p = board_.getPosition( row, col );
                    if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() == player1 ) {
                        addMoves( p, moveList, lastMove, weights );
                    }
                }
            }
            return getBestMoves( player1, moveList, player1sPerspective );
        }

        /**
         * @@ quiescent search not yet implemented for checkers
         * Probably we should return all moves that capture opponent pieces.
         *
         * @param lastMove
         * @param weights
         * @param player1sPerspective
         * @return list of urgent moves
         */
        public List generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            return null;
        }

        /**
         * returns true if the specified move caused one or more opponent pieces to become jeopardized
         */
        public boolean inJeopardy( TwoPlayerMove m )
        {
            return false;
        }
    }
}
