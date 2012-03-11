// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.common.ui.menu;

import com.becker.common.util.FileUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.ui.panel.IGamePanel;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;

/**
 * Listens for menu changes and updates the UI appropriately.
 *
 * @author Barry Becker
 */
public class GameMenuController
       implements GameMenuListener, FileMenuListener {

    protected JFrame frame_;
    protected IGamePanel gamePanel_;

    /**
     * GConstructor.
     */
    public GameMenuController(JFrame frame) {

        frame_ = frame;
    }

    public void gameChanged(String gameName) {
        showGame(gameName);
    }

    public void openFile() {
        gamePanel_.openGame();
    }

    public void saveFile() {
        gamePanel_.saveGame();
    }

    public void saveImage() {
        JComponent comp = getGameComponent();
        GUIUtil.saveSnapshot(comp, FileUtil.getHomeDir());
    }

    public JComponent getGameComponent() {
        return (JComponent)gamePanel_;
    }

    /**
     * Show the game panel for the specified game
     * @param gameName name of the game to show in the frame.
     */
    protected void showGame(String gameName) {
        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName);

        if (gamePanel_ != null) {
            frame_.getContentPane().remove(getGameComponent());
        }

        gamePanel_ = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        gamePanel_.init(frame_);

        frame_.getContentPane().add(getGameComponent());
        frame_.setTitle(gamePanel_.getTitle());
        frame_.setVisible(true);
    }
}
