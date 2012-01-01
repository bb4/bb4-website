// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.common.ui.dialogs;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameWeights;
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

    private JPanel searchOptionsPanel;
    private EditWeightsPanel weightsPanel;
    
    private ParameterArray weights;
    private GameWeights gameWeights;

    /**  constructor  */
    PlayerOptionsDialog(JFrame parent, ParameterArray weights, GameWeights gameWeights) {
        super( parent );
        this.weights = weights;
        this.gameWeights = gameWeights;
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

        System.out.println("created mainPanel...");

        // contains tabs for search, and weights
        JTabbedPane tabbedPanel = new JTabbedPane();
        //tabbedPanel.setPreferredSize(new Dimension(450, 350));

        searchOptionsPanel = new JPanel(); //new SearchOptionsPanel();
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

        //searchOptionsPanel.ok();
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

