/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package ca.dj.awt.util;

import ca.dj.awt.widgets.OrderedList;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import java.net.*;



/**
 * A simple web-based file and directory browser.  When given a starting
 * directory, this class allows an applet to browse files and directories on
 * the server where it came from.  For example, if the applet was running
 * at "www.joot.com" in the "/jigo-2.0" directory with all SGF files in a
 * subdirectory of JiGo named "sgf-games", then the root directory that must
 * be provided to the FileDialog is:
 *
 * <PRE>
 *   /jigo-2.0/sgf-games
 * </PRE>
 *
 * In this directory must be a file named "directory.dat" whose contents take
 * the following format:
 *
 * <PRE>
 *   +directory1
 *   +directory2
 *   +directory3
 *   filename1.txt
 *   filename2.doc
 *   filename3.gif
 *   filename4.sgf
 * </PRE>
 *
 * On each line is the name of a file that exists in the same directory as
 * the "directory.dat" file.  If a line begins with a + symbol, then it
 * is treated as a directory entry, and it is presumed a directory of that
 * name exists in the same directory as the "directory.dat" file.  Similarly,
 * lines that do not begin with a + symbol must be files that exist in the
 * "current" directory.
 */
public class FileDialog
  extends Dialog
  implements ActionListener, WindowListener
{
  private static final String DEFAULT_TITLE = "Choose a File ...";
  private static final String DIRECTORY_INDEX = "directory.dat";
  private static final String PREVIOUS_DIRECTORY = "<< Back <<";

  private static final int VISIBLE_ITEMS = 12;

  private static final char DIRECTORY_CHAR = '+';

  private Vector myActionListeners = new Vector();

  private String myFileName,
                 myRootDir,
                 myCurrentDir = "",
                 myHostName;
  private URL    myCodeBase;

  private OrderedList myDirectories,
                      myFiles;

  private Button myOKButton = new Button( "OK" ),
                 myCancelButton = new Button( "Cancel" );

  /**
   * Creates a new FileDialog for directory browsing of a remote web site.
   * A default title bar, with appropriate meaning is used.
   *
   * @param frame - The parent frame for this modal dialog box.
   * @param host - The web server host name (DNS or IP address).
   * @param rootDir - Full path on the web server to "directory.dat".
   * @param codeBase - backup location if running standalone
   */
  public FileDialog( Frame frame, String host, String rootDir, URL codeBase )
  {
    this( frame, DEFAULT_TITLE, host, rootDir, codeBase );
  }

  /**
   * Creates a new FileDialog for directory browsing of a remote web site.
   *
   * @param frame - The parent frame for this modal dialog box.
   * @param title - The title bar text.
   * @param host - The web server host name (DNS or IP address).
   * @param rootDir - Full path on the web server to "directory.dat".
   * @param codeBase - backup location if running standalone
   */
  public FileDialog( Frame frame, String title, String host, String rootDir, URL codeBase )
  {
    super( frame, title, true );

    setHostName( host );
    setRootDir( rootDir );
    setCodeBase( codeBase );
    initGUI();
    addWindowListener( this );
  }

  /**
   * Sets up the graphical layout for the components in this dialog box.
   */
  private void initGUI()
  {
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    setLayout( gbl );

    // Put the "Directories" and "Files" lables side by each.
    //
    Label label = new Label( "Directories" );
    gbl.setConstraints( label, gbc );
    add( label );

    gbc.gridwidth = GridBagConstraints.REMAINDER;

    label = new Label( "Files" );
    gbl.setConstraints( label, gbc );
    add( label );

    // Next come the directory and file listings.  Directories on the left,
    // and files on the right (following standard conventions).
    //
    setDirectoryList( new OrderedList( 12, false ) );
    setFileList( new OrderedList( 12, false ) );

    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    gbc.ipadx = 4;
    gbc.ipady = 4;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = GridBagConstraints.RELATIVE;

    gbl.setConstraints( getDirectoryList(), gbc );
    add( getDirectoryList() );

    gbc.gridwidth = GridBagConstraints.REMAINDER;

    gbl.setConstraints( getFileList(), gbc );
    add( getFileList() );

    gbc.insets = new Insets( 4, 4, 4, 4 );
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbc.fill = 2;
    gbc.ipadx = 4;
    gbc.ipady = 4;

    // Lastly, the OK and Cancel buttons.
    //
    gbl.setConstraints( myOKButton, gbc );
    add( myOKButton );
    gbl.setConstraints( myCancelButton, gbc );
    add( myCancelButton );

    myOKButton.addActionListener( this );
    myCancelButton.addActionListener( this );
    getDirectoryList().addActionListener( this );
    getFileList().addActionListener( this );

    pack();
  }

  public void windowActivated( WindowEvent windowevent ) { } 
  public void windowClosed( WindowEvent windowevent ) { } 

  /**
   * User wants to close the window ... that's the same as hitting cancel.
   */
  public void windowClosing( WindowEvent windowevent )
  {
    cancelSelected();
  }

  public void windowDeactivated( WindowEvent windowevent ) { } 
  public void windowDeiconified( WindowEvent windowevent ) { } 
  public void windowIconified( WindowEvent windowevent ) { } 

  /**
   * When the window is opened for the first time, the OK button gets focus.
   */
  public void windowOpened( WindowEvent windowevent )
  {
    updateLists();
    myOKButton.requestFocus();
  }

  /**
   * Called to update the content of both lists.  This presumes that a call
   * to "downloadLists" will replace the content for both lists.  On failure
   * nothing happens.  This should probably throw an IOException on error.
   */
  private void updateLists()
  {
    getFileList().removeAll();
    getDirectoryList().removeAll();

    try
    {
      downloadLists();
    }
    catch( MalformedURLException mue ) {
        mue.printStackTrace();
    }
    catch( IOException io ) {
       io.printStackTrace();
    }
  }

  /**
   * Does the work of downloading (parsing) the "directory.dat" files from
   * the web server.
   */
  private void downloadLists()
    throws MalformedURLException, IOException
  {
    System.out.println("FileDialog: hostName = "+getHostName()+" rootDir="+getRootDir()+" getCurrentDir()="+getCurrentDir()+" getDirectoryIndex()="+getDirectoryIndex());
    
    String prefix = getHostName();
    String suffix = getRootDir() + getCurrentDir() + getDirectoryIndex();
    URL url = null;
    if (prefix==null || prefix.equals("")) {
        prefix = getCodeBase().toString();
        suffix = suffix.substring(1);
        System.out.println("FileDialog: sub suffix= "+suffix);
        url = new URL(prefix + suffix); // remove one of the /'s
    }
    else
        url = new URL( "http", prefix, suffix);
        
    System.out.println("FileDialog: url = "+url.toString());

    OrderedList dirList = getDirectoryList(),
                fileList = getFileList();

    InputStream inStream = url.openStream();

    BufferedReader reader = new BufferedReader(
      new InputStreamReader( inStream ) );

    String fromFile;

    dirList.setVisible( false );
    fileList.setVisible( false );

    while( (fromFile = reader.readLine()) != null ) 
      if( fromFile.length() > 1 )
      {
        if( fromFile.charAt( 0 ) == '+' )
          dirList.add( fromFile.substring( 1 ) );
        else
          fileList.add( fromFile );
      }

    dirList.setVisible( true );
    fileList.setVisible( true );

    // If we're not at the root directory, we need to provide a way for the
    // user to go back a level.
    //
    if( getCurrentDir() != "" )
      dirList.add( getPreviousDirectory(), 0 );

    reader.close();
    inStream.close();
  }

  /**
   * The user has interacted with the dialog box!  See what happened and only
   * if they selected a file name do we want to notify the authorities.
   */
  public void actionPerformed( ActionEvent event )
  {
    String fileName = getFileList().getSelectedItem(),
           dirName = getDirectoryList().getSelectedItem();

    Object source = event.getSource();

    // When the user clicks the OK button, two things can happen:
    //
    // 1) They have selected a file name, we inform listeners and be done.
    // 2) They have selected a directory name, we enter it and continue.
    //
    // Otherwise, if the user double clicked a file name, inform listeners
    // and be done.
    //
    if( (source == myOKButton) || (source == getFileList()) )
    {
      if( fileName != null )
        fileSelected( fileName );
      else if( dirName != null )
        directorySelected( dirName );
    }
    else if( source == getDirectoryList() && (dirName != null) )
      directorySelected( dirName );
    else if( source == myCancelButton )
      cancelSelected();
  }

  /**
   * A file name was selected by the user.  Sound the alarm, inform the
   * guards, and don't forget to notify the observers!
   */
  private void fileSelected( String fileName )
  {
    setVisible( false );

    String host = getHostName();
    String prefix = getRootDir();
    if (host==null || host.equals("")) {
        prefix = getCodeBase().toString() + getRootDir().substring(1);
        System.out.println("FileDialog fileSelected: prefix= "+prefix);
    }
        
    ActionEvent ae = new ActionEvent(
      this, ActionEvent.ACTION_PERFORMED,
      prefix + getCurrentDir() + fileName );

    notifyListeners( ae );
    dispose();
  }

  /**
   * A directory was selected by the user.  Refresh the directory and file
   * lists.
   */
  private void directorySelected( String dirName )
  {
    String currDir = getCurrentDir();
    int lastSlash = -1;

    if( currDir.length() > 1 )
      lastSlash = currDir.lastIndexOf( '/', currDir.length() - 2 );

    if( dirName.equals( getPreviousDirectory() ) )
      setCurrentDir(
        (lastSlash != -1) ? currDir.substring( 0, lastSlash ) + '/' : "" );
    else
      setCurrentDir( currDir + dirName + "/" );

    updateLists();
  }

  /**
   * Oopa!
   */
  private void cancelSelected()
  {
    setVisible( false );
    dispose();
  }

  /**
   * Let everyone know that the particular action has occured.  The
   * parameter will contain the name of the file as its command.
   *
   * @param event - The event fired off to listeners for when the user
   * has selected a particular file.
   */
  private void notifyListeners( ActionEvent event )
  {
    for( Enumeration e = myActionListeners.elements(); e.hasMoreElements(); )
      ((ActionListener)e.nextElement()).actionPerformed( event );
  }

  /**
   * Just in case you want to stop listening for file names (though I
   * haven't a clue why this might be), this is your opportunity.
   */
  public void removeActionListener( ActionListener listener )
  {
    myActionListeners.removeElement( listener );
  }

  /**
   * If you want to know when a file name was selected, listen in using
   * this method.  Multiple listeners are allowed, and there is no check to
   * ensure the same listener isn't added twice!
   *
   * @param listener - An object that wants to know when the user has
   * selected a file name.
   */
  public void addActionListener( ActionListener listener )
  {
    myActionListeners.addElement( listener );
  }

  protected String getDirectoryIndex() { return DIRECTORY_INDEX; }
  protected String getPreviousDirectory() { return PREVIOUS_DIRECTORY; }

  private String getHostName() { return myHostName; }
  private void setHostName( String s ) { myHostName = s; }
  
  private void setCodeBase( URL codeBase ) {myCodeBase = codeBase; }
  private URL getCodeBase() { return myCodeBase; }

  private String getRootDir() { return myRootDir; }
  private void setRootDir( String s ) { myRootDir = s; }

  private String getCurrentDir() { return myCurrentDir; }
  private void setCurrentDir( String s ) { myCurrentDir = s; }

  private void setFileList( OrderedList list ) { myFiles = list; }
  private OrderedList getFileList() { return myFiles; }

  private void setDirectoryList( OrderedList list ) { myDirectories = list; }
  private OrderedList getDirectoryList() { return myDirectories; }
}

