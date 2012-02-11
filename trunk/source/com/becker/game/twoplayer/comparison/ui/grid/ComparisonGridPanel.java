// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.ui.menu.GameMenuListener;
import com.becker.game.common.ui.panel.IGamePanel;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;
import com.becker.game.twoplayer.comparison.execution.PerformanceRunner;
import com.becker.game.twoplayer.comparison.execution.PerformanceRunnerListener;
import com.becker.game.twoplayer.comparison.model.ResultsModel;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Show grid of game trials with run button at top.
 *
 * @author Barry Becker
 */
public final class ComparisonGridPanel
           extends JPanel
        implements ActionListener, GameMenuListener, PerformanceRunnerListener {

    private GradientButton runButton_;
    private ComparisonGrid grid_;
    private JScrollPane scrollPane;
    private SearchOptionsConfigList optionsList;
    private String gameName;

    /**
     * constructor - create the tree dialog.
     */
    public ComparisonGridPanel() {

        grid_ = ComparisonGrid.createInstance(new SearchOptionsConfigList());
        init();
    }
    
    public void setOptionsList(SearchOptionsConfigList optionsList) {

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

            final IGamePanel gamePanel = createGamePanel(gameName);

            PerformanceRunner runner =
                new PerformanceRunner((TwoPlayerPanel)gamePanel, optionsList, this);

            // when done performanceRunsDone will be called.
            runner.doComparisonRuns();
        }        
    }

    public void gameChanged(String gameName) {

        this.gameName = gameName;
    }

    public IGamePanel createGamePanel(String gameName) {

        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName);
        grid_.setGameName(gameName);

        return PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
    }

    @Override
    public void paint(Graphics g) {

        grid_.updateRowHeight(scrollPane.getHeight());
        super.paint(g);
    }

    public void performanceRunsDone(ResultsModel model) {
        grid_.updateWithResults(model);
    }
}

