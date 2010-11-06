package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.ui.GameChangedListener;
import com.becker.game.common.ui.GameInfoPanel;
import com.becker.game.multiplayer.set.SetController;
import com.becker.game.multiplayer.set.SetPlayer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;


/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class SetInfoPanel extends GameInfoPanel
                   implements GameChangedListener, ListSelectionListener
{

    private SetSummaryTable playerTable_;

    private JPanel playerPanel_;

    private JLabel numSetsOnBoardLabel_;
    private JLabel numCardsRemainingLabel_;


    /**
     * Constructor
     */
    SetInfoPanel( GameController controller )
    {
        super(controller);
    }

    @Override
    protected void createSubPanels()
    {
        add( createGeneralInfoPanel() );
        add( createCustomInfoPanel() );
    }

    @Override
    protected int getMinWidth() {
        return 250;
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Set, we have a button that allows the current player to enter his commands
     */
    @Override
    protected JPanel createCustomInfoPanel()
    {
        JPanel pp = createSectionPanel("Players");

        playerPanel_ = createPanel();
        playerPanel_.setLayout(new BorderLayout());
        playerPanel_.setBorder(createMarginBorder());

        insertPlayerTable();

        pp.add(playerPanel_);
        return pp;
    }


    /**
     * this is general information that is applicable to every 2 player game.
     */
    @Override
    protected JPanel createGeneralInfoPanel()
    {
        JPanel generalPanel = createSectionPanel(GameContext.getLabel("GENERAL_INFO"));

        JLabel numSetsOnBoardText = createLabel(GameContext.getLabel("NUMBER_OF_SETS_ON_BOARD") + COLON);
        numSetsOnBoardLabel_ = createLabel( " " );

        JLabel numCardsRemainingText = createLabel( GameContext.getLabel("NUMBER_OF_CARDS_REMAINING") + COLON);
        numCardsRemainingLabel_ = createLabel( " " );
        numCardsRemainingLabel_.setHorizontalAlignment(JLabel.LEFT);

        generalPanel.add( createRowEntryPanel( numSetsOnBoardText, numSetsOnBoardLabel_ ) );
        generalPanel.add( createRowEntryPanel( numCardsRemainingText, numCardsRemainingLabel_ ) );

        generalPanel.add( Box.createGlue() );

        return generalPanel;
    }

    @Override
    protected void setPlayerLabel() {
    }

    void insertPlayerTable() {

        playerPanel_.removeAll();

        playerTable_ = new SetSummaryTable(controller_.getPlayers());
        playerTable_.addListSelectionListener(this);

        playerPanel_.add(playerTable_.getTable(), BorderLayout.CENTER);
    }

    /**
     * restore to new game state.
     */
    @Override
    public void reset() {
        insertPlayerTable();
        invalidate();
        repaint();
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever something on the board has changed.
     */
    @Override
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;

        SetController c = (SetController)controller_;
        numSetsOnBoardLabel_.setText( c.getNumSetsOnBoard() + " " );

        int cardsInDeck = c.getDeck().size() - c.getNumCardsShowing();
        numCardsRemainingLabel_.setText( cardsInDeck + " " );

        SetPlayer player = getSelectedPlayer();
        if (player != null) {
            int r = playerTable_.getTable().getSelectedRow();
            playerTable_.getTable().getModel().setValueAt(""+player.getNumSetsFound(), r, 2);
            playerTable_.getTable().clearSelection();
        }
    }


    /**
     * @return null if no current player
     */
    private SetPlayer getSelectedPlayer() {
        SetController c = (SetController)controller_;
        int selectedPlayerIndex = playerTable_.getTable().getSelectedRow();
        SetPlayer selectedPlayer = null;
        if (selectedPlayerIndex >= 0) {
            selectedPlayer = (SetPlayer) c.getPlayers().get(selectedPlayerIndex);
        }
        return selectedPlayer;
    }

    public void valueChanged(ListSelectionEvent e) {

        SetController c = (SetController)controller_;
        c.setCurrentPlayer(getSelectedPlayer());
    }
}