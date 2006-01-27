package com.becker.game.twoplayer.go.ui;

import com.becker.common.ColorMap;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.Location;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.go.*;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game. The GoController contains a GoBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
final class GoBoardViewer extends TwoPlayerBoardViewer
{

    // a colormap for coloring the groups according to how healthy they are
    // blue will be healthy, while red will be near dead
    private static final double[] values_ = {-1.1, -1.0, -0.2, 0.1, -0.05,
                                             0.0,
                                             0.05, 0.1, 0.2, 1.0, 1.1};
    private static final int CM_TRANS = 50;
    // this colormap is used to show a spectrum of colors representing a groups health status.
    private static final Color[] colors_ = {new Color( 200, 0, 0, CM_TRANS + 40 ),
                                            new Color( 255, 20, 0, CM_TRANS ), new Color( 250, 130, 0, CM_TRANS ), new Color( 250, 255, 0, CM_TRANS ), new Color( 200, 200, 90, CM_TRANS ),
                                            new Color( 220, 220, 220, 0 ),
                                            new Color( 30, 220, 20, CM_TRANS ), new Color( 0, 255, 0, CM_TRANS ), new Color( 0, 255, 255, CM_TRANS ), new Color( 0, 0, 255, CM_TRANS ),
                                            new Color( 150, 0, 250, CM_TRANS + 40 )};
    private static final ColorMap colormap_ = new ColorMap( values_, colors_ );

    // the image for the wouden board.
    private static final ImageIcon woodGrainImage_ =
            GUIUtil.getIcon(GameContext.GAME_ROOT + "twoplayer/go/ui/images/goBoard1.png");

    private static final String STONES_CAPTURED = GameContext.getLabel("CAPTURES_EQUALS");
    private static final String TERRITORY = GameContext.getLabel("TERRITORY_EQUALS");
    private static final String SCORE = GameContext.getLabel("SCORE_EQUALS");

    /**
     * Construct the viewer given the controller.
     */
    GoBoardViewer()
    {
        pieceRenderer_ = GoStoneRenderer.getRenderer();
    }

    protected GameController createController()
    {
        return new GoController();
    }

    protected int getDefaultCellSize()
    {
        return 16;
    }


    /**
     * first draw borders for the groups in the appropriate color, then draw the pieces for both players.
     */
    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {
        GoBoard board = (GoBoard)getBoard();

        // draw the starpoint markers
        List starpoints = board.getHandicapPositions();
        Iterator it = starpoints.iterator();
        g2.setColor(Color.black);
        double rad = (float)cellSize_/21.0+0.1;
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition)it.next();
            g2.fillOval(BOARD_MARGIN+(int)(cellSize_*(p.getCol()-0.5)-rad),
                        BOARD_MARGIN+(int)(cellSize_*(p.getRow()-0.5)-rad),
                        (int)(2.0*rad+1.7), (int)(2.0*rad+1.7));
        }

        Set groups = board.getGroups();
        // draw the group borders
        if ( GameContext.getDebugMode() > 0 ) {
            it = groups.iterator();
            //System.out.println( "drawing group decor: ***The groups on the board are: ***\n"+board.getGroupsText());
            while ( it.hasNext() ) {
                GoGroup group = (GoGroup) it.next();
                GoGroupRenderer.drawGroupDecoration(group, colormap_, (float) cellSize_, board, g2 );
            }
        }

        super.drawMarkers( nrows, ncols, g2 );

        drawNextMoveMarkers(g2);
    }

    /**
     * whether to draw the pieces on cell centers or vertices (like go)
     */
    protected boolean offsetGrid()
    {
        return true;
    }


    public void pass()
    {
        GameContext.log( 1, "passing" );
        GoMove m = GoMove.createPassMove( 0.0, get2PlayerController().isPlayer1sTurn() );
        continuePlay( m );
    }

    public static ColorMap getColorMap() {
        return colormap_;
    }


    /**
     *  mouseClicked requires both the mouse down and mouse up event to occur at the same location.
     *  classes derived from TwoPlayerBoardViewer must call mousePressed first.
     */
    public void mousePressed( MouseEvent e )
    {
        // all derived classes must check this to disable user clicks while the computer is thinking
        if (get2PlayerController().isProcessing())
            return;
        Location loc = createLocation(e, getCellSize());
        GoBoard board = (GoBoard) controller_.getBoard();
        GoController controller = (GoController) controller_;

        GameContext.log( 3, "GoBoardViewer: mousePressed: controller_.isPlayer1sTurn()=" + get2PlayerController().isPlayer1sTurn() );

        GoMove m = GoMove.createGoMove( loc.getRow(), loc.getCol(), 0, new GoStone(controller.isPlayer1sTurn()));

        // if there is already a piece where the user clicked, or its
        // out of bounds, or its a suicide move, then return without doing anything
        GoBoardPosition stone = (GoBoardPosition) board.getPosition( loc );
        if ( stone == null )
            return;      // user clicked out of bounds

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

        //board.makeMove( m );


        if ( !continuePlay( m ) ) {   // then game over
            showWinnerDialog();
        }
    }

     /**
     * display a dialog at the end of the game showing who won and other relevant
     * game specific information.
     */
    protected void showWinnerDialog()
    {
         super.showWinnerDialog();
         ((GoController)controller_).clearGameOver();
    }


    /**
     * @return   the message to display at the completion of the game.
     */
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
     * draw the wood grain background.
     * @param g
     */
    protected void drawBackground( Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        super.drawBackground( g ,  startPos, rightEdgePos, bottomEdgePos);
        int t = (int)(cellSize_/3.4f);
        g.drawImage(woodGrainImage_.getImage(), (int)(startPos-1.35*t), (int)(startPos-1.35*t),
                                                (rightEdgePos+t), (bottomEdgePos+t), null);
    }


    /**
     * @return the tooltip for the panel given a mouse event.
     */
    public String getToolTipText( MouseEvent e )
    {
        if (get2PlayerController().isProcessing())
            return "";  // avoids concurrent modification exception

        Location loc = createLocation(e, getCellSize());
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        GoBoardPosition space = (GoBoardPosition) controller_.getBoard().getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            String spaceText = space.toString();
            sb.append( spaceText);
            GoString string = space.getString();
            GoEye eye = space.getEye();
            if ( string != null ) {
                sb.append( "<br>" );
                sb.append( "string liberties = " + string.getNumLiberties((GoBoard)controller_.getBoard()) );
                String stringText = string.toString();
                if ( string.getGroup() != null ) {
                    sb.append( "<br>" );
                    String groupText = string.getGroup().toHtml();
                    groupText = GUIUtil.replaceString( groupText, stringText, "<font color=#440000>" + stringText + "</font>" );
                    groupText = GUIUtil.replaceString( groupText, spaceText, "<b><font color=#991100>" + spaceText + "</font></b>" );
                    sb.append( groupText );
                }
            }
            // it might belong to both an eye and a string
            if (eye != null) {
               String eyeText = eye.toString();
               sb.append( "<br>" );
               eyeText = GUIUtil.replaceString( eyeText, spaceText, "<b><font color=#991100>" + spaceText + "</font></b>");
               sb.append(eyeText);
               // to debug show the group that contains this eye
               sb.append( "<br>" );
               sb.append("The group that contains this eye is "+eye.getGroup());
            }
        }
        else {
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }
}
