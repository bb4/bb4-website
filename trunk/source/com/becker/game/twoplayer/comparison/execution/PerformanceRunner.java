// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.execution;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.plugin.GamePlugin;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.ui.panel.IGamePanel;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;
import com.becker.game.twoplayer.comparison.model.PerformanceResults;
import com.becker.game.twoplayer.comparison.model.ResultsModel;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.becker.game.twoplayer.comparison.model.SearchOptionsConfigList;

/**
 * Run through the grid of game combinations and gather the performance results
 * @author Barry Becker
 */
public class PerformanceRunner {

    private SearchOptionsConfigList optionsList;
    private TwoPlayerPanel gamePanel_;
    
    public PerformanceRunner(TwoPlayerPanel gamePanel, SearchOptionsConfigList optionsList)  {
         this.optionsList = optionsList;
        this.gamePanel_ = gamePanel;
    }

    /**
     * Run the NxN comparison and return the results.
     * @return model with all the results
     */
    public ResultsModel doComparisonRuns() {

        int size = optionsList.size();
        ResultsModel model = new ResultsModel(size);
        GameRunnerDialog runnerDialog = new GameRunnerDialog(gamePanel_);
        runnerDialog.showDialog();
        gamePanel_.init(null);

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                                
                PerformanceResults results = 
                        getResultsForComparison(i, j);
                model.setResults(i, j, results);
            }
        }
        return model;
    }
    
    private PerformanceResults getResultsForComparison(int i, int j) {

        SearchOptionsConfig config1 = optionsList.get(i);
        SearchOptionsConfig config2 = optionsList.get(j);

        TwoPlayerController controller = gamePanel_.get2PlayerController();
        PlayerList players = controller.getPlayers();
        ((TwoPlayerOptions)controller.getOptions()).setShowGameOverDialog(false);
        
        Player player1 = players.getPlayer1();
        Player player2 = players.getPlayer2();
        player1.setHuman(false);
        player2.setHuman(false);
        ((TwoPlayerPlayerOptions)(player1.getOptions())).setSearchOptions(config1.getSearchOptions());
        ((TwoPlayerPlayerOptions)(player2.getOptions())).setSearchOptions(config2.getSearchOptions());

        long startTime = System.currentTimeMillis();
        // should run with each as player1
        gamePanel_.startGame();

        //assert (controller.isDone());
        System.out.println("game is done = " + controller.isDone());
        double strengthOfWin = controller.getStrengthOfWin();
        System.out.println("str of win = " + strengthOfWin);
        int numMoves = controller.getNumMoves();
        long elapsedMillis = System.currentTimeMillis() - startTime;

        boolean isTie = !players.anyPlayerWon();
        boolean player1Won = players.getWinningPlayer() == player1;
        controller.reset();

        return new PerformanceResults(player1Won, isTie, strengthOfWin, numMoves, elapsedMillis);
    }

}
