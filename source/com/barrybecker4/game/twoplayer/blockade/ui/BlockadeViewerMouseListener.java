/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeWall;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Mouse handling for blockade game.
 *
 *  @author Barry Becker
 */
public class BlockadeViewerMouseListener extends ViewerMouseListener {

    private BlockadeMove currentMove_ = null;

    /** becomes true if the player has placed his pawn on an opponent base. */
    private boolean hasWon_ = false;

    /** this becomes true when the player needs to place a wall instead of a piece during his turn.  */
    private boolean wallPlacingMode_;


    /**
     * Constructor.
     */
    public BlockadeViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
        wallPlacingMode_ = false;
    }


    @Override
    public void mousePressed( MouseEvent e )
    {
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) viewer_;
        if (viewer.get2PlayerController().isProcessing() || wallPlacingMode_)
            return;

        Board board = viewer.getBoard();
        Location loc = getRenderer().createLocation(e);
        BoardPosition position = board.getPosition( loc );

        // if there is no piece, or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( viewer.get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getRenderer().setDraggedPiece(position);
    }

    /**
     * When the mouse is released either the piece or a wall is being placed
     * depending on the value of wallPlacingMode_.
     * @param e
     */
    @Override
    public void mouseReleased( MouseEvent e )
    {
        // compute the coords of the position that we dropped the piece on.
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) viewer_;
        Location loc = getRenderer().createLocation(e);

        if (!wallPlacingMode_)  {
            boolean placed = placePiece( loc );
            if (!placed) {
                getRenderer().setDraggedPiece(null);
            }
            if (hasWon_) {
                viewer.continuePlay( currentMove_ );
            }
            return;
        }

        boolean wallPlaced = placeWall(loc, currentMove_);
        if (!wallPlaced)
            return;

        viewer.continuePlay( currentMove_ );
    }


    /**
     * implements the MouseMotionListener interface.
     */
    @Override
    public void mouseDragged( MouseEvent e )
    {
        GameBoardRenderer renderer = getRenderer();
        Location loc = renderer.createLocation(e);

        if ( renderer.getDraggedShowPiece() != null ) {
            renderer.getDraggedShowPiece().setLocation( loc );
        }
        viewer_.repaint();
    }

    /**
     * if we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     * @param e
     */
    @Override
    public void mouseMoved( MouseEvent e )
    {
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) viewer_;
        if (wallPlacingMode_)  {

            // show the hovering wall
            BlockadeBoard board = (BlockadeBoard)viewer.getBoard();
            // now show it in the new location
            Location loc = getRenderer().createLocation(e);
            if (board.getPosition(loc)==null) {
                return;  // out of bounds
            }
            int index =
                ((BlockadeBoardRenderer)getRenderer()).getWallIndexForPosition(e.getX(), e.getY(), loc, board);

            BoardPosition pos1 = null,  pos2 = null;

            switch (index) {
                case 0 :
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow()+1, loc.getCol());
                    break;
                case 1 :
                    assert (board.getPosition(loc) != null);
                    assert (board.getPosition(loc.getRow()-1, loc.getCol())!=null);
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol());
                    break;
                case 2 :
                    pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()+1);
                    break;
                case 3 :
                    pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                    break;
                case 4 :
                    pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                    break;
                case 5 :
                    pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    pos2 = board.getPosition(loc.getRow()+1, loc.getCol()-1);
                    break;
                case 6 :
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    break;
                case 7 :
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow(), loc.getCol()+1);
                    break;
                default : assert false:("bad index="+index);
            }

            ((BlockadeBoardRenderer)getRenderer()).setDraggedWall(
                    new BlockadeWall((BlockadeBoardPosition)pos1, (BlockadeBoardPosition)pos2));

            viewer.repaint();
        }
    }


    /**
     * Place the piece after dropping it two spaces from where it was.
     * @param loc location where the piece was placed.
     * @return true if a piece is successfully moved.
     */
    private boolean placePiece(Location loc) {
        if ( getRenderer().getDraggedPiece() == null )   {
            return false; // nothing being dragged
        }

        BlockadeController controller = (BlockadeController)viewer_.getController();
        BlockadeBoard board = (BlockadeBoard) controller.getBoard();
        // get the original position.
        BlockadeBoardPosition position =
                (BlockadeBoardPosition)board.getPosition(getRenderer().getDraggedPiece().getLocation());

        // valid or not, don't show the dragged piece after releasing the mouse.
        getRenderer().setDraggedPiece(null);

        BlockadeMove m = checkAndGetValidMove(position, loc);
        if (m == null) {
            return false;
        }

        // make sure that the piece shows while we decide where to place the wall.
        currentMove_ = m;
        GameContext.log(1, "legal human move :"+m.toString());
        position.getPiece().setTransparency((short)0);
        boolean isPlayer1 = position.getPiece().isOwnedByPlayer1();
        BlockadeBoardPosition newPosition =
                (BlockadeBoardPosition) board.getPosition(currentMove_.getToRow(), currentMove_.getToCol());
        newPosition.setPiece(position.getPiece());
        position.setPiece(null);
        viewer_.refresh();

        if (newPosition.isHomeBase( !isPlayer1 )) {
            hasWon_ = true;
            assert(isPlayer1 == (controller.getCurrentPlayer() == controller.getPlayers().getFirstPlayer()));
            controller.getCurrentPlayer().setWon(true);
        }
        else {
            // piece moved! now a wall needs to be placed.
            wallPlacingMode_ = true;
        }
        return true;
    }

    /**
     * @param origPosition place dragged from
     * @param placedLocation location dragged to.
     * @return the valid move else and error is shown and null is returned.
     */
    private BlockadeMove checkAndGetValidMove(BlockadeBoardPosition origPosition, Location placedLocation) {
        BlockadeController controller = (BlockadeController)viewer_.getController();
        BlockadeBoard board = (BlockadeBoard) controller.getBoard();
        List<BlockadeMove> possibleMoveList = controller.getPossibleMoveList(origPosition);

        BlockadeBoardPosition destpos = (BlockadeBoardPosition) board.getPosition( placedLocation );
        if (customCheckFails(destpos)) {
            JOptionPane.showMessageDialog( viewer_, GameContext.getLabel("ILLEGAL_MOVE"));
            return null;
        }
        // verify that the move is valid before allowing it to be made.
        Iterator it = possibleMoveList.iterator();
        boolean found = false;

        BlockadeMove m = null;
        while ( it.hasNext() && !found ) {
            m = (BlockadeMove) it.next();
            if ( (m.getToRow() == destpos.getRow()) && (m.getToCol() == destpos.getCol()) )
                found = true;
        }

        if ( !found ) {
            return null; // it was not valid
        }
        return m;
    }


    /**
     * If there is a piece at the destination already, or destination is out of bounds,
     * then return without doing anything.
     * @param destpos position to move to.
     * @return true if this is not a valid move.
     */
    private boolean customCheckFails(BlockadeBoardPosition destpos) {
        return  ( (destpos == null) || (destpos.isOccupied() && !destpos.isHomeBase()) );
    }

    /**
     * Place the wall if it is a legal placement.
     * We need to give a warning message if they try to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     *
     * @param loc location where the wall was placed.
     * @return true if a wall is successfully placed.
     */
    private boolean placeWall(Location loc, BlockadeMove m) {
        BlockadeBoardRenderer bbRenderer = (BlockadeBoardRenderer) getRenderer();
        BlockadeWall draggedWall = bbRenderer.getDraggedWall();
        if (draggedWall == null)
            return false;

        // first check to see if its a legal placement
        BlockadeBoard board = (BlockadeBoard)viewer_.getBoard();

        // check wall intersection and overlaps.
        //assert(draggedWall_ != null);
        String sError = board.checkLegalWallPlacement(draggedWall, loc);

        if (sError != null) {
            JOptionPane.showMessageDialog( viewer_, sError);
            bbRenderer.setDraggedWall(null);
            return false;
        }
        else {
            // wall placed successfully.
            wallPlacingMode_ = false;
            m.setWall(draggedWall);
            bbRenderer.setDraggedWall(null);
            return true;
        }
    }
}