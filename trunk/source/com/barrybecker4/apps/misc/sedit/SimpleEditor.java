/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.sedit;

import com.barrybecker4.common.util.Base64Codec;
import com.barrybecker4.ui.components.ScrollingTextArea;
import com.barrybecker4.ui.file.ExtensionFileFilter;
import com.barrybecker4.ui.file.FileChooserUtil;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SimpleEditor extends JFrame implements ActionListener {

    private ScrollingTextArea editArea;

    // menu options
    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem exitItem_;

    private static JFileChooser chooser_ = null;

    private static final String EXT = "sed";
    private static final boolean COMPRESS = true;

    public SimpleEditor() {
        super("Simple Editor");

        GUIUtil.setCustomLookAndFeel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        getRootPane().setJMenuBar(createMenuBar());

        editArea = new ScrollingTextArea(40, 75);
        editArea.setEditable(true);
        editArea.setFont(new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12));

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(editArea, BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        pack();
        setVisible(true);
    }

    /**
     * Add a top level menu to allow opening and saving of edited files.
     */
    private JMenuBar createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBorder(BorderFactory.createEtchedBorder());

        openItem_ =  createMenuItem("Open");
        saveItem_ =  createMenuItem("Save");
        exitItem_ = createMenuItem("Exit");
        fileMenu.add(openItem_);
        fileMenu.add(saveItem_);
        fileMenu.add(exitItem_);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);

        return menuBar;
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
        if (item == openItem_)  {
            openDoc();
        }
        else if (item == saveItem_) {
            saveDoc();
        }
        else if (item == exitItem_) {
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
            loadFile(file.getAbsolutePath());
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
            saveFile( fPath);
        }
    }

    private static JFileChooser getFileChooser() {
        if (chooser_ == null) {
            chooser_ = FileChooserUtil.getFileChooser();
            //chooser_.setCurrentDirectory( new File( GameContext.getHomeDir() ) );
            chooser_.setFileFilter(new ExtensionFileFilter(EXT));
        }
        return chooser_;
    }


    private void loadFile(String fileName) {
        try {
            BufferedReader reader =
                 new BufferedReader( new FileReader( fileName ) );

            StringBuilder bldr = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                bldr.append(line);
            }
            String text = bldr.toString();

            if (COMPRESS)
                text = Base64Codec.decompress(text);

            editArea.setText(text);
            reader.close();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

     private void saveFile(String fileName) {
         try {
             BufferedWriter out =
                     new BufferedWriter(new FileWriter( fileName ));

             String text = editArea.getText();
             if (COMPRESS)
                 text = Base64Codec.compress(text);
             out.write(text);
             out.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    public static void main(String[] args) {
        new SimpleEditor();
    }
}
