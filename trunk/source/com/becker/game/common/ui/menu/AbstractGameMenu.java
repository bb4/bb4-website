/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.ui.menu;

import com.becker.game.common.ui.panel.IGamePanel;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public abstract class AbstractGameMenu extends JMenu implements ActionListener {

    protected IGamePanel gamePanel_;

    /**
     * Game file menu constructor
     * @param title user visible menu title.
     */
    public AbstractGameMenu(String title) {
        super(title);
        setBorder(BorderFactory.createEtchedBorder());
    }

    public abstract JComponent getGameComponent();

    /**
     * Create a menu item.
     * @param name name of the menu item. The label.
     * @return the menu item to add.
     */
    protected JMenuItem createMenuItem(String name) {

        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }


}
