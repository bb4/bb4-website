// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.sedit;

import com.barrybecker4.ui.file.ExtensionFileFilter;
import com.barrybecker4.ui.file.FileChooserUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A top level menu to allow opening and saving of edited files.
 * @author Barry Becker
 */
public class EditorMenuBar extends JMenuBar implements ActionListener {

    // menu options
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;

    private static JFileChooser chooser = null;

    private static final String EXT = "sed";

    private SimpleEditor editArea;

    /** Constructor */
    EditorMenuBar(SimpleEditor editArea) {
        this.editArea = editArea;
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBorder(BorderFactory.createEtchedBorder());

        openItem = createMenuItem("Open");
        saveItem = createMenuItem("Save");
        exitItem = createMenuItem("Exit");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        add(fileMenu);
    }

    private JMenuItem createMenuItem(String name) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }

    /**
     * The actionPerformed method in this class
     * Opena nd save files.
     */
    public void actionPerformed( ActionEvent e ) {
        JMenuItem item = (JMenuItem) e.getSource();
        if (item == openItem)  {
            openDoc();
        }
        else if (item == saveItem) {
            saveDoc();
        }
        else if (item == exitItem) {
            System.exit(0);
        }
    }

     /**
      * restore a game from a previously saved file (in SGF = Smart Game Format)
      * Derived classes should implement the details of the open
      */
    public void openDoc() {
        JFileChooser chooser = getFileChooser();
        int state = chooser.showOpenDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
            editArea.loadFile(file.getAbsolutePath());
        }
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     */
    public void saveDoc() {
        JFileChooser chooser = getFileChooser();
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION ) {
            // if it does not have the .sgf extension already then add it
            String fPath = file.getAbsolutePath();
            fPath = ExtensionFileFilter.addExtIfNeeded(fPath, EXT);
            editArea.saveFile(fPath);
        }
    }

    private static JFileChooser getFileChooser() {
        if (chooser == null) {
            chooser = FileChooserUtil.getFileChooser();
            chooser.setFileFilter(new ExtensionFileFilter(EXT));
        }
        return chooser;
    }
}
