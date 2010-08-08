package com.becker.game.twoplayer.checkers.ui;

import com.becker.common.Location;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.ViewerMouseListener;
import com.becker.game.twoplayer.checkers.CheckersController;
import com.becker.game.twoplayer.checkers.MoveGenerator;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
public class CheckersViewerMouseListener extends ViewerMouseListener {

    /**
     * Constructor.
     */
    public CheckersViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        CheckersController controller = (CheckersController)viewer_.getController();

        if (controller.isProcessing())
            return;
        Location loc = getRenderer().createLocation(e);

        Board board = controller.getBoard();
        BoardPosition position = board.getPosition( loc );
        // if there is no piece or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( controller.isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getRenderer().setDraggedPiece(position);
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        CheckersBoardViewer viewer = (CheckersBoardViewer)viewer_;
        CheckersController controller = (CheckersController)viewer.getController();

        // compute the coordinates of the position that we dropped the piece on.
        Location loc = getRenderer().createLocation(e);

        if ( getRenderer().getDraggedPiece() == null )
            return; // nothing being dragged

        Board board = controller.getBoard();
        // get the original position.
        BoardPosition position = board.getPosition( getRenderer().getDraggedPiece().getLocation());

        // valid or not, we won't show the dragged piece after releasing the mouse
        getRenderer().setDraggedPiece(null);

        BoardPosition destp = board.getPosition( loc );
        if (customCheckFails(position, destp)) {
            invalidMove();
            return;
        }

        List possibleMoveList = getPossibleMoveList(position);

        // verify that the move is valid before allowing it to be made
        Iterator it = possibleMoveList.iterator();
        boolean found = false;

        TwoPlayerMove m = null;
        while ( it.hasNext() && !found ) {
            m = (TwoPlayerMove) it.next();
            if ( (m.getToRow() == destp.getRow()) && (m.getToCol() == destp.getCol()) )
                found = true;
        }

        if ( !found ) {
            invalidMove();
            return; // it was not valid
        }

        if ( !viewer.continuePlay( m ) )    // then game over
            viewer.showWinnerDialog();
    }


    /**
     * if there is a piece at the destination already, or destination is out of bounds,
     * then return without doing anything.
     * @return true if custom check failed.
     */
    protected boolean customCheckFails(BoardPosition position, BoardPosition destp) {
        return  ( (position == null) || (destp == null) || (destp.isOccupied()) );
    }

    /**
     *   implements the MouseMotionListener interface
     */
    @Override
    public void mouseDragged( MouseEvent e ) {
        Location loc = getRenderer().createLocation(e);

        if ( getRenderer().getDraggedShowPiece() != null ) {
            getRenderer().getDraggedShowPiece().setLocation( loc );
        }
        viewer_.refresh();
    }

    /**
     * @param position place to consider possible moves from.
     * @return a list of all possible moves from the given position.
     */
    protected List getPossibleMoveList(BoardPosition position) {

        CheckersBoardViewer viewer = (CheckersBoardViewer)viewer_;
        CheckersController controller = (CheckersController)viewer.getController();

        MoveList possibleMoveList = new MoveList();

        TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();
        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();
        MoveGenerator generator =
                new MoveGenerator(controller, possibleMoveList,
                                  controller.getComputerWeights().getDefaultWeights(), player1);

        // it doesn't matter which set of wts are pass in here since we just need
        // a list of valid moves, so use default weights.
        generator.addMoves(position, lastMove);
        return possibleMoveList;
    }


    private void invalidMove() {
        JOptionPane.showMessageDialog(viewer_, GameContext.getLabel("ILLEGAL_MOVE"));
        viewer_.refresh();
    }

}