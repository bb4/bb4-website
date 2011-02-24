package com.becker.game.common.ui.menu;

import com.becker.game.common.ui.GamePanel;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public abstract class AbstractGameMenu extends JMenu implements ActionListener {

    GamePanel gamePanel_;


    /**
     * Game file menu constructor
     * @param title user visible menu title.
     */
    AbstractGameMenu(String title)
    {
        super(title);
        
        setBorder(BorderFactory.createEtchedBorder());
    }


    /**
     * Create a menu item.
     * @param name name of the menu item. The label.
     * @return the menu item to add.
     */
    JMenuItem createMenuItem(String name)
    {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

}
