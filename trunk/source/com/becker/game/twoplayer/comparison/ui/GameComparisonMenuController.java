// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.ui.menu.FileMenuListener;
import com.becker.game.common.ui.menu.GameMenuController;
import com.becker.game.common.ui.menu.GameMenuListener;
import com.becker.game.common.ui.panel.IGamePanel;

import javax.swing.*;

/**
 * Listens for menu changes and updates the UI appropriately.
 *
 * @author Barry Becker
 */
public class GameComparisonMenuController extends GameMenuController {

    /**
     * GConstructor.
     */
    public GameComparisonMenuController(JFrame frame) {

        super(frame);
    }


    /**
     * Show the game panel for the specified game
     * @param gameName name of the game to show in the frame.
     */
    protected void showGame(String gameName) {
        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName);

        gamePanel_ = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        //gamePanel_.init(frame_);

        //frame_.getContentPane().add(getGameComponent());
        frame_.setTitle(gamePanel_.getTitle());
        //frame_.setVisible(true);
    }
}
