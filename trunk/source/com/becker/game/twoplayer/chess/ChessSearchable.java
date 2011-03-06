package com.becker.game.twoplayer.chess;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerSearchable;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 * For searching the Chess game tree.
 * @@ extract aq MoveGenerator
 *
 * @author Barry Becker
 */
public class ChessSearchable extends TwoPlayerSearchable {

    public ChessSearchable(TwoPlayerBoard board, PlayerList players, SearchOptions options) {
        super(board, players, options);
    }

    public ChessSearchable(ChessSearchable searchable) {
        super(searchable);
    }

    public ChessSearchable copy() {
        return new ChessSearchable(this);
    }

    @Override
    public ChessBoard getBoard() {
        return (ChessBoard) board_;
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
    @Override
    public int worth( Move lastMove, ParameterArray weights )  {
        int row, col;
        double score = 0;

        // evaluate the board after the move has been made
        for ( row = 1; row <= ChessController.NUM_ROWS; row++ ) {
            for ( col = 1; col <= ChessController.NUM_COLS; col++ ) {
                BoardPosition pos = getBoard().getPosition( row, col );
                if ( pos.isOccupied() ) {
                    ChessPiece piece = (ChessPiece)pos.getPiece();
                    int side = piece.isOwnedByPlayer1() ? 1 : -1;
                    int advancement =
                            (piece.isOwnedByPlayer1() ? pos.getRow()-1 : (ChessController.NUM_ROWS - pos.getRow()-1));
                    score += piece.getWeightedScore(side, pos, weights, advancement);
                }
            }
        }
        return (int)score;
    }


    /**
     *  generate all possible next moves.
     */
   public MoveList generateMoves( TwoPlayerMove lastMove, ParameterArray weights) {
       MoveList moveList = new MoveList();
       int row,col;

       boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());

       // scan through the board positions. For each each piece of the current player's,
       // add all the moves that it can make.
       for ( row = 1; row <= ChessController.NUM_ROWS; row++ ) {
           for ( col = 1; col <= ChessController.NUM_COLS; col++ ) {
               BoardPosition pos = getBoard().getPosition(row, col);
               if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1 ) {
                   addMoves( pos, moveList, lastMove, weights);
               }
           }
       }

       // remove any moves that causes the king goes into jeopardy (ie check).
       removeSelfCheckingMoves(moveList);

       return  bestMoveFinder_.getBestMoves( player1, moveList);
   }

   /**
    * @@todo
    * @return those moves that result in check or getting out of check.
    */
   public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
       return new MoveList();
   }

    /**
     * Find all the moves a piece p can make and insert them into moveList.
     *
     * @param pos the piece to check.
     * @param moveList add the potential moves to this existing list.
     * @param weights to use.
     * @return the number of moves added.
     */
    int addMoves( BoardPosition pos, MoveList moveList, TwoPlayerMove lastMove, ParameterArray weights) {
        List<ChessMove> moves =
                ((ChessPiece)pos.getPiece()).findPossibleMoves(getBoard(), pos.getRow(), pos.getCol(), lastMove);

        // score the moves in this list
        for (ChessMove move : moves) {
            // first apply the move
            getBoard().makeMove(move);
            move.setValue(worth(move, weights));
            getBoard().undoMove();
        }
        moveList.addAll( moves );

        return moveList.size();
    }

    /**
     * remove any moves that put the king in jeopardy.
     * @param moveList
     */
    public void removeSelfCheckingMoves(List moveList) {

        Iterator it = moveList.iterator();
        while (it.hasNext()) {
           ChessMove move = (ChessMove)it.next();
           if (getBoard().causesSelfCheck(move)) {
                GameContext.log(2, "don't allow " + move + " because it puts the king in check." );
                it.remove();
           }
        }
    }
}
