package com.becker.game.twoplayer.chess;

import com.becker.game.common.*;
import com.becker.game.common.Move;

import java.util.LinkedList;
import java.util.List;

/**
 *  the ChessChessBoardPosition describes the physical marker at a location on the board.
 *  It can beA ChessPiece is either empty or contains one of the standard chess pieces.
 *  This class has in it the rules for how each chess move can move.
 *  @@ consider making this an abstract class and faving subclasses for each piece type.
 *
 * @see ChessBoard
 * @author Barry Becker
 */
public class ChessPiece extends GamePiece
{

    // the basic kinds of chess pieces
    public static final char PAWN = 'P';
    public static final char ROOK = 'R';
    public static final char KNIGHT = 'N';   // K already used for king.
    public static final char BISHOP = 'B';
    public static final char QUEEN = 'Q';
    public static final char KING = 'K';

    // true until the piece has been moved the first time
    private boolean firstTimeMoved_ = true;

    public ChessPiece( boolean player1, char type)
    {
        super( player1, type);
    }

    /**
     *  create a deep copy of the position.
     */
    public GamePiece copy()
    {
        ChessPiece p = new ChessPiece( ownedByPlayer1_, type_);
        p.setTransparency( (short) 0 );
        p.setAnnotation( null );
        return p;
    }

    public void copy(GamePiece p)
    {
        super.copy(p);
        this.firstTimeMoved_ = ((ChessPiece)p).isFirstTimeMoved();
    }

    /**
     * @return  true if this is the first time that this piece has been moved in the game.
     */
    public boolean isFirstTimeMoved()
    {
        return firstTimeMoved_;
    }
    /**
     * @param firstTimeMoved whether or not this piece has been moved yet this game.
     */
    public void setFirstTimeMoved( boolean firstTimeMoved)
    {
        firstTimeMoved_ = firstTimeMoved;
    }

    /**
     * find all the posible moves that this piece can make.
     * If checkingChecks is true then moves that lead to a king capture are not allowed.
     * @@ this could move to ChessController
     * @param board  the board we are examining
     * @param lastMove the most recently made move.
     * @return a list of legal moves for this piece to make
     */
    public List findPossibleMoves(Board board, int row, int col, Move lastMove)
    {
        List moveList = null;

        switch (type_) {
            case PAWN : moveList = findPawnMoves(board, row, col, lastMove); break;
            case ROOK : moveList = findRookMoves(board, row, col, lastMove); break;
            case KNIGHT : moveList = findKnightMoves(board, row, col, lastMove); break;
            case BISHOP : moveList = findBishopMoves(board, row, col, lastMove); break;
            case QUEEN : moveList = findQueenMoves(board, row, col, lastMove); break;
            case KING : moveList = findKingMoves(board, row, col, lastMove); break;
            default: assert false:("bad chess piece type: "+type_);
        }
        return moveList;
    }

    /**
     * Find all the legal moves that this pawn can make.
     * @return list of legal pawn moves.
     */
    private List findPawnMoves(Board board, int row, int col, Move lastMove)
    {
        List moveList = new LinkedList();

        int direction = -1;
        if ( isOwnedByPlayer1() )
            direction = 1;

        // if this is the first time moved, we need to consider a 2 space jump
        if (firstTimeMoved_)
            checkPawnForward(row, col, direction, 2, board, moveList);
        // in general pawns move forward 1 space
        checkPawnForward(row, col, direction, 1, board, moveList);

        // pawns capture by moving diagonally. Check both diagonals for enemy peices we can capture.
        checkPawnDiagonal(row, col, direction, -1, board, moveList);
        checkPawnDiagonal(row, col, direction,  1, board, moveList);

        return moveList;
    }

    /**
     * see if its legal to move the pawn forward numSteps. If so, add it to the moveList.
     * @return moveList list of legal moves discovered so far.
     */
    private List checkPawnForward(int row, int col, int direction, int numSteps, Board b, List moveList)
    {
        BoardPosition next =  b.getPosition( row + numSteps*direction, col );
        checkForNonCapture(next, row, col, moveList);
        return moveList;
    }

     /**
     * see if its possible for the pawn to capture an opponent piece on the left or right diagonal.
     * If so, add it to the moveList.
     * @return moveList list of legal moves discovered so far.
     */
    private List checkPawnDiagonal(int row, int col, int direction, int colInc, Board b, List moveList)
    {
        BoardPosition diag =  b.getPosition( row + direction, col + colInc );
        return checkForCapture(diag, row, col, moveList);
    }

    /**
     * find all the moves for this rook given the current board configuration.
     * @@ allow castling as an option the first time it as moved.
     * @return list of possible moves.
     */
    private List findRookMoves(Board board, int row, int col, Move lastMove)
    {
        List moveList = new LinkedList();

        // consider horixontal and vertical directions.
        checkRunDirection(row, col, 1, 0,  board, moveList);
        checkRunDirection(row, col, -1, 0, board, moveList);
        checkRunDirection(row, col, 0, 1, board, moveList);
        checkRunDirection(row, col, 0, -1, board, moveList);

        return moveList;
    }

