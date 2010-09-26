package com.becker.game.twoplayer.pente.ui;

import com.becker.common.Location;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.ViewerMouseListener;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.PenteBoard;
import com.becker.game.twoplayer.pente.PenteController;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
public class PenteViewerMouseListener extends ViewerMouseListener {

    /**
     * Constructor.
     */
    public PenteViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
    }


    @Override
    public void mousePressed( MouseEvent e )
    {
        PenteBoardViewer viewer = (PenteBoardViewer) viewer_;
        PenteController controller = (PenteController) viewer.getController();
        
        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        PenteBoard board = (PenteBoard) controller.getBoard();

        // if there is already a piece where the user clicked or its
        // out of bounds, then return without doing anything
        BoardPosition p = board.getPosition( loc);
        if ( (p == null) || !p.isUnoccupied() )
            return;

        TwoPlayerMove m =
            TwoPlayerMove.createMove( loc.getRow(), loc.getCol(), 0,
                                      new GamePiece(controller.isPlayer1sTurn()));

        viewer.continuePlay( m );
    }

}