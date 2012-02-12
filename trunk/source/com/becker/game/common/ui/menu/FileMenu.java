/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.common.ui.menu;

import com.becker.game.common.GameContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
public class FileMenu extends JMenu implements ActionListener {

    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;
    private FileMenuListener listener;


    /**
     * Game file menu constructor
     */
    public FileMenu(FileMenuListener listener) {

        super(GameContext.getLabel("FILE"));
        setBorder(BorderFactory.createEtchedBorder());

        this.listener = listener;
        openItem_ = createMenuItem(GameContext.getLabel("OPEN"));
        saveItem_ = createMenuItem(GameContext.getLabel("SAVE"));
        saveImageItem_ = createMenuItem(GameContext.getLabel("SAVE_IMAGE"));
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
            listener.openFile();
        }
        else if (item == saveItem_) {
            listener.saveFile();
        }
        else if (item == saveImageItem_) {
            listener.saveImage();
        }
        else if (item == exitItem_) {
            System.exit(0);
        }
        else {
            assert false : "unexpected menuItem = "+ item.getName();
        }
    }

}
