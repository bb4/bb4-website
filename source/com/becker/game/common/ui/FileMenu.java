package com.becker.game.common.ui;

import com.becker.game.common.GameContext;
import com.becker.ui.GUIUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author becker
 */
public class FileMenu extends JMenu implements ActionListener {

    GameMenu gameMenu_;
    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;


    /**
     * Game file menu constructor
     */
    public FileMenu(GameMenu gameMenu)
    {
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


    private JMenuItem createMenuItem(String name)
    {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

    /**
     * called when the user has selected a different game to play from the game menu
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        JMenuItem item = (JMenuItem) e.getSource();
        if (item == openItem_)  {
            gameMenu_.getGamePanel().openGame();
        }
        else if (item == saveItem_) {
            gameMenu_.getGamePanel().saveGame();
        }
        else if (item == saveImageItem_) {
            GUIUtil.saveSnapshot(gameMenu_.getGamePanel(), GameContext.getHomeDir());
        }
        else if (item == exitItem_) {
            System.exit(0);
        }
        else {
            assert false : "unexpected menuItem = "+ item.getName();
        }
    }

}
