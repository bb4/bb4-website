package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.ui.ViewerMouseListener;
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
final class GoBoardViewer extends AbstractTwoPlayerBoardViewer {

    private static final String STONES_CAPTURED = GameContext.getLabel("CAPTURES_EQUALS");
    private static final String TERRITORY = GameContext.getLabel("TERRITORY_EQUALS");
    private static final String SCORE = GameContext.getLabel("SCORE_EQUALS");


    /**
     * Construct the viewer given the controller.
     */
    GoBoardViewer() {}


    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new GoViewerMouseListener(this);
    }
    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame()  {
        super.startNewGame();
        getBoardRenderer().setDraggedShowPiece(null);
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
    public void pass() {
        GameContext.log( 1, "passing" );
        GoMove m = GoMove.createPassMove( 0, get2PlayerController().isPlayer1sTurn() );
        continuePlay( m );
    }


    /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    @Override
    public void showWinnerDialog()
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
