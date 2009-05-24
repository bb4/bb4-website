package com.becker.game.common.ui;

import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author becker
 */
public abstract class AbstractGameMenu extends JMenu implements ActionListener {

    protected GamePanel gamePanel_;


    /**
     * Game file menu constructor
     * @param title user visible menu title.
     * @param gamePanel the game panel
     */
    public AbstractGameMenu(String title, GamePanel gamePanel)
    {
        super(title);
        gamePanel_ = gamePanel;

        setBorder(BorderFactory.createEtchedBorder());
    }


    /**
     * Create a menu item.
     * @param name name of the menu item. The label.
     * @return the menu item to add.
     */
    protected JMenuItem createMenuItem(String name)
    {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

}
