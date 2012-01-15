// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui;

import com.becker.game.common.plugin.GamePlugin;
import com.becker.game.common.plugin.PluginManager;
import com.becker.game.common.plugin.PluginType;
import com.becker.game.common.ui.menu.GameMenu;

import java.util.List;

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public class GameComparisonMenu extends GameMenu  {

    public GameComparisonMenu(String initialGame) {
        super(initialGame);
    }

    /** @return a list of only the two player games */
    protected List<GamePlugin> getPlugins() {
        return PluginManager.getInstance().getPlugins(PluginType.TWO_PLAYER_GAME);
    }

}
