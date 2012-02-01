// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.execution;

import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerViewable;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;
import com.becker.game.twoplayer.comparison.model.*;

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

                System.out.println("-loop ------("+i+", "+j+") ----");
                PerformanceResultsPair results = 
                        getResultsForComparison(i, j);
                model.setResults(i, j, results);
            }
        }
        model.normalize();
        return model;
    }
    
    private PerformanceResultsPair getResultsForComparison(int i, int j) {

        TwoPlayerController controller = gamePanel_.get2PlayerController();
        PlayerList players = controller.getPlayers();
        ((TwoPlayerOptions)controller.getOptions()).setShowGameOverDialog(false);
        
        Player player1 = players.getPlayer1();
        Player player2 = players.getPlayer2();
        player1.setHuman(false);
        player2.setHuman(false);
        SearchOptionsConfig config1 = optionsList.get(i);
        SearchOptionsConfig config2 = optionsList.get(j);
        ((TwoPlayerPlayerOptions) (player1.getOptions())).setSearchOptions(config1.getSearchOptions());
        ((TwoPlayerPlayerOptions)(player2.getOptions())).setSearchOptions(config2.getSearchOptions());

        System.out.println("("+i+", "+j+") round 1");
        PerformanceResults p1FirstResults = getResultsForRound(player1, player2);
        System.out.println("("+i+", "+j+") round 2");
        PerformanceResults p2FirstResults = getResultsForRound(player2, player1);
        return new PerformanceResultsPair(p1FirstResults, p2FirstResults);
    }

    private PerformanceResults getResultsForRound(Player player1, Player player2) {
                       
        long startTime = System.currentTimeMillis();
        // should run with each as player1
        TwoPlayerController controller = gamePanel_.get2PlayerController();

        // this is freezing the UI and reporting that the first move is null.
        ((TwoPlayerViewable)controller.getViewer()).showComputerVsComputerGame();
        //gamePanel_.startGame();

        PlayerList players = controller.getPlayers();
        players.set(0, player1);
        players.set(1, player2);
        
        assert (controller.isDone());
        System.out.println("******** game is done = " + controller.isDone() +" ******");
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
