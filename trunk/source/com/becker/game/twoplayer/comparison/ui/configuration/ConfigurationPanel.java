// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.configuration;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
public final class ConfigurationPanel extends JPanel
                              implements ActionListener {

    private GradientButton addConfigButton_;
    private GradientButton removeConfigButton_;
    private ConfigurationsTable configTable_;


    /**
     * constructor - create the tree dialog.
     */
    public ConfigurationPanel() {

        configTable_ = new ConfigurationsTable();
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());
        JPanel addremoveButtonsPanel = new JPanel();

        addConfigButton_ = new GradientButton("new entry");
        addConfigButton_.addActionListener(this);
        addremoveButtonsPanel.add(addConfigButton_, BorderLayout.CENTER);

        removeConfigButton_ = new GradientButton(GameContext.getLabel("remove entry"));
        removeConfigButton_.addActionListener(this);
        addremoveButtonsPanel.add(removeConfigButton_, BorderLayout.EAST);

        JPanel titlePanel = new JPanel(new BorderLayout());
                titlePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST);


        add(titlePanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(configTable_.getTable());
        scrollPane.setPreferredSize(new Dimension(360,120));
        scrollPane.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                scrollPane.getBorder()));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * @return  the options list in the table
     */
    public List<SearchOptionsConfig> getConfigurations() {
        return configTable_.getSearchOptions();
    }

    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == addConfigButton_) {
            addConfiguration();
        }
        else if (source == removeConfigButton_) {

            // should remove selected
            configTable_.removeRow(configTable_.getNumRows()-1);

        }
    }

    /**
     * add another row to the end of the table.
     */
    private void addConfiguration()  {

        SearchOptionsDialog optionsDialog = new SearchOptionsDialog(this);
        boolean canceled = optionsDialog.showDialog();

        if ( !canceled ) {
            SearchOptionsConfig options = optionsDialog.getSearchOptionsConfig();
            if (options != null)
                configTable_.addRow(options);
        }
    }

}

