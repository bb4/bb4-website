/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.ui.menu;

import com.becker.common.util.FileUtil;
import com.becker.game.common.GameContext;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public class FileMenu extends JMenu implements ActionListener {

    private GameMenu gameMenu_;
    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;


    /**
     * Game file menu constructor
     */
    public FileMenu(GameMenu gameMenu) {

        super(GameContext.getLabel("FILE"));
        gameMenu_ = gameMenu;
        setBorder(BorderFactory.createEtchedBorder());

        openItem_ =  createMenuItem(GameContext.getLabel("OPEN"));
        saveItem_ =  createMenuItem(GameContext.getLabel("SAVE"));
        saveImageItem_ =  createMenuItem(GameContext.getLabel("SAVE_IMAGE"));
        exitItem_ = createMenuItem("Exit");
        add(openItem_);
        add(saveItem_);
        add(saveImageItem_);
        add(exitItem_);
    }


    private JMenuItem createMenuItem(String name) {

        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

    /**
     * called when the user has selected a different game to play from the game menu
     * @param e
     */
    public void actionPerformed( ActionEvent e )  {
        JMenuItem item = (JMenuItem) e.getSource();
        if (item == openItem_)  {
            gameMenu_.open();
        }
        else if (item == saveItem_) {
            gameMenu_.save();
        }
        else if (item == saveImageItem_) {
            GUIUtil.saveSnapshot(gameMenu_.getGameComponent(), FileUtil.getHomeDir());
        }
        else if (item == exitItem_) {
            System.exit(0);
        }
        else {
            assert false : "unexpected menuItem = "+ item.getName();
        }
    }

}
