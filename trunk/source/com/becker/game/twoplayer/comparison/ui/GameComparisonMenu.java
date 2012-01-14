// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.plugin.GamePlugin;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.plugin.PluginType;
import com.becker.game.common.ui.menu.AbstractGameMenu;
import com.becker.game.common.ui.menu.GameMenu;
import com.becker.game.common.ui.panel.IGamePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public class GameComparisonMenu extends GameMenu  {


    public GameComparisonMenu(JFrame frame, String initialGame) {
        super(frame, initialGame);
    }

    /** @return a list of only the two player games */
    protected List<GamePlugin> getPlugins() {
        return PluginManager.getInstance().getPlugins(PluginType.TWO_PLAYER_GAME);
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
