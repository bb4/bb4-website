// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.common.ui.dialogs;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameWeights;
import com.becker.game.common.player.Player;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.ui.dialogs.options.SearchOptionsPanel;
import com.becker.game.twoplayer.go.options.GoPlayerOptions;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow for editing player options.
 * There are two tabs: search options and game weights.
 *
 * @author Barry Becker
 */
class PlayerOptionsDialog extends OptionsDialog {

    private GradientButton okButton_;

    private SearchOptionsPanel searchOptionsPanel;
    private EditWeightsPanel weightsPanel;
    
    private ParameterArray weights;
    private TwoPlayerController controller;
    private Player player;

    /** Constructor  */
    PlayerOptionsDialog(JFrame parent, TwoPlayerController controller, boolean showForPlayer1) {
        super( parent );
        
        this.controller = controller;
        GameWeights gameWeights = controller.getComputerWeights();
        ParameterArray weights = 
                showForPlayer1? gameWeights.getPlayer1Weights() : gameWeights.getPlayer2Weights();

        player = controller.getPlayers().get(showForPlayer1?0:1);
        this.weights = weights;
        
        showContent();
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("EDIT_PLAYER_OPTIONS");
    }

    @Override
    protected JComponent createDialogContent() {
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        // contains tabs for search, and weights
        JTabbedPane tabbedPanel = new JTabbedPane();

        GameWeights gameWeights = controller.getComputerWeights();
        
        SearchOptions searchOptions =
                ((TwoPlayerPlayerOptions)player.getOptions()).getSearchOptions();

        searchOptionsPanel = new SearchOptionsPanel(searchOptions);
        weightsPanel = new EditWeightsPanel(weights, gameWeights);

        tabbedPanel.add( GameContext.getLabel("SEARCH_OPTIONS"), searchOptionsPanel );
        tabbedPanel.add( GameContext.getLabel("EDIT_WEIGHTS"), weightsPanel );

        mainPanel.add( tabbedPanel, BorderLayout.CENTER );
        mainPanel.add( createButtonsPanel(), BorderLayout.SOUTH );

        return mainPanel;
    }

    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("ACCEPT_PLAYER_OPTIONS") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL_EDITS") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    private void ok() {

        searchOptionsPanel.ok();
        weightsPanel.ok();
    }


    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
            dispose();
        }
    }
}