    /**
     * find all the moves for this bishop given the current board configuration.
     * @return list of possible moves
     */
    private List findBishopMoves(Board board, int row, int col, Move lastMove)
    {
        List moveList = new LinkedList();

        // consider the 4 diagonal directions.
        checkRunDirection(row, col, 1, 1,  board, moveList);
        checkRunDirection(row, col, 1, -1, board, moveList);
        checkRunDirection(row, col, -1, 1,  board, moveList);
        checkRunDirection(row, col, -1, -1,  board, moveList);

        return moveList;
    }

    /**
     * find all the moves for this queen given the current board configuration.
     * @return list of possible moves.
     */
    private List findQueenMoves(Board board, int row, int col, Move lastMove)
    {
        List moveList = new LinkedList();

        // the set of queen moves equals rook type moves and bishop type moves.
        // all 8 directions are covered by this.
        List rookMoveList = findRookMoves(board, row, col, lastMove);
        List bishopMoveList = findBishopMoves(board, row, col,  lastMove);

        moveList.addAll(rookMoveList);
        moveList.addAll(bishopMoveList);

        return moveList;
    }

    // These give all the positions for a knight's move.
    // check every pair of 2 in the sequence.
    private static final int[] knightMoveRow_ = {2,  2, -2, -2, 1, -1,  1, -1};
    private static final int[] knightMoveCol_ = {1, -1,  1, -1, 2,  2, -2, -2};

    /**
     * find all the moves for this knight given the current board configuration.
     * there are 8 postitions that the knight can move to, but only some may be legal.
     * @return list of possible moves.
     */
    private List findKnightMoves(Board board, int row, int col, Move lastMove)
    {
        return getEightDirectionalMoves(board, row, col, lastMove, knightMoveRow_, knightMoveCol_);
    }

    // These give all the positions for a knight's move.
    // check every pair of 2 in the sequence.
    private static final int[] kingMoveRow_ = {1, -1,  0,  0, -1,  1, -1,  1};
    private static final int[] kingMoveCol_ = {0,  0,  1, -1, -1,  1,  1, -1};

    /**
     * find all the moves for this King given the current board configuration.
     * We do not allow a king to move to a position that would put it in check.
     * @return list of possible moves.
     */
    private List findKingMoves(Board board, int row, int col, Move lastMove)
    {
        return getEightDirectionalMoves(board, row, col, lastMove, kingMoveRow_, kingMoveCol_);
    }


    /**
     * find moves for kings or knights which have 8 possible moves.
     * @return  those moves which are valid out of the eight possible that are checked.
     */
    private List getEightDirectionalMoves(Board board, int row, int col, Move lastMove, int[] rowOffsets, int[] colOffsets)
    {
        List moveList = new LinkedList();

        for (int i=0; i<8; i++) {
            BoardPosition next =
               board.getPosition( row + rowOffsets[i], col + colOffsets[i] );
            checkForCapture(next, row, col, moveList);
            checkForNonCapture(next, row, col, moveList);
        }
        return moveList;
    }

    /**
     * Check all the moves in the direction specified by rowDir and colDir. Add all legal moves in that direction.
     * @param moveList the accumulated possible moves
     * @return moveList
     */
    private List checkRunDirection(int curRow, int curCol, int rowDir, int colDir, Board board, List moveList)
    {
      // loop through all spaces between this piece and the next piece or the edge of the board.
      // if the next piece encountered in the specified direction is an opponent piece, then capture it.
      int row = (curRow+rowDir);
      int col = (curCol+colDir);
      BoardPosition next = board.getPosition( row, col );

      while ((next != null) && next.isUnoccupied() )   {
          ChessMove m = ChessMove.createMove( curRow, curCol, row, col,
                                              null, 0,  this );
          // no need to evaluate it since there were no captures
          moveList.add( m );
          row += rowDir;
          col += colDir;
          next = board.getPosition( row, col );
      }

      // if the first encountered piece is an enemy then add a move which captures it
      checkForCapture(next, curRow, curCol, moveList);

      return moveList;
    }

    /**
     * @param next candidate next move
     * @param moveList current list of legal moves for this piece
     * @return all current legal moves plus the capture if there is one
     */
    private List checkForNonCapture(BoardPosition next, int row, int col, List moveList)
    {
        if ( (next != null) &&  next.isUnoccupied()) {
            ChessMove m = ChessMove.createMove(row, col, next.getRow(), next.getCol(),
                                                null, 0.0, this );
            moveList.add( m );
        }
        return moveList;
    }


    /**
     * @param next candidate next move
     * @param moveList current list of legal moves for this piece
     * @return all current legal moves plus the capture if there is one
     */
    private List checkForCapture(BoardPosition next, int row, int col, List moveList)
    {
        if ( (next != null) &&  next.isOccupied() && (next.getPiece().isOwnedByPlayer1() != isOwnedByPlayer1())) {
            // there can only be one capture in chess.
            CaptureList capture = new CaptureList();
            capture.add( next.copy() );
            ChessMove m = ChessMove.createMove( row, col, next.getRow(), next.getCol(),
                                                 capture, 0, this );
            moveList.add( m );
        }
        return moveList;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer( super.toString() );
        if (this.isFirstTimeMoved())
            sb.append(" notYetMoved ");
        return sb.toString();
    }
}



