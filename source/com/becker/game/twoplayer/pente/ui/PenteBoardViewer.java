package com.becker.game.twoplayer.pente.ui;

import ca.dj.jigo.sgf.tokens.MoveToken;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.Move;
import com.becker.game.common.ui.GamePieceRenderer;
import com.becker.game.twoplayer.pente.PenteBoard;
import com.becker.game.twoplayer.pente.PenteController;

import java.awt.event.MouseEvent;

/**
 *  Takes a PenteController as input and displays the
 *  current state of the Pente Game. The PenteController contains a PenteBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class PenteBoardViewer extends TwoPlayerBoardViewer
{

    //Construct the application
    public PenteBoardViewer()
    {
        pieceRenderer_ = GamePieceRenderer.getRenderer();
    }

    protected GameController createController()
    {
        return new PenteController();
    }

    protected int getDefaultCellSize()
    {
        return 16;
    }

    /**
     * This will create a move from an SGF token
     */
    protected Move createMoveFromToken( MoveToken token, int moveNum )
    {
        GameContext.log(0,  "not implemented yet" );
        return null;
    }

    public void mousePressed( MouseEvent e )
    {
        if (get2PlayerController().isProcessing())   {
            return;
        }
        Location loc = createLocation(e, getCellSize());


        PenteBoard board = (PenteBoard) controller_.getBoard();

        // if there is already a piece where the user clicked or its
        // out of bounds, then return without doing anything
        BoardPosition p = board.getPosition( loc);
        if ( (p == null) || !p.isUnoccupied() )
            return;

        TwoPlayerMove m =
            TwoPlayerMove.createMove( loc.row, loc.col, 0, controller_.getNumMoves() + 1,
                             new GamePiece(get2PlayerController().isPlayer1sTurn()));

        if ( !continuePlay( m ) )    // then game over
            showWinnerDialog();
    }
}
