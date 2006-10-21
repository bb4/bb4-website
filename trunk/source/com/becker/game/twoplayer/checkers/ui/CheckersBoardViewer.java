package com.becker.game.twoplayer.checkers.ui;

import ca.dj.jigo.sgf.tokens.MoveToken;
import com.becker.game.twoplayer.checkers.CheckersController;
import com.becker.game.twoplayer.checkers.CheckersMove;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.Move;
import com.becker.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

/**
 *  This class takes a CheckersController as input and displays the
 *  Current state of the Checkers Game. The CheckersController contains a CheckersBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class CheckersBoardViewer extends TwoPlayerBoardViewer implements MouseMotionListener
{

    // colors of the squares on the chess board.
    // make them transparend so the background color shows through.
    protected static final Color BLACK_SQUARE_COLOR = new Color(2, 2, 2, 80);
    protected static final Color RED_SQUARE_COLOR = new Color(250, 0, 0, 80);

    /**
     *  Construct the viewer
     */
    public CheckersBoardViewer()
    {
        pieceRenderer_ = CheckersPieceRenderer.getRenderer();
        addMouseMotionListener( this );
    }


    protected GameController createController()
    {
        return new CheckersController();
    }


    protected int getDefaultCellSize()
    {
        return 34;
    }

    /**
     *  animate the last move so the player does not lose orientation.
     */
    public void showLastMove()
    {
        CheckersMove m = (CheckersMove)getBoard().getLastMove();
        // if we have captures, then we want to show each one
        if (m.captureList != null) {
            controller_.undoLastMove();
            BoardPosition origPos = getBoard().getPosition(m.getFromRow(), m.getFromCol());
            draggedShowPiece_ = origPos.copy();
            origPos.setPiece(null);
            Iterator it = m.captureList.iterator();
            while (it.hasNext()) {
                BoardPosition capPos = (BoardPosition)it.next();
                int rOrig =  draggedShowPiece_.getRow();
                int cOrig =  draggedShowPiece_.getCol();
                int rdir = capPos.getRow() - rOrig;
                int cdir = capPos.getCol() - cOrig;
                draggedShowPiece_.setRow(rOrig + 2*rdir);
                draggedShowPiece_.setCol(cOrig + 2*cdir);
                getBoard().getPosition(capPos.getLocation()).setPiece(null);
                refresh();
                //JOptionPane.showMessageDialog(this, "cap1");
            }
            draggedShowPiece_ = null;
            controller_.makeMove(m);
        }

        // this will paint the component immediately
        refresh();
    }


    /**
     * @return a move created from an SGF token
     */
    protected Move createMoveFromToken( MoveToken token, int moveNum )
    {
        GameContext.log(0,  "not implemented yet" );
        return null;
    }

    protected void drawBackground( Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        super.drawBackground(g, startPos, rightEdgePos, bottomEdgePos);

        Board board = controller_.getBoard();
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();

        for (int i=0; i<nrows; i++) {
            for (int j=0; j<ncols; j++)  {
                g.setColor(((i+j)%2 == 0)? BLACK_SQUARE_COLOR : RED_SQUARE_COLOR);
                int ioff = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize_ * i;
                int joff = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize_ * j;
                g.fillRect( ioff, joff, cellSize_, cellSize_ );
            }
        }
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
        Location loc = createLocation(e, getCellSize());

        Board board = controller_.getBoard();
        BoardPosition position = board.getPosition( loc );
        // if there is no piece or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        draggedPiece_ = position;
        draggedShowPiece_ = position.copy();
        draggedShowPiece_.getPiece().setTransparency( (short) 160 );
    }

    public void mouseReleased( MouseEvent e )
    {
        // compute the coords of the position that we dropped the piece on.
        Location loc = createLocation(e, getCellSize());

        if ( draggedPiece_ == null )
            return; // nothing being dragged

        Board board = controller_.getBoard();
        // get the original position.
        BoardPosition position =  board.getPosition( draggedPiece_.getLocation() );


        // valid or not, we won't show the dragged piece after releasing the mouse
        draggedPiece_ = null;
        draggedShowPiece_ = null;

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
        Location loc = createLocation(e, getCellSize());

        if ( draggedShowPiece_ != null ) {
            draggedShowPiece_.setLocation( loc );
        }
        refresh();
    }

    public void mouseMoved( MouseEvent e )
    {}

     /**
     * @return the tooltip for the panel given a mouse event
     */
    public String getToolTipText( MouseEvent e )
    {
        Location loc = createLocation(e, getCellSize());
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
