// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.execution;

import com.becker.common.math.MathUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.becker.game.twoplayer.common.TwoPlayerViewable;
import com.becker.game.twoplayer.comparison.model.*;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * A worker that will run all the computer vs computer games serially in a separate thread.
 * @author Barry Becker
 */
public class PerformanceWorker implements Runnable {

    private TwoPlayerController controller;
    private SearchOptionsConfigList optionsList;
    private PerformanceRunnerListener listener;
    private ResultsModel model;

    /**
     * Constructor.
     * The listener will be called when all the performance results have been computed and normalized.
     */
    PerformanceWorker(TwoPlayerController controller, SearchOptionsConfigList optionsList,
                      PerformanceRunnerListener listener) {
        this.model = new ResultsModel(optionsList.size());
        this.controller = controller;
        this.optionsList = optionsList;
        this.listener = listener;
        System.out.println("initialized");
    }

    /** Run the process in a separate thread */
    public void run() {

        int size = model.getSize();
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {

                PerformanceResultsPair results =
                        getResultsForComparison(i, j);
                model.setResults(i, j, results);
            }
        }
        model.normalize();
        listener.performanceRunsDone(model);
    }

    /** Get the results for a pair of games where each player gets to play first */
    private PerformanceResultsPair getResultsForComparison(int i, int j) {

        PlayerList players = controller.getPlayers();
        ((TwoPlayerOptions)controller.getOptions()).setShowGameOverDialog(false);

        Player player1 = players.getPlayer1();
        Player player2 = players.getPlayer2();
        player1.setHuman(false);
        player2.setHuman(false);
        SearchOptionsConfig config1 = optionsList.get(i);
        SearchOptionsConfig config2 = optionsList.get(j);
        ((TwoPlayerPlayerOptions)(player1.getOptions())).setSearchOptions(config1.getSearchOptions());
        ((TwoPlayerPlayerOptions)(player2.getOptions())).setSearchOptions(config2.getSearchOptions());

        System.out.println("("+i+", "+j+") round 1  starts:" + config1.getSearchOptions().getSearchStrategyMethod());
        PerformanceResults p1FirstResults = getResultsForRound(player1, player2);
        System.out.println("("+i+", "+j+") round 2  starts:" + config2.getSearchOptions().getSearchStrategyMethod());
        PerformanceResults p2FirstResults = getResultsForRound(player2, player1);

        return new PerformanceResultsPair(p1FirstResults, p2FirstResults);
    }

    /** Get the results for one of the games in the pair */
    private PerformanceResults getResultsForRound(Player player1, Player player2) {

        long startTime = System.currentTimeMillis();
        // this is freezing the UI and reporting that the first move is null.
        ((TwoPlayerViewable)controller.getViewer()).showComputerVsComputerGame();
        //gamePanel_.startGame();

        // make sure the random number sequence is the same for each game to make comparison easier.
        GameContext.setRandomSeed(1);

        PlayerList players = controller.getPlayers();
        players.set(0, player1);
        players.set(1, player2);

        assert (controller.isDone());
        System.out.println("******** game is done = " + controller.isDone() +" ******");
        double strengthOfWin = controller.getStrengthOfWin();
        System.out.println("str of win = " + strengthOfWin);
        int numMoves = controller.getNumMoves();
        BufferedImage finalImage = GUIUtil.getSnapshot((JComponent) controller.getViewer());

        long elapsedMillis = System.currentTimeMillis() - startTime;

        boolean isTie = !players.anyPlayerWon();
        boolean player1Won = players.getWinningPlayer() == player1;
        controller.reset();

        return new PerformanceResults(player1Won, isTie, strengthOfWin, numMoves, elapsedMillis, finalImage);
    }
}
