package com.becker.game.twoplayer.blockade.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.blockade.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 *  This class takes a BlockadeController as input and displays the
 *  Current state of the Blockade Game. The BlockadeController contains a BlockadeBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class BlockadeBoardViewer extends TwoPlayerBoardViewer implements MouseMotionListener
{
    /** this becomes true when the player needs to place a wall instead of a piece during his turn.  */
    private boolean wallPlacingMode_ = false;

    private BlockadeMove currentMove_ = null;
    // becomes true if the player has placed his pawn on an opponent base.
    private boolean hasWon_ = false;


    /**
     * Construct the viewer.
     */
    public BlockadeBoardViewer()
    {
        wallPlacingMode_ = false;
        addMouseMotionListener( this );
    }

    protected GameController createController()
    {
        return new BlockadeController();
    }

    protected GameBoardRenderer getBoardRenderer() {
        return BlockadeBoardRenderer.getRenderer();
    }


    @Override
    public void mousePressed( MouseEvent e )
    {

        if (get2PlayerController().isProcessing() || wallPlacingMode_)
            return;

        Board board = controller_.getBoard();
        Location loc = getBoardRenderer().createLocation(e);
        BoardPosition position = board.getPosition( loc );

        // if there is no piece, or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getBoardRenderer().setDraggedPiece(position);
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
        Location loc = getBoardRenderer().createLocation(e);

        if (!wallPlacingMode_)  {
            boolean placed = placePiece( loc );
            if (!placed) {
                getBoardRenderer().setDraggedPiece(null);
            }
            if (hasWon_) {
                continuePlay( currentMove_ );
            }
            return;
        }

        boolean wallPlaced = placeWall(loc, currentMove_);
        if (!wallPlaced)
            return;

        continuePlay( currentMove_ );
    }

    /**
     *
     * @param loc location where the piece was placed.
     * @return true if a piece is successfully moved.
     */
    private boolean placePiece(Location loc)
    {
        if ( getBoardRenderer().getDraggedPiece() == null )   {
            return false; // nothing being dragged
        }

        BlockadeBoard board = (BlockadeBoard) controller_.getBoard();
        // get the original position.
        BlockadeBoardPosition position =
                (BlockadeBoardPosition)board.getPosition( getBoardRenderer().getDraggedPiece().getLocation());

        // valid or not, we won't show the dragged piece after releasing the mouse.
        getBoardRenderer().setDraggedPiece(null);

        BoardPosition destpos = board.getPosition( loc );
        if (customCheckFails(position, destpos)) {
            JOptionPane.showMessageDialog( this, GameContext.getLabel("ILLEGAL_MOVE"));
            return false;
        }

        List<BlockadeMove> possibleMoveList = ((BlockadeController)controller_).getPossibleMoveList(position);

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
            return false; // it was not valid
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
        refresh();

        if (newPosition.isHomeBase( !isPlayer1 )) {
            hasWon_ = true;
            assert(isPlayer1 == (controller_.getCurrentPlayer() == controller_.getFirstPlayer()));
            controller_.getCurrentPlayer().setWon(true);
        }
        else {
            // piece moved! now a wall needs to be placed.
            wallPlacingMode_ = true;
        }
        return true;
    }


    /**
     * @param position orig position.
     * @param destp position to move to.
     * @return true if this is not a valie move.
     */
    private static boolean customCheckFails(BoardPosition position, BoardPosition destp)
    {
        // if there is a piece at the destination already, or destination is out of bounds,
        // then return without doing anything
        BlockadeBoardPosition destpos = (BlockadeBoardPosition)destp;
        if (destpos.isOccupied())
            GameContext.log(0, destpos + "is occupied by "+destp.getPiece() );
        return  ( (position == null) || (destpos.isOccupied() && !destpos.isHomeBase()) );
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
    private boolean placeWall(Location loc, BlockadeMove m)
    {
        BlockadeBoardRenderer bbRenderer = (BlockadeBoardRenderer) getBoardRenderer();
        BlockadeWall draggedWall = bbRenderer.getDraggedWall();
        if (draggedWall == null)
            return false;

        // first check to see if its a legal placement
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        // check wall intersection and overlaps.
        //assert(draggedWall_ != null);
        String sError = board.checkLegalWallPlacement(draggedWall, loc, m.getPiece());

        if (sError != null) {
            JOptionPane.showMessageDialog( this, sError);
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

    /**
     * implements the MouseMotionListener interface.
     */
    public void mouseDragged( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);

        if ( getBoardRenderer().getDraggedShowPiece() != null ) {
            getBoardRenderer().getDraggedShowPiece().setLocation( loc );
        }
        repaint();
    }

    /**
     * if we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     * @param e
     */
    public void mouseMoved( MouseEvent e )
    {
        if (wallPlacingMode_)  {

            // show the hovering wall
            BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
            // now show it in the new location
            Location loc = getBoardRenderer().createLocation(e);
            if (board.getPosition(loc)==null) {
                return;  // out of bounds
            }
            int index = ((BlockadeBoardRenderer)getBoardRenderer()).getWallIndexForPosition(e.getX(), e.getY(), loc, board);

            Set<BlockadeBoardPosition> positions = new LinkedHashSet<BlockadeBoardPosition>();

            boolean isVertical = false;
            BoardPosition pos1 = null,  pos2 = null;

            switch (index) {
                case 0 :
                    isVertical = true;
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow()+1, loc.getCol());
                    break;
                case 1 :
                    isVertical = true;
                    assert (board.getPosition(loc)!=null);
                    assert (board.getPosition(loc.getRow()-1, loc.getCol())!=null);
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol());
                    break;
                case 2 :
                    isVertical = false;
                    pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()+1);
                    break;
                case 3 :
                    isVertical = false;
                    pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                    break;
                case 4 :
                    isVertical = true;
                    pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                    break;
                case 5 :
                    isVertical = true;
                    pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    pos2 = board.getPosition(loc.getRow()+1, loc.getCol()-1);
                    break;
                case 6 :
                    isVertical = false;
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow(), loc.getCol()-1);
                    break;
                case 7 :
                    isVertical = false;
                    pos1 = board.getPosition(loc);
                    pos2 = board.getPosition(loc.getRow(), loc.getCol()+1);
                    break;
                default : assert false:("bad index="+index);
            }

            positions.add((BlockadeBoardPosition)pos1);
            positions.add((BlockadeBoardPosition)pos2);
            ((BlockadeBoardRenderer)getBoardRenderer()).setDraggedWall(
                    new BlockadeWall((BlockadeBoardPosition)pos1, (BlockadeBoardPosition)pos2));

            repaint();
        }
    }


    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        BlockadeBoardPosition space = (BlockadeBoardPosition)controller_.getBoard().getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            sb.append(space.toString());
            sb.append(space.isVisited()?":Visited":"");
            sb.append((space.isHomeBase()?(space.isHomeBase(true)?" P1 Home":"p2 Home"):""));
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
