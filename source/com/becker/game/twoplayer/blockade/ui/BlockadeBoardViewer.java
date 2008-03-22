package com.becker.game.twoplayer.blockade.ui;

import com.becker.common.*;
import com.becker.game.common.*;
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
    
    /** wall that gets dragged around until the player places it.   */
    private BlockadeWall draggedWall_;
    private BlockadeMove currentMove_ = null;
    // becomes true if the player has placed his pawn on an opponent base.
    private boolean hasWon_ = false;
    
    private static final short DRAG_TRANSPARENCY = 170;

    /**
     * Construct the viewer.
     */
    public BlockadeBoardViewer()
    {
        pieceRenderer_ =  BlockadePieceRenderer.getRenderer();
        wallPlacingMode_ = false;
        addMouseMotionListener( this );
    }

    protected GameController createController()
    {
        return new BlockadeController();
    }

    protected int getDefaultCellSize()
    {
        return 24;
    }


    public void mousePressed( MouseEvent e )
    {

        if (get2PlayerController().isProcessing() || wallPlacingMode_)
            return;

        Board board = controller_.getBoard();
        Location loc = createLocation(e, getCellSize());
        BoardPosition position = board.getPosition( loc );
        
        // if there is no piece, or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        draggedPiece_ = position;
        draggedShowPiece_ = position.copy();
        assert (draggedShowPiece_.getPiece() != null);
        draggedShowPiece_.getPiece().setTransparency( DRAG_TRANSPARENCY );
    }

    /**
     * When the mouse is released either the piece or a wall is being placed
     * depending on the value of wallPlacingMode_.
     * @param e
     */
    public void mouseReleased( MouseEvent e )
    {
        // compute the coords of the position that we dropped the piece on.
        Location loc = createLocation(e, getCellSize());

        if (!wallPlacingMode_)  {
            boolean placed = placePiece( loc );
            if (!placed) {
                draggedPiece_ = null;
                draggedShowPiece_ = null;
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
        if ( draggedPiece_ == null )   {
            return false; // nothing being dragged
        }

        Board board = controller_.getBoard();
        // get the original position.
        BlockadeBoardPosition position =  (BlockadeBoardPosition)board.getPosition( draggedPiece_.getLocation() );

        // valid or not, we won't show the dragged piece after releasing the mouse.
        draggedPiece_ = null;
        draggedShowPiece_ = null;

        BoardPosition destpos = board.getPosition( loc );
        if (customCheckFails(position, destpos)) {
            JOptionPane.showMessageDialog( this, GameContext.getLabel("ILLEGAL_MOVE"));
            return false;
        }

        List possibleMoveList = ((BlockadeController)controller_).getPossibleMoveList(position);

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
        if (draggedWall_ == null)
            return false;
        
        // first check to see if its a legal placement
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        // check wall intersection and overlaps.
        //assert(draggedWall_ != null);
        String sError = board.checkLegalWallPlacement(draggedWall_, loc, m.getPiece());

        if (sError != null) {
            JOptionPane.showMessageDialog( this, sError);
            draggedWall_ = null;
            return false;
        }
        else {
            // wall placed successfully.
            wallPlacingMode_ = false;
            m.setWall(draggedWall_);
            draggedWall_ = null;
            return true;
        }
    }

    /**
     * implements the MouseMotionListener interface.
     */
    public void mouseDragged( MouseEvent e )
    {
        Location loc = createLocation(e, getCellSize());

        if ( draggedShowPiece_ != null ) {
            draggedShowPiece_.setLocation( loc );
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
            Location loc = createLocation(e, getCellSize());
            if (board.getPosition(loc)==null)
                return;  // out of bounds
            int index = getWallIndexForPosition(e.getX(), e.getY(), loc, board);

            Set positions = new HashSet();

            boolean isVertical = false;

            switch (index) {
                case 0 :
                    isVertical = true;
                    positions.add(board.getPosition(loc));
                    positions.add(board.getPosition(loc.getRow()+1, loc.getCol()));
                    break;
                case 1 :
                    isVertical = true;
                    assert (board.getPosition(loc)!=null);
                    assert (board.getPosition(loc.getRow()-1, loc.getCol())!=null);
                    positions.add(board.getPosition(loc));
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()));
                    break;
                case 2 :
                    isVertical = false;
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()));
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()+1));
                    break;
                case 3 :
                    isVertical = false;
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()));
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()-1));
                    break;
                case 4 :
                    isVertical = true;
                    positions.add(board.getPosition(loc.getRow(), loc.getCol()-1));
                    positions.add(board.getPosition(loc.getRow()-1, loc.getCol()-1));
                    break;
                case 5 :
                    isVertical = true;
                    positions.add(board.getPosition(loc.getRow(), loc.getCol()-1));
                    positions.add(board.getPosition(loc.getRow()+1, loc.getCol()-1));
                    break;
                case 6 :
                    isVertical = false;
                    positions.add(board.getPosition(loc));
                    positions.add(board.getPosition(loc.getRow(), loc.getCol()-1));
                    break;
                case 7 :
                    isVertical = false;
                    positions.add(board.getPosition(loc));
                    positions.add(board.getPosition(loc.getRow(), loc.getCol()+1));
                    break;
                default : assert false:("bad index="+index);
            }

            draggedWall_ = new BlockadeWall(isVertical, positions);

            repaint();
        }
    }


    /**
     * returns an index corresponding to a triangular cell segment according to:
     * <pre>
     * \  3 | 2 /
     * 4 \  | /  1
     * -------------
     * 5 /  | \  0
     * /  6 | 7 \
     * </pre>
     *@@ We could make this an enum with values NEE, NNE, NNW, NWW, SWW, etc
     * @param xp
     * @param yp
     * @return
     */
    private int getWallIndexForPosition(int xp, int yp, Location loc, BlockadeBoard b)
    {
        int numRows = b.getNumRows();
        int numCols = b.getNumCols();
        float x = (float)xp/cellSize_ - (int)(xp / cellSize_);
        float y = (float)yp/cellSize_ - (int)(yp / cellSize_);

        if (loc.getCol() >= numCols)
           x = Math.min(0.499f, x);
        if (loc.getCol() <= 1)
           x = Math.max(0.501f, x);
        if (loc.getRow() >= numRows)
           y = Math.min(0.499f, y);
        if (loc.getRow() <=1 )
           y = Math.max(0.501f, y);

        if (x <= 0.5f) {
            if (y <= 0.5f) {    // upper left
                return (y > x)? 4 : 3;
            }
            else {  // y > .5  // lower left
                return ((y - 0.5f) > (0.5f - x))?  6 : 5;
            }
        }
        else { // x>.5
            if (y <= 0.5f) {    // upper right
                return (y < (1.0f-x))? 2 : 1;
            }
            else {  // y > .5  // lower right
                return (y < x)? 0 : 7;
            }
        }
    }

    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        int cellSize = this.getCellSize();

        boolean drewWall = false;
        for ( int i = 1; i <= nrows; i++ )  {
            for ( int j = 1; j <= ncols; j++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( i, j );
                drewWall = drewWall || BlockadePieceRenderer.renderWallAtPosition( g2,  pos, cellSize );
                if (pos.isOccupied() && GameContext.getDebugMode() > 0)
                   PathRenderer.drawShortestPaths(g2, pos, board, cellSize_);
            }
        }

        for ( int i = 1; i <= nrows; i++ )  {
            for ( int j = 1; j <= ncols; j++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( i, j );
                pieceRenderer_.render(g2, pos,  cellSize_, board);
            }
        }

        // if there is a wall being dragged, draw it
        if ( draggedWall_ != null ) {
            // first remember the walls currently there (if any) so they can be restored.
            Set<BlockadeBoardPosition> hsPositions = draggedWall_.getPositions();
            Iterator it = hsPositions.iterator();
            while (it.hasNext())  {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)it.next();
                boolean vertical = draggedWall_.isVertical();
                BlockadeWall cWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

                // temporarily set the dragged wall long enough to render it.
                if (vertical)
                    pos.setEastWall(draggedWall_);
                else
                    pos.setSouthWall(draggedWall_);

                BlockadePieceRenderer.renderWallAtPosition1( g2,  pos, cellSize );

                // restore the actual wall
                if (vertical)
                    pos.setEastWall(cWall);
                else
                    pos.setSouthWall(cWall);
            }
        }
    }

    protected void drawBackground( Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        super.drawBackground(g, startPos, rightEdgePos, bottomEdgePos);

        drawHomeBases(g, true);
        drawHomeBases(g, false);
    }

    private void drawHomeBases(Graphics g, boolean player1)
    {
        // draw the homebases
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        BoardPosition[] homes = player1? board.getPlayer1Homes() : board.getPlayer2Homes();

        int cellSize = this.getCellSize();
        int offset = Math.round((float)cellSize / 4.0f);
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer) pieceRenderer_;
        g.setColor(player1? renderer.getPlayer1Color(): renderer.getPlayer2Color());

        for (BoardPosition home : homes) {
            g.drawOval(BOARD_MARGIN + (home.getCol() - 1) * cellSize + offset,
                       BOARD_MARGIN + (home.getRow() - 1) * cellSize + offset,
                       2 * offset, 2 * offset);
        }
    }
  

    /**
     * @return the tooltip for the panel given a mouse event.
     */
    public String getToolTipText( MouseEvent e )
    {
        Location loc = createLocation(e, getCellSize());
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
