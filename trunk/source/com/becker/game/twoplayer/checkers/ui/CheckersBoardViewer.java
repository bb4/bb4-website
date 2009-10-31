package com.becker.game.twoplayer.checkers.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.checkers.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 *  This class takes a CheckersController as input and displays the
 *  Current state of the Checkers Game. The CheckersController contains a CheckersBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class CheckersBoardViewer extends AbstractTwoPlayerBoardViewer
                                 implements MouseMotionListener
{

    /**
     *  Construct the viewer
     */
    public CheckersBoardViewer()
    {
        addMouseMotionListener( this );
    }


    protected GameController createController()
    {
        return new CheckersController();
    }

    protected GameBoardRenderer getBoardRenderer() {
        return CheckersBoardRenderer.getRenderer();
    }


    /**
     * @param position
     * @return  a list of all possible moves from the given position.
     */
    protected List getPossibleMoveList(BoardPosition position)
    {
        List possibleMoveList = new LinkedList();

        // it doesn't matter which set of wts are pass in here since we just need
        // a list of moves so use default weights.
        ((CheckersController) controller_).addMoves( position, possibleMoveList,
                                                    (TwoPlayerMove)getBoard().getLastMove(),
                                                    get2PlayerController().getDefaultWeights() );
        return possibleMoveList;
    }

    public void mousePressed( MouseEvent e )
    {
        if (get2PlayerController().isProcessing())
            return;
        Location loc = getBoardRenderer().createLocation(e);

        Board board = controller_.getBoard();
        BoardPosition position = board.getPosition( loc );
        // if there is no piece or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getBoardRenderer().setDraggedPiece(position);
    }

    public void mouseReleased( MouseEvent e )
    {
        // compute the coords of the position that we dropped the piece on.
        Location loc = getBoardRenderer().createLocation(e);

        if ( getBoardRenderer().getDraggedPiece() == null )
            return; // nothing being dragged

        Board board = controller_.getBoard();
        // get the original position.
        BoardPosition position = board.getPosition( getBoardRenderer().getDraggedPiece().getLocation());

        // valid or not, we won't show the dragged piece after releasing the mouse
        getBoardRenderer().setDraggedPiece(null);

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

        if ( !continuePlay( m ) )    // then game over
            showWinnerDialog();
    }


    protected boolean customCheckFails(BoardPosition position, BoardPosition destp)
    {
        // if there is a piece at the destination already, or destination is out of bounds,
        // then return without doing anything
        return  ( (position == null) || (destp == null) || (!destp.isUnoccupied()) );
    }

    private void invalidMove()
    {
        JOptionPane.showMessageDialog(this, GameContext.getLabel("ILLEGAL_MOVE"));
        refresh();
    }

    /**
     *   implements the MouseMotionListener interface
     */
    public void mouseDragged( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);

        if ( getBoardRenderer().getDraggedShowPiece() != null ) {
            getBoardRenderer().getDraggedShowPiece().setLocation( loc );
        }
        refresh();
    }

    public void mouseMoved( MouseEvent e )
    {}

     /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        BoardPosition space = controller_.getBoard().getPosition( loc );
        if ( space != null && space.isOccupied() && GameContext.getDebugMode() > 0 ) {
            sb.append( loc );
            sb.append("<br>");
            sb.append(space.toString());
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
