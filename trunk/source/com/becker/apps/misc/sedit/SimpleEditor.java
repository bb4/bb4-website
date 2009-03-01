package com.becker.apps.misc.sedit;

import com.becker.common.Base64Codec;
import com.becker.ui.GUIUtil;
import com.becker.ui.filefilter.ExtensionFileFilter;
import com.becker.ui.filefilter.TextFileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import javax.swing.*;


public class SimpleEditor extends JFrame implements ActionListener {
 
  
    private JTextArea editArea;
    private String newline = "\n";
    
    // menu options
    private JMenuItem openItem_;
    private JMenuItem saveItem_;
    private JMenuItem saveImageItem_;
    private JMenuItem exitItem_;
    
    private static JFileChooser chooser_ = null;
    
    private static final String EXT = "sed";
    private static final boolean COMPRESS = true;

    public SimpleEditor() {
        super("Simple Editor");
    
        
        GUIUtil.setCustomLookAndFeel();
           
       addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });        
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       
        
        // Create the menu.
        getRootPane().setJMenuBar(createMenuBar());
  
        editArea = new JTextArea(40, 75);
        editArea.setMargin(new Insets(5, 5, 5, 5));
        editArea.setEditable(true);
        editArea.setFont(new Font("Helvetica", Font.PLAIN, 12));

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());   
        contentPane.add(new JScrollPane(editArea), BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        
         pack();
         setVisible(true);                     
    }

    
    /**
     * Add a top level menu to allow opening and saving of edited files.
     */
    private JMenuBar createMenuBar()
    {
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
    
    private JMenuItem createMenuItem(String name)
    {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);
        return item;
    }
        
    /**
     * The actionPerformed method in this class
     * Opena nd save files.
     */ 
    public void actionPerformed( ActionEvent e )
    {
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
    public void openDoc()
    {
        JFileChooser chooser = getFileChooser();
        int state = chooser.showOpenDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION )  {
            //lastDirectoryAccessed_ = file.getAbsolutePath();
            loadFile(file.getAbsolutePath());       
        }
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     */
    public void saveDoc() 
    {
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
            chooser_ = GUIUtil.getFileChooser();
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
        JFrame frame = new SimpleEditor();        
    }
}
