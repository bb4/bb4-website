// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid;

import com.becker.game.twoplayer.comparison.execution.PerformanceRunner;
import com.becker.game.twoplayer.comparison.model.ResultsModel;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
public final class ComparisonGridPanel extends JPanel
                                       implements ActionListener {

    private GradientButton runButton_;
    private ComparisonGrid grid_;
    private JScrollPane scrollPane;
    List<SearchOptionsConfig> optionsList; 


    /**
     * constructor - create the tree dialog.
     */
    public ComparisonGridPanel() {

        grid_ = ComparisonGrid.createInstance(new ArrayList<SearchOptionsConfig>());
        init();
    }
    
    public void setOptionsList(List<SearchOptionsConfig> optionsList) {

        grid_ = ComparisonGrid.createInstance(optionsList);
        this.optionsList = optionsList;
        runButton_.setEnabled(optionsList.size() > 0);
        scrollPane.setViewportView(grid_.getTable());
    }

    private void init() {

        this.setLayout(new BorderLayout());
        JPanel addremoveButtonsPanel = new JPanel();

        runButton_ = new GradientButton("Run comparisons");
        runButton_.addActionListener(this);
        runButton_.setEnabled(false);
        addremoveButtonsPanel.add(runButton_, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
                titlePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(grid_.getTable());
        scrollPane.setPreferredSize(new Dimension(360,120));
        scrollPane.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                scrollPane.getBorder()));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == runButton_) {
            PerformanceRunner runner = new PerformanceRunner(optionsList);//, gameName);
            ResultsModel resultsModel = runner.doComparisonRuns();
            System.out.println("resultsModel =" + resultsModel);
        }
        
    }
    
  
}

