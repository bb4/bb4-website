package com.becker.game.twoplayer.common.ui;

import com.becker.common.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.common.ui.GameInfoPanel;
import com.becker.game.twoplayer.common.TwoPlayerController;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public class TwoPlayerInfoPanel extends GameInfoPanel implements GameChangedListener
{

    private JLabel chanceOfWinningLabel_;


    /**
     * Constructor
     */
    public TwoPlayerInfoPanel( GameController controller )
    {
        super(controller);

    }

    protected TwoPlayerController getController()
    {
        return (TwoPlayerController)controller_;
    }

    /**
     * this is general information that is applicable to every 2 player game.
     */
    protected JPanel createGeneralInfoPanel()
    {
        JPanel generalPanel = createSectionPanel(GameContext.getLabel("GENERAL_INFO"));

        JLabel turnLabel = createLabel(GameContext.getLabel("PLAYER_TO_MOVE_COLON"));
        playerLabel_ = new JLabel();
        playerLabel_.setOpaque(true);
        playerLabel_.setFont(BOLD_FONT);
        setPlayerLabel();

        JLabel moveNumTextLabel = createLabel( GameContext.getLabel("CURRENT_MOVE_NUM_COLON"));
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel_ = createLabel( "  " );

        Object[] args = {getController().getPlayer1().getName()};
        String m = MessageFormat.format(GameContext.getLabel("CHANCE_OF_WINNING"), args );
        JLabel chanceOfWinningTextLabel = createLabel( m );
        chanceOfWinningTextLabel.setHorizontalAlignment(JLabel.LEFT);
        chanceOfWinningLabel_ = createLabel( "   " );
        //showRecommendedMove_ = new JCheckBox( "Show recommended move", false );

        generalPanel.add( createRowEntryPanel( turnLabel, playerLabel_ ) );
        generalPanel.add( createRowEntryPanel( moveNumTextLabel, moveNumLabel_ ) );
        generalPanel.add( createRowEntryPanel( chanceOfWinningTextLabel, chanceOfWinningLabel_ ) );
        // add this back in when it is implemented
        //generalPanel.add( createRowEntryPanel(showRecommendedMove_) );
        generalPanel.add( Box.createGlue() );

        return generalPanel;
    }



    /**
     * set the appropriate text and color for the player label.
     */
    protected void setPlayerLabel()
    {
        TwoPlayerBoardViewer viewer = (TwoPlayerBoardViewer)controller_.getViewer();
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        boolean p1sturn = getController().isPlayer1sTurn();
        String player = p1sturn? getController().getPlayer1().getName() : getController().getPlayer2().getName();
        playerLabel_.setText(" " + player + " ");

        Color pColor = p1sturn? renderer.getPlayer1Color() : renderer.getPlayer2Color();
        playerLabel_.setBorder(getPlayerLabelBorder(pColor));

        repaint();
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;
        if ( controller_.getBoard().getLastMove() != null ) {
            setPlayerLabel();
            moveNumLabel_.setText( controller_.getNumMoves() + " " );
        }
        chanceOfWinningLabel_.setText( Util.formatNumber(getController().getChanceOfPlayer1Winning()) + " " );
    }

}