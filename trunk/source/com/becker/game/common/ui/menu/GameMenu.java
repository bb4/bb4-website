/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.ui.menu;

import com.becker.game.common.GameContext;
import com.becker.game.common.plugin.GamePlugin;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.ui.panel.IGamePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public class GameMenu extends AbstractGameMenu implements ActionListener  {

    private JFrame frame_;

    /**
     * Game application constructor
     * @param frame
     * @param initialGame the initially selected game.
     */
    public GameMenu(JFrame frame, String initialGame) {
        super(GameContext.getLabel("GAME"));

        frame_ = frame;

        for (GamePlugin gamePlugin : PluginManager.getInstance().getPlugins()) {
            String gameNameLabel = (gamePlugin.getLabel());
            add(createMenuItem(gameNameLabel));
        }

        showGame(initialGame);
    }

    /**
     * called when the user has selected a different game to play from the game menu
     * @param e
     */
    public void actionPerformed( ActionEvent e ) {
        JMenuItem item = (JMenuItem) e.getSource();

        showGame(PluginManager.getInstance().getPluginFromLabel(item.getText()).getName());
    }

    public JComponent getGameComponent() {
        return (JComponent)gamePanel_;
    }

    public IGamePanel getGamePanel() {
        return gamePanel_;
    }

    /**
     * Show the game panel for the specified game
     * @param gameName name of the game to show in the frame.
     */
    private void showGame(String gameName) {
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
