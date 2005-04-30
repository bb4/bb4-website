package com.becker.game.twoplayer.chess.ui;

import com.becker.game.twoplayer.checkers.ui.CheckersBoardViewer;
import com.becker.game.twoplayer.chess.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.*;

import javax.swing.*;
import java.awt.event.MouseMotionListener;
import java.util.List;


/**
 *  This class takes a ChessController as input and displays the
 *  Current state of the Chess Game. The ChessController contains a ChessBoard
 *  which describes this state.
 *  Since the chess board is very much like the checkers board viewer, we derive from that
 *  @see com.becker.game.twoplayer.checkers.ui.CheckersBoardViewer
 *
 *  @author Barry Becker
 */
public class ChessBoardViewer extends CheckersBoardViewer implements MouseMotionListener
{

    /**
     * Construct the viewer
     */
    public ChessBoardViewer()
    {
        pieceRenderer_ = ChessPieceRenderer.getRenderer();
    }


    protected GameController createController()
    {
        return new ChessController();
    }

    protected boolean customCheckFails(BoardPosition position, BoardPosition destp)
    {
       // intentionally do nothing.
       return false;
    }

    protected List getPossibleMoveList(BoardPosition position)
    {
        ChessPiece piece = (ChessPiece)position.getPiece();
        List possibleMoveList =
            piece.findPossibleMoves(getBoard(), position.getRow(), position.getCol(),
                                    getBoard().getLastMove());
        ((ChessController)controller_).removeSelfCheckingMoves(possibleMoveList);
        return possibleMoveList;
    }

    /**
     * Some moves require that the human players be given some kind of notification.
     * We need to see if this move caused the opponents king to be put in check.
     * We need to check all of our pieces, not just the one moved, since the movement of one
     * piece may cause another (that was previously blocked by the piece we just moved) to put
     * the opponents king in jeopardy.
     * @param m the last move made
     */
    public void warnOnSpecialMoves( TwoPlayerMove m )
    {
        super.warnOnSpecialMoves(m);
        // we don't show dialogs if both players are computers.
        if (get2PlayerController().allPlayersComputer())
            return;

        int row, col;
        ChessBoard b = (ChessBoard)controller_.getBoard();
        boolean checked = false;
        for ( row = 1; row <= b.getNumRows(); row++ ) {
            for ( col = 1; col <= b.getNumCols(); col++ ) {
                BoardPosition pos = b.getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == m.player1 ) {
                    // @@ second arg is not technically correct. it should be last move, but I don't think it matters.
                    checked = b.isKingCheckedByPosition(pos, m);
                }
                if (checked) {
                    JOptionPane.showMessageDialog( this,
                        GameContext.getLabel("KING_IN_CHECK"), GameContext.getLabel("INFORMATION"), JOptionPane.INFORMATION_MESSAGE );
                    return;
                }
            }
        }
    }

    public void showLastMove()
    {
        refresh();
    }
}
