package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoSearchable;
import com.becker.game.twoplayer.go.board.BoardValidator;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.ui.legend.ContinuousColorLegend;

import javax.swing.*;

/**
 *  Show information and statistics specific to the game of go
 *
 *  @author Barry Becker
 */
final class GoInfoPanel extends TwoPlayerInfoPanel implements GameChangedListener {

    // do not initialize these to null.
    // if you do, things will not work. With the java 1.3.1_02 compiler, they will get initialized to values when
    // you call createCustomInfoPanel from the super class constructor, but then they will then get initialized
    // to null when it is done calling the super class constructor and then calls the constructor for this class.
    private JLabel p1CapturesLabel_;
    private JLabel p2CapturesLabel_;
    private JLabel p1TerritoryLabel_;
    private JLabel p2TerritoryLabel_;

    private JPanel legendPanel_;

    /**
     * Constructor
     */
    GoInfoPanel( GameController controller ) {
        super( controller );
    }

    @Override
    protected String getTitleText() {
        return GameContext.getLabel("GO_INFO");
    }


    @Override
    protected void createSubPanels() {
        super.createSubPanels();

        legendPanel_ = createLegendPanel();
        this.add( legendPanel_ );
        legendPanel_.setVisible(GameContext.getDebugMode() > 0);
    }


    /**
     * This panel shows information that is specific to go - specifically
     * captures and territory estimates
     */
    @Override
    protected JPanel createCustomInfoPanel() {

        JPanel customPanel = createPanel();
        customPanel.setLayout( new BoxLayout( customPanel, BoxLayout.Y_AXIS ) );

        p1CapturesLabel_ = createLabel();
        p2CapturesLabel_ = createLabel();

        p1TerritoryLabel_ = createLabel();
        p2TerritoryLabel_ = createLabel();

        JPanel capturesPanel = createSectionPanel(GameContext.getLabel("NUMBER_OF_CAPTURES"));
        PlayerList players = getController().getPlayers();
        JLabel p1 = createLabel( players.getPlayer1().getName() + COLON );
        JLabel p2 = createLabel( players.getPlayer2().getName() + COLON );
        capturesPanel.add(createRowEntryPanel( p1, p1CapturesLabel_ ));
        capturesPanel.add(createRowEntryPanel( p2, p2CapturesLabel_ ));

        JPanel territoryPanel = createSectionPanel(GameContext.getLabel("EST_TERRITORY"));
        JLabel blackTerr = createLabel( GameContext.getLabel("EST_BLACK_TERR") + COLON );
        JLabel whiteTerr = createLabel( GameContext.getLabel("EST_WHITE_TERR") + COLON );
        territoryPanel.add(createRowEntryPanel( blackTerr, p1TerritoryLabel_ ));
        territoryPanel.add(createRowEntryPanel( whiteTerr, p2TerritoryLabel_ ));

        customPanel.add( capturesPanel );
        customPanel.add( territoryPanel );

        return customPanel;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = createSectionPanel("Group Health Legend");
        ContinuousColorLegend legend =
                new ContinuousColorLegend(null, GoBoardRenderer.COLORMAP, false);
        legendPanel.add(legend);
        return legendPanel;
    }

    /**
     * update the info with controller stats when the game changes.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        
        super.gameChanged( gce );
        GoController goController = (GoController) controller_;

        if ( p1CapturesLabel_ == null )
            return;

        GoSearchable searchable = (GoSearchable) goController.getSearchable().copy();
        p1CapturesLabel_.setText( searchable.getNumCaptures( false ) + " " );
        p2CapturesLabel_.setText( searchable.getNumCaptures( true ) + " " );

        new BoardValidator((GoBoard)searchable.getBoard()).confirmStonesInValidGroups();
        p1TerritoryLabel_.setText( searchable.getTerritoryEstimate( true ) + " " );
        p2TerritoryLabel_.setText( searchable.getTerritoryEstimate( false ) + " " );

        legendPanel_.setVisible(GameContext.getDebugMode() > 0);
    }

}