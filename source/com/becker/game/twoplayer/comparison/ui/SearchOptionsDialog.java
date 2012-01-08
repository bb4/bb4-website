// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.common.ui.dialogs.searchoptions.SearchOptionsPanel;
import com.becker.ui.components.GradientButton;
import com.becker.ui.components.TextInput;
import com.becker.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow for editing player search options.
 *
 * @author Barry Becker
 */
public class SearchOptionsDialog extends OptionsDialog {

    private GradientButton okButton_;

    private SearchOptionsPanel searchOptionsPanel;


    /** Constructor  */
    public SearchOptionsDialog(Component parent) {
        super( parent );
        showContent();
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("EDIT_PLAYER_OPTIONS");
    }

    public SearchOptions getSearchOptions() {
        return searchOptionsPanel.getOptions();
    }

    @Override
    protected JComponent createDialogContent() {
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        TextInput nameField = new TextInput("Configuration Name:", "", 30);
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        // all defaults initially.
        SearchOptions searchOptions = new SearchOptions();

        searchOptionsPanel = new SearchOptionsPanel(searchOptions);
        
        mainPanel.add(nameField, BorderLayout.NORTH);
        mainPanel.add( searchOptionsPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
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

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        if ( source == okButton_ ) {
            searchOptionsPanel.ok();
            dispose();
        }
    }
}

