package com.becker.game.common.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GamePlugin;
import com.becker.game.common.PluginManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.*;

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author becker
 */
public class GameMenu extends AbstractGameMenu implements ActionListener  {

    private JFrame frame_;

    /**
     * Game application constructor
     * @param frame
     * @param initialGame the initially selected game.
     */
    public GameMenu(JFrame frame, String initialGame)
    {
        super(GameContext.getLabel("GAME"));

        frame_ = frame;

        Iterator pluginIt = PluginManager.getInstance().getPlugins().iterator();

        while (pluginIt.hasNext()) {
            GamePlugin p = (GamePlugin) pluginIt.next();
            String gameNameLabel = (p.getLabel());
            add(createMenuItem(gameNameLabel));
        }

         showGame(initialGame);
    }

    /**
     * called when the user has selected a different game to play from the game menu
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        JMenuItem item = (JMenuItem) e.getSource();

        showGame(PluginManager.getInstance().getPluginFromLabel(item.getText()).getName());
    }

    public GamePanel  getGamePanel() {
        return gamePanel_;
    }

    /**
     * Show the game panel for the specified game
     * @param gameName name of the game to show in the frame.
     */
    private void showGame(String gameName)
    {
        // this will load the resources for the specified game.
        GameContext.loadGameResources(gameName);

        if (gamePanel_ != null) {
            frame_.getContentPane().remove(gamePanel_);
        }

        gamePanel_ = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        gamePanel_.init(frame_);

        frame_.getContentPane().add(gamePanel_);
        frame_.setTitle(gamePanel_.getTitle());
        frame_.setVisible(true);
    }
}
