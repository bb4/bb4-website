// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.configuration;

import com.becker.game.twoplayer.common.search.options.SearchOptions;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;
import com.becker.game.twoplayer.comparison.model.data.DefaultSearchConfigurations;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
public final class ConfigurationPanel extends JPanel
                              implements ActionListener, ListSelectionListener {

    private static final SearchOptionsConfigList DEFAULT_CONFIGURATIONS =
            new DefaultSearchConfigurations();

    private GradientButton addConfigButton_;
    private GradientButton editConfigButton_;
    private GradientButton removeConfigButton_;
    private ConfigurationsTable configTable_;
    //private int selectedRow;


    /**
     * constructor - create the tree dialog.
     */
    public ConfigurationPanel() {

        configTable_ = new ConfigurationsTable(DEFAULT_CONFIGURATIONS);
        configTable_.addListSelectionListener(this);
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());
        JPanel addremoveButtonsPanel = new JPanel();

        addConfigButton_ = new GradientButton("New");
        addConfigButton_.setToolTipText("add a new entry in the table");
        addConfigButton_.addActionListener(this);
        addremoveButtonsPanel.add(addConfigButton_, BorderLayout.WEST);
        
        editConfigButton_ = new GradientButton("Edit");
        editConfigButton_.setToolTipText("edit an existing entry in the table");
        editConfigButton_.addActionListener(this);
        editConfigButton_.setEnabled(false);
        addremoveButtonsPanel.add(editConfigButton_, BorderLayout.CENTER);

        removeConfigButton_ = new GradientButton("Remove");
        removeConfigButton_.setToolTipText("remove an entry from the table");
        removeConfigButton_.addActionListener(this);
        removeConfigButton_.setEnabled(false);
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
    public SearchOptionsConfigList getConfigurations() {
        return configTable_.getSearchOptions();
    }

    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == addConfigButton_) {
            addConfiguration();
        }
        else if (source == editConfigButton_) {

            editConfiguration(configTable_.getSelectedRow());
        }
        else if (source == removeConfigButton_) {

            configTable_.removeRow(configTable_.getSelectedRow());
            removeConfigButton_.setEnabled(false);
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
    
    /**
     * add another row to the end of the table.
     */
    private void editConfiguration(int row)  {

        SearchOptionsConfig initOptions = getConfigurations().get(row);
        System.out.println("now editing row="+ row);
        SearchOptionsDialog optionsDialog = new SearchOptionsDialog(this, initOptions);
        boolean canceled = optionsDialog.showDialog();

        if ( !canceled ) {
            SearchOptionsConfig options = optionsDialog.getSearchOptionsConfig();
            if (options != null) {
                configTable_.updateRow(row, options);
            }
        }
    }

    /** called when one of the rows in the grid is selected */
    public void valueChanged(ListSelectionEvent e) {
        //selectedRow = e.getFirstIndex();
        ///System.out.println("selected row=" + selectedRow);
        editConfigButton_.setEnabled(true);
        removeConfigButton_.setEnabled(true);
    }
}

