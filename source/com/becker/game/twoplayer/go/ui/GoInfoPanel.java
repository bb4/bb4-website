package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;

import javax.swing.*;

/**
 *  Show information and statistics specific to the game of go
 *
 *  @author Barry Becker
 */
final class GoInfoPanel extends TwoPlayerInfoPanel implements GameChangedListener
{

    // do not initialize these to null.
    // if you do, things will not work. With the 1.3.1_02 compiler, they will get initialized to values when
    // you call createCustomInfoPanel from the super class constructor, but then they will then get initiallized
    // to null when it is done calling the super class constructor and then calls the constructor for this class.
    private JLabel p1CapturesLabel_;
    private JLabel p2CapturesLabel_;
    private JLabel p1TerritoryLabel_;
    private JLabel p2TerritoryLabel_;

    private static final String COLON = " " + GameContext.getLabel("COLON")+ " ";

    //Construct the GoInfoPael
    public GoInfoPanel( GameController controller )
    {
        super( controller );
    }

    protected final String getTitleText()
    {
        return GameContext.getLabel("GO_INFO");
    }


    /**
     * This panel shows information that is specific to go - specifically
     * captures and territory estimates
     */
    protected final JPanel createCustomInfoPanel()
    {
        JPanel customPanel = createPanel();
        customPanel.setLayout( new BoxLayout( customPanel, BoxLayout.Y_AXIS ) );

        p1CapturesLabel_ = createLabel();
        p2CapturesLabel_ = createLabel();

        p1TerritoryLabel_ = createLabel();
        p2TerritoryLabel_ = createLabel();

        JPanel capturesPanel = createSectionPanel(GameContext.getLabel("NUMBER_OF_CAPTURES"));
        capturesPanel.add( createRowEntryPanel( createLabel( getController().getPlayer1().getName()+COLON ), p1CapturesLabel_ ) );
        capturesPanel.add( createRowEntryPanel( createLabel( getController().getPlayer2().getName()+COLON ), p2CapturesLabel_ ) );

        JPanel territoryPanel = createSectionPanel(GameContext.getLabel("EST_TERRITORY"));
        territoryPanel.add(
                createRowEntryPanel( createLabel( GameContext.getLabel("EST_BLACK_TERR_COLON") ), p1TerritoryLabel_ ) );
        territoryPanel.add(
                createRowEntryPanel( createLabel( GameContext.getLabel("EST_WHITE_TERR_COLON") ), p2TerritoryLabel_ ) );

        customPanel.add( capturesPanel );
        customPanel.add( territoryPanel );

        return customPanel;
    }

    /**
     * update the info with controller stats when the game changes.
     */
    public final void gameChanged( GameChangedEvent gce )
    {
        super.gameChanged( gce );
        GoController goController = (GoController) controller_;

        if ( p1CapturesLabel_ == null )
            return;

        p1CapturesLabel_.setText( goController.getNumCaptures( true ) + " " );
        p2CapturesLabel_.setText( goController.getNumCaptures( false ) + " " );

        p1TerritoryLabel_.setText( goController.getTerritoryEstimate( true ) + " " );
        p2TerritoryLabel_.setText( goController.getTerritoryEstimate( false ) + " " );
    }

}