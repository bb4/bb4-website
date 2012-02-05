// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.execution;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerViewable;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;
import com.becker.game.twoplayer.comparison.model.*;

import javax.swing.*;

/**
 * Run through the grid of game combinations and gather the performance results
 * @author Barry Becker
 */
public class PerformanceRunner {

    private SearchOptionsConfigList optionsList;
    private TwoPlayerPanel gamePanel_;
    private PerformanceRunnerListener listener;
    
    public PerformanceRunner(TwoPlayerPanel gamePanel, SearchOptionsConfigList optionsList,
                             PerformanceRunnerListener listener)  {
        this.optionsList = optionsList;
        this.gamePanel_ = gamePanel;
        this.listener = listener;
    }

    /**
     * Run the NxN comparison and return the results.
     * @return model with all the results
     */
    public void doComparisonRuns() {

        GameRunnerDialog runnerDialog = new GameRunnerDialog(gamePanel_);
        runnerDialog.showDialog();
        gamePanel_.init(null);

        PerformanceWorker worker =
                new PerformanceWorker(gamePanel_.get2PlayerController(), optionsList, listener);
        SwingUtilities.invokeLater(worker);
    }

}
