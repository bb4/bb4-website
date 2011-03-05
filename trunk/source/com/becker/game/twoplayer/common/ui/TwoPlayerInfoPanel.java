package com.becker.game.twoplayer.common.ui;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.ui.panel.GameChangedEvent;
import com.becker.game.common.ui.panel.GameChangedListener;
import com.becker.game.common.ui.panel.GameInfoPanel;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.WinProbabilityCaclulator;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public class TwoPlayerInfoPanel extends GameInfoPanel implements GameChangedListener {

    private JLabel chanceOfWinningLabel_;


    /**
     * Constructor
     */
    public TwoPlayerInfoPanel( GameController controller ) {
        super(controller);
    }

    protected TwoPlayerController getController() {
        return (TwoPlayerController)controller_;
    }

    /**
     * this is general information that is applicable to every 2 player game.
     */
    @Override
    protected JPanel createGeneralInfoPanel()  {

        JPanel generalPanel = createSectionPanel(GameContext.getLabel("GENERAL_INFO"));

        JLabel turnLabel = createLabel(GameContext.getLabel("PLAYER_TO_MOVE") + COLON);
        initPlayerLabel();

        JLabel moveNumTextLabel = createLabel( GameContext.getLabel("CURRENT_MOVE_NUM") + COLON);
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel_ = createLabel( "  " );

        Object[] args = {getController().getPlayers().getPlayer1().getName()};
        String m = MessageFormat.format(GameContext.getLabel("CHANCE_OF_WINNING") + COLON, args );
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
    @Override
    protected void setPlayerLabel() {

        AbstractTwoPlayerBoardViewer viewer = (AbstractTwoPlayerBoardViewer)controller_.getViewer();
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        PlayerList players = getController().getPlayers();
        boolean p1sturn = getController().isPlayer1sTurn();
        String player = p1sturn? players.getPlayer1().getName() : players.getPlayer2().getName();
        playerLabel_.setText(' ' + player + ' ');

        Color pColor = p1sturn? renderer.getPlayer1Color() : renderer.getPlayer2Color();
        playerLabel_.setBorder(getPlayerLabelBorder(pColor));

        repaint();
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {

        if ( controller_ == null )
            return;
        if ( controller_.getLastMove() != null ) {
            setPlayerLabel();
            moveNumLabel_.setText( controller_.getNumMoves() + " " );
            WinProbabilityCaclulator calc = new WinProbabilityCaclulator();
            String formattedPropability =
                    Util.formatNumber(calc.getChanceOfPlayer1Winning(getController().getMoveList().copy()));
            chanceOfWinningLabel_.setText(  formattedPropability + ' ' );
        }
    }

}