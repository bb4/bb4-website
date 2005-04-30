package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.checkers.CheckersController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.Move;
import com.becker.game.common.*;
import com.becker.optimization.ParameterArray;

import java.util.*;

/**
 * Defines for the computer how it should play Chess.
 * Chess is very similar to Checkers so we derive from the CheckersController
 *
 * @author Barry Becker
 */
public class ChessController extends CheckersController
{
    // @@todo:
    //  - show indicator of invalid move while dragging piece (before placed)
    //  - don't allow moves that put the king in check.
    //  - if you are in check, then don't allow moves other than those that get you out of check.
    //  - game is over if no moves available (because of check mate usually).
    //  - exchange pawn for best peice when it reaches the other side.
    //  - there is a tendancy to get into an infinite cycle at the end of a computer vs computer game.
    //  - castling.
    //  - account for amount of king endangerment in worth.
    //  - Checkers and Chess should probably have a common abstract base class, but I can't think of a good
    //    name for it, so currently Chess just derives from Checkers.

    // these weights determine how the computer values features of the board
    // if only one computer is playing, then only one of the weights arrays is used.
    // use these weights if no others are provided
    private static final double[] DEFAULT_WEIGHTS = {1.1, 7.0, 10.0, 10.0, 14.0, 2.*WINNING_VALUE, .5};
    // don't allow the weights to exceed these maximum values
    private static final double[] MAX_WEIGHTS = {10.0, 100.0, 100.0, 100.0, 100.0, 2.*WINNING_VALUE+1.0, 10.0 };
    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {"Pawn weight", "Knight weight", "Rook weight",
                                                               "Bishop weight", "queen weight", "King weight",
                                                               "Pawn Advancement weight"};
    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Weight to associate with each remaining pawn",
        "Weight to associate with Knights",
        "Weight to associate with Rooks",
        "Weight to associate with Bishops",
        "Weight to associate with the Queen",
        "Weight to associate with the King",
        "Weight to associate with pawn advancement"
    };
    private static final int PAWN_WEIGHT_INDEX = 0;
    private static final int KNIGHT_WEIGHT_INDEX = 1;
    private static final int ROOK_WEIGHT_INDEX = 2;
    private static final int BISHOP_WEIGHT_INDEX = 3;
    private static final int QUEEN_WEIGHT_INDEX = 4;
    private static final int KING_WEIGHT_INDEX = 5;
    private static final int PAWN_ADVANCEMENT_WEIGHT_INDEX = 6;

    // initial look ahead factor.
    private static final int DEFAULT_LOOKAHEAD = 3;


    /**
     *   Construct the Chess game controller.
     */
    public ChessController()
    {
        initializeData();
        board_ = new ChessBoard();
    }

    /**
     * this gets the Chess specific weights.
     */
    protected void initializeData()
    {
        weights_ = new GameWeights( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

    /**
     * The computer makes the first move in the game.
     */
    public void computerMovesFirst()
    {
        // create a bogus previous move
        ChessMove lastMove = ChessMove.createMove( 2,  2,  3,  3,
                null, 0, new ChessPiece(false, ChessPiece.REGULAR_PIECE));

        // determine the possible moves and choose one at random.
        List moveList = generateMoves( lastMove, weights_.getPlayer1Weights(), true );

        int r = (int) (Math.random() * moveList.size());
        ChessMove m = (ChessMove) moveList.get( r );

        makeMove( m );
        //getMoveList().add( m );
        player1sTurn_ = false;
    }

    protected final int getDefaultLookAhead()
    {
        return DEFAULT_LOOKAHEAD;
    }

    /**
     *  @return the default top percentage of best moves to consider at each ply.
     */
    protected int getDefaultBestPercentage()
    {
        return 100;
    }

    /**
     *  The primary way of computing the score for Chess is to just add up the pieces
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
        int row, col;
        double score = 0;

        // evaluate the board after the move has been made
        for ( row = 1; row <= NUM_ROWS; row++ ) {      //rows
            for ( col = 1; col <= NUM_COLS; col++ ) {  //cols
                BoardPosition pos = board_.getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();
                    int side = piece.isOwnedByPlayer1()?1:-1;
                    switch (piece.getType()) {
                        case ChessPiece.PAWN :
                            //  pawn advancemnt
                            int advance = (piece.isOwnedByPlayer1()? pos.getRow()-1: (NUM_ROWS-pos.getRow()-1));
                            score += side * advance * weights.get(PAWN_ADVANCEMENT_WEIGHT_INDEX).value;
                            score += side * weights.get(PAWN_WEIGHT_INDEX).value; break;
                        case ChessPiece.KNIGHT :
                            score += side * weights.get(KNIGHT_WEIGHT_INDEX).value; break;
                        case ChessPiece.ROOK :
                            score += side * weights.get(ROOK_WEIGHT_INDEX).value; break;
                        case ChessPiece.BISHOP :
                            score += side * weights.get(BISHOP_WEIGHT_INDEX).value; break;
                        case ChessPiece.QUEEN :
                            score += side * weights.get(QUEEN_WEIGHT_INDEX).value; break;
                        case ChessPiece.KING :
                            score += side * weights.get(KING_WEIGHT_INDEX).value; break;
                        default : assert false:("bad chess piece type:"+ piece.getType());
                    }
                }
            }
        }
        return score;
    }


     /**
     *  generate all possible next moves
     */
    public List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        List moveList = new LinkedList();
        int row,col;
        player1sPerspective_ = player1sPerspective;

        boolean player1 = !(lastMove.player1);

        // scan through the board positions. For each each piece of the current player's,
        // add all the moves that it can make.
        for ( row = 1; row <= NUM_ROWS; row++ ) {
            for ( col = 1; col <= NUM_COLS; col++ ) {
                BoardPosition pos = board_.getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1 ) {
                    addMoves( pos, moveList, lastMove, weights,  player1sPerspective);
                }
            }
        }

        // remove any moves that causes the king goes into jeopardy (ie check).
        removeSelfCheckingMoves(moveList);

        return getBestMoves( player1, moveList, player1sPerspective );
    }

    /**
     * Find all the moves a piece p can make and insert them into moveList.
     *
     * @param pos the piece to check.
     * @param moveList add the potential moves to this existing list.
     * @param weights to use.
     * @return the number of moves added.
     */
    public int addMoves( BoardPosition pos, List moveList, TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        List moves = ((ChessPiece)pos.getPiece()).findPossibleMoves(board_, pos.getRow(), pos.getCol(), lastMove);

        // score the moves in this list
        Iterator it = moves.iterator();
        while (it.hasNext()) {
            ChessMove move = (ChessMove)it.next();
            // first apply the move
            board_.makeMove(move);
            move.value = worth(move, weights, player1sPerspective);
            board_.undoMove();
        }
        moveList.addAll( moves );

        return moveList.size();
    }

    /**
     * remove any moves that put the king in jeopardy.
     * @param moveList
     */
    public void removeSelfCheckingMoves(List moveList)
    {
        ChessBoard b = (ChessBoard)board_;
        Iterator it = moveList.iterator();
        while (it.hasNext()) {
           ChessMove move = (ChessMove)it.next();
           if (b.causesSelfCheck(move)) {
                //System.out.println( "don't allow "+move+" because it puts the king in check." );
                it.remove();
           }
        }
    }


    /**
     * @@ quiescent search not yet implemented for Chess
     * Probably we should return all moves that capture opponent pieces or put the king in check.
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
