package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.checkers.CheckersController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.common.Move;
import com.becker.game.common.*;
import com.becker.optimization.ParameterArray;
import com.becker.sound.MusicMaker;

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
    //        Should show piece grayed our or transparent until in a valid position
    //        If you drop the piece in an invlid position, instead of showing an error messagem
    //        animate the piece back to its original position.
    //  - exchange pawn for best piece when it reaches the other side.
    //  - if you are in check, then don't allow moves other than those that get you out of check.
    //  - game is over if no moves available (because of check mate usually).
    //  - there is a tendancy to get into an infinite cycle at the end of a computer vs computer game.
    //  - castling.
    //  - account for amount of king endangerment in worth.
    //  - Checkers and Chess should probably have a common abstract base class, but I can't think of a good
    //    name for it, so currently Chess just derives from Checkers.

    // initial look ahead factor.
    private static final int DEFAULT_CHESS_LOOKAHEAD = 3;


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
        weights_ = new ChessWeights();
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
        List moveList = getSearchable().generateMoves( lastMove, weights_.getPlayer1Weights(), true );

        int r = (int) (Math.random() * moveList.size());
        ChessMove m = (ChessMove) moveList.get( r );

        makeMove( m );
        //getMoveList().add( m );
        player1sTurn_ = false;
    }

    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions(DEFAULT_CHESS_LOOKAHEAD, 100, MusicMaker.TAIKO_DRUM);
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
                            score += side * advance * weights.get(ChessWeights.PAWN_ADVANCEMENT_WEIGHT_INDEX).getValue();
                            score += side * weights.get(ChessWeights.PAWN_WEIGHT_INDEX).getValue(); break;
                        case ChessPiece.KNIGHT :
                            score += side * weights.get(ChessWeights.KNIGHT_WEIGHT_INDEX).getValue(); break;
                        case ChessPiece.ROOK :
                            score += side * weights.get(ChessWeights.ROOK_WEIGHT_INDEX).getValue(); break;
                        case ChessPiece.BISHOP :
                            score += side * weights.get(ChessWeights.BISHOP_WEIGHT_INDEX).getValue(); break;
                        case ChessPiece.QUEEN :
                            score += side * weights.get(ChessWeights.QUEEN_WEIGHT_INDEX).getValue(); break;
                        case ChessPiece.KING :
                            score += side * weights.get(ChessWeights.KING_WEIGHT_INDEX).getValue(); break;
                        default : assert false:("bad chess piece type:"+ piece.getType());
                    }
                }
            }
        }
        return score;
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
            move.setValue(worth(move, weights, player1sPerspective));
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
                GameContext.log(2, "don't allow "+move+" because it puts the king in check." );
                it.remove();
           }
        }
    }



    public Searchable getSearchable() {
        return new ChessSearchable();
    }


    public class ChessSearchable extends CheckersSearchable {

         /**
         *  generate all possible next moves
         */
        public List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
        {
            List moveList = new LinkedList();
            int row,col;
            player1sPerspective_ = player1sPerspective;

            boolean player1 = !(lastMove.isPlayer1());

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
    }

}
