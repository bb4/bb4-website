package com.becker.game.twoplayer.chess;

import com.becker.game.common.board.Board;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.Move;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  The ChessChessBoardPosition describes the physical marker at a location on the board.
 *  A ChessPiece is either empty or contains one of the standard chess pieces.
 *  This class has in it the rules for how each chess move can move.
 *
 * @@ make this an enum with findMoves as an abstract method.
 *  The images and label could also be part of the enum.
 *
 * @see ChessBoard
 * @author Barry Becker
 */
public class ChessPiece extends GamePiece
{
    private ChessPieceType pieceType_;

    /** true until the piece has been moved the first time.  */
    private boolean firstTimeMoved_ = true;

    public ChessPiece( boolean player1, ChessPieceType type)
    {
        super( player1, type.getSymbol());
        pieceType_ = type;
    }

    /**
     *  create a deep copy of the position.
     */
    @Override
    public GamePiece copy()
    {
        ChessPiece p = new ChessPiece( ownedByPlayer1_, pieceType_);
        p.setTransparency( (short) 0 );
        p.setAnnotation( null );
        return p;
    }

    @Override
    public void copy(GamePiece p)
    {
        super.copy(p);
        this.firstTimeMoved_ = ((ChessPiece)p).isFirstTimeMoved();
    }

    public boolean is(ChessPieceType type) {
        return pieceType_ == type;
    }

    public ChessPieceType getPieceType() {
        return pieceType_;
    }
    /**
     * @return  true if this is the first time that this piece has been moved in the game.
     */
    public boolean isFirstTimeMoved() {
        return firstTimeMoved_;
    }
    /**
     * @param firstTimeMoved whether or not this piece has been moved yet this game.
     */
    public void setFirstTimeMoved( boolean firstTimeMoved) {
        firstTimeMoved_ = firstTimeMoved;
    }

    /**
     * find all the posible moves that this piece can make.
     * If checkingChecks is true then moves that lead to a king capture are not allowed.
     *
     * @param board  the board we are examining
     * @param lastMove the most recently made move.
     * @return a list of legal moves for this piece to make
     */
    public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove) {
        return pieceType_.findPossibleMoves(board, row, col, lastMove, this);
    }

    public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
        return pieceType_.getWeightedScore(side, pos, weights, advancement);
    }

    public int typeIndex() {
        return pieceType_.ordinal() + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( super.toString() );
        if (this.isFirstTimeMoved())
            sb.append(" notYetMoved ");
        return sb.toString();
    }
}



