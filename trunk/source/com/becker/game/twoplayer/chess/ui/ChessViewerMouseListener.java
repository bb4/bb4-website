package com.becker.game.twoplayer.chess.ui;

import com.becker.common.Location;
import com.becker.game.common.Board;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.GamePiece;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.ViewerMouseListener;
import com.becker.game.twoplayer.checkers.CheckersController;
import com.becker.game.twoplayer.checkers.ui.CheckersBoardViewer;
import com.becker.game.twoplayer.checkers.ui.CheckersViewerMouseListener;
import com.becker.game.twoplayer.chess.ChessBoard;
import com.becker.game.twoplayer.chess.ChessController;
import com.becker.game.twoplayer.chess.ChessPiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  Mouse handling for chess game.
 *
 *  @author Barry Becker
 */
public class ChessViewerMouseListener extends CheckersViewerMouseListener {

    /**
     * Constructor.
     */
    public ChessViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
    }

    
    @Override
    protected List getPossibleMoveList(BoardPosition position)
    {
        ChessBoard board = (ChessBoard)viewer_.getBoard();
        ChessController controller = (ChessController)viewer_.getController();

        ChessPiece piece = (ChessPiece)position.getPiece();
        List possibleMoveList =
            piece.findPossibleMoves(board, position.getRow(), position.getCol(),
                                    controller.getLastMove());
        controller.removeSelfCheckingMoves(possibleMoveList);
        return possibleMoveList;
    }



    @Override
    protected boolean customCheckFails(BoardPosition position, BoardPosition destp) {
       // intentionally do nothing.
       return false;
    }
}