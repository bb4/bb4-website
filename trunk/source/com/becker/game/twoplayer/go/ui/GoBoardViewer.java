package com.becker.game.twoplayer.go.ui;

import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import com.becker.game.twoplayer.go.board.elements.GoString;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.common.*;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.becker.game.twoplayer.go.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game. The GoController contains a GoBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
final class GoBoardViewer extends AbstractTwoPlayerBoardViewer
                          implements MouseMotionListener
{

    private static final String STONES_CAPTURED = GameContext.getLabel("CAPTURES_EQUALS");
    private static final String TERRITORY = GameContext.getLabel("TERRITORY_EQUALS");
    private static final String SCORE = GameContext.getLabel("SCORE_EQUALS");

    /** Still remember the dragged show piece when the players mouse goes off the board. */
    private BoardPosition savedShowPiece_;

    /**
     * Construct the viewer given the controller.
     */
    GoBoardViewer()
    {
        addMouseMotionListener( this );
    }

    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame()
    {
        super.startNewGame();
        getBoardRenderer().setDraggedShowPiece(null);

        if (!controller_.allPlayersComputer()) {
            getBoardRenderer().setDraggedShowPiece(
                    new GoBoardPosition(0, 0, null, new GoStone(get2PlayerController().isPlayer1sTurn())));
            savedShowPiece_ = getBoardRenderer().getDraggedShowPiece();
        }
    }

    @Override
    protected GameController createController()
    {
        return new GoController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return GoBoardRenderer.getRenderer();
    }


    /**
     * perform a pass for the current player.
     */
    public void pass()
    {
        GameContext.log( 1, "passing" );
        GoMove m = GoMove.createPassMove( 0, get2PlayerController().isPlayer1sTurn() );
        continuePlay( m );
    }


    /**
     *  mouseClicked requires both the mouse down and mouse up event to occur at the same location.
     *  classes derived from TwoPlayerBoardViewer must call mousePressed first.
     */
    @Override
    public void mousePressed( MouseEvent e )
    {
        // all derived classes must check this to disable user clicks while the computer is thinking
        if (get2PlayerController().isProcessing()) {
            return;
        }
        Location loc = getBoardRenderer().createLocation(e);
        GoBoard board = (GoBoard) controller_.getBoard();
        GoController controller = (GoController) controller_;

        boolean player1sTurn = controller.isPlayer1sTurn();
        GameContext.log( 3, "GoBoardViewer: mousePressed: player1sTurn()=" + player1sTurn);

        GoMove m = GoMove.createGoMove( loc.getRow(), loc.getCol(), 0, new GoStone(player1sTurn));

        // if there is already a piece where the user clicked, or its
        // out of bounds, or its a suicide move, then return without doing anything
        GoBoardPosition stone = (GoBoardPosition) board.getPosition( loc );
        if ( stone == null ) {
            return;      // user clicked out of bounds
        }

        if ( stone.isOccupied() ) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("CANT_PLAY_ON_STONE") );
            GameContext.log( 0, "GoBoardViewer: There is already a stone there: " + stone );
            return;
        }
        if ( GoController.isTakeBack( m.getToRow(), m.getToCol(), (GoMove) getBoard().getLastMove(), board ) ) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("NO_TAKEBACKS"));
            return;
        }
        assert(!stone.isVisited());

        if (m.isSuicidal(board)) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("SUICIDAL") );
            GameContext.log( 1, "GoBoardViewer: That move is suicidal (and hence illegal): " + stone );
            return;
        }

        if ( !continuePlay( m ) ) {   // then game over
            getBoardRenderer().setDraggedShowPiece(null);
            showWinnerDialog();
        } else if (controller_.allPlayersHuman()) {
            // create a stone to show for the next players move
            getBoardRenderer().setDraggedShowPiece(
                    new GoBoardPosition(loc.getRow(), loc.getCol(), null, new GoStone(!player1sTurn)));
        }
    }

     /**
     * if we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     */
    public void mouseMoved( MouseEvent e )
    {
        if (get2PlayerController().isProcessing()) {
            return;
        }
        Location loc = getBoardRenderer().createLocation(e);

        if ( getBoardRenderer().getDraggedShowPiece() != null ) {
            getBoardRenderer().getDraggedShowPiece().setLocation( loc );
        }
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseEntered( MouseEvent e ) {
        getBoardRenderer().setDraggedShowPiece(savedShowPiece_);
    }

    @Override
    public void mouseExited( MouseEvent e ) {
        getBoardRenderer().setDraggedShowPiece(null);
        //repaint();
    }

    /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    @Override
    protected void showWinnerDialog()
    {
         super.showWinnerDialog();
         controller_.clearGameOver();
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage()
    {
        String message = "\n";
        GoController gc = (GoController)controller_;

        // show the dead stones marked as such.
        this.paint( this.getGraphics() );

        int blackCaptures = gc.getNumCaptures(true);
        int whiteCaptures = gc.getNumCaptures(false);

        String p1Name = gc.getPlayer1().getName();
        String p2Name = gc.getPlayer2().getName();

        message += p1Name +' '+ STONES_CAPTURED + blackCaptures +'\n';
        message += p2Name +' '+ STONES_CAPTURED + whiteCaptures +"\n\n";

        int blackTerritory = gc.getTerritory(true);
        int whiteTerritory = gc.getTerritory(false);
        message += p1Name +' '+ TERRITORY + blackTerritory +'\n';
        message += p2Name +' '+ TERRITORY + whiteTerritory +"\n\n";

        message += p1Name +' '+ SCORE + gc.getFinalScore(true) +'\n';
        message += p2Name +' '+ SCORE + gc.getFinalScore(false) +'\n';

        return super.getGameOverMessage() +'\n'+ message;
    }

    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        if (get2PlayerController().isProcessing())
            return "";  // avoids concurrent modification exception

        Location loc = getBoardRenderer().createLocation(e);
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        GoBoardPosition space = (GoBoardPosition) controller_.getBoard().getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            String spaceText = space.getDescription();
            sb.append( spaceText);
            GoString string = space.getString();
            GoEye eye = space.getEye();
            if ( string != null ) {
                sb.append( "<br>" );
                sb.append("string liberties = ").append(string.getNumLiberties((GoBoard) controller_.getBoard()));
                String stringText = string.toString();
                if ( string.getGroup() != null ) {
                    sb.append( "<br>" );
                    String groupText = string.getGroup().toHtml();

                    groupText = groupText.replaceAll(stringText, "<font color=#440000>" + stringText + "</font>" );
                    groupText = groupText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>");
                    sb.append( groupText );
                }
            }
            // it might belong to both an eye and a string
            if (eye != null) {
               String eyeText = eye.toString();
               sb.append( "<br>" );
               eyeText = eyeText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>");
               sb.append(eyeText);
               // to debug show the group that contains this eye
               sb.append( "<br>" );
                sb.append("The group that contains this eye is ").append(eye.getGroup());
            }
        }
        else {
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
