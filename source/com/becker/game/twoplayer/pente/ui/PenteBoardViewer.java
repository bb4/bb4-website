package com.becker.game.twoplayer.pente.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.ui.*;
import com.becker.game.twoplayer.pente.*;

import java.awt.event.*;

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
        pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer();
    }

    protected GameController createController()
    {
        return new PenteController();
    }

    protected int getDefaultCellSize()
    {
        return 16;
    }


    public void mousePressed( MouseEvent e )
    {
        if (get2PlayerController().isProcessing() || get2PlayerController().isDone())   {
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
            TwoPlayerMove.createMove( loc.getRow(), loc.getCol(), 0,
                                      new GamePiece(get2PlayerController().isPlayer1sTurn()));

        continuePlay( m );
    }
}
