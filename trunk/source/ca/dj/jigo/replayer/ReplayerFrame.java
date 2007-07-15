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

package ca.dj.jigo.replayer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import ca.dj.awt.widgets.*;
import ca.dj.awt.util.FileDialog;
import ca.dj.jigo.*;
import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;

/**
 * Houses the Goban that shows the game which is being replayed.
 */
public class ReplayerFrame extends GobanFrame
  implements ActionListener, ComponentListener
{
  private final static String TB_DIRECTORY = "images/toolbar/";
  private final static String TITLE_BAR_TEXT = "Open Game";
  private final static String ROOT_DIR_PARAM = "Root Directory";

  /** Action commands for the action event; numbers faster than strings. */
  private final static int OPEN  = 1,
                           NEXT  = 2,
                           BACK  = 3,
                           START = 4,
                           END   = 5;

  private final static String OPEN_ICON  = "tb-open.gif",
                              START_ICON = "tb-start.gif",
                              BACK_ICON  = "tb-back.gif",
                              NEXT_ICON  = "tb-next.gif",
                              END_ICON   = "tb-end.gif",
                              START_DISABLED_ICON = "tb-start-disabled.gif",
                              END_DISABLED_ICON   = "tb-end-disabled.gif",
                              NEXT_DISABLED_ICON  = "tb-next-disabled.gif",
                              BACK_DISABLED_ICON  = "tb-back-disabled.gif";

  private final static Markup CIRCLE_MARK = new Markup( Markup.CIRCLE );

  private Rules myRules;

  private Vector myMoves = new Vector( 200, 5 );

  /**
   * Represents the current (most recently played) move on the board--it acts
   * as an index into the "myMoves" Vector.
   */
  private int myMoveIndex;

  private Toolbar myToolbar = new Toolbar();

  private ImageButton myStartButton,
                      myNextButton,
                      myBackButton,
                      myEndButton;

  private InfoFrame myInfoFrame;

  /**
   * This variable is used only for optimization purposes.  It's used for
   * two things: placing a move at a given location and placing board markup
   * at that same location.  When a move is played on the board, this
   * variable contains the X and Y values for that move.  It then is used
   * immediately after a call to "playMove" (which is where the values get
   * set in the first place) to put a board markup on the most recently
   * played move.
   */
  private static Location myLocation = new Location();

  public ReplayerFrame( String title, JiGoApplet applet )
  {
    super( title, applet );

    createToolbar();

    addWindowListener( this );
    addComponentListener( this );

    // The rules have to apply to the board in order for stones to be
    // removed when captured.
    //
    setRules( new Rules( getGoban() ) );
  }

  protected void initGUI()
  {
    add( getGoban() );
    pack();
    setResizable( false );
  }

  /**
   * Don't bother highlighting the board.
   */
  public boolean shouldHighlight() { return false; }

  private void createToolbar()
  {
    JiGoApplet applet = getJiGoApplet();
    Toolbar tb = getToolbar();

    tb.setBackground( Color.lightGray );

    tb.addButton( new ImageButton(
      applet.loadImage( TB_DIRECTORY + OPEN_ICON ),
      Integer.toString( OPEN ) ) );

    myStartButton = new ImageButton(
      applet.loadImage( TB_DIRECTORY + START_ICON ),
      applet.loadImage( TB_DIRECTORY + START_DISABLED_ICON ),
      Integer.toString( START ) );
    myStartButton.setEnabled( false );
    tb.addButton( myStartButton );

    myBackButton = new ImageButton(
      applet.loadImage( TB_DIRECTORY + BACK_ICON ),
      applet.loadImage( TB_DIRECTORY + BACK_DISABLED_ICON ),
      Integer.toString( BACK ) );
    myBackButton.setEnabled( false );
    tb.addButton( myBackButton );

    myNextButton = new ImageButton(
      applet.loadImage( TB_DIRECTORY + NEXT_ICON ),
      applet.loadImage( TB_DIRECTORY + NEXT_DISABLED_ICON ),
      Integer.toString( NEXT ) );
    myNextButton.setEnabled( false );
    tb.addButton( myNextButton );

    myEndButton = new ImageButton(
      applet.loadImage( TB_DIRECTORY + END_ICON ),
      applet.loadImage( TB_DIRECTORY + END_DISABLED_ICON ),
      Integer.toString( END ) );
    myEndButton.setEnabled( false );
    tb.addButton( myEndButton );

    tb.addActionListener( this );
  }

  private void showDefaultCursor()
  {
    Cursor cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    getToolbar().setCursor( cursor );
    setCursor( cursor );
  }

  private void showWaitCursor()
  {
    Cursor cursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    getToolbar().setCursor( cursor );
    setCursor( cursor );
  }

  public void actionPerformed( ActionEvent event )
  {
    if( event.getSource() instanceof ImageButton )
      switch( Integer.parseInt( event.getActionCommand() ) )
      {
        case OPEN : openFile();     break;
        case START: showStart();    break;
        case BACK : showPrevMove(); break;
        case NEXT : showNextMove(); break;
        case END  : showEnd();      break;
      }
    else
      loadGame( event.getActionCommand() );
  }

  /**
   * Loads up the SGF file, given a particular file name.  The file must be
   * on the web somewhere ...
   */
  private void loadGame( String fileName )
  {
    showWaitCursor();

    try
    {
      URL url = null;
      if (getHostName().length()>0)
          url = new URL( "http", getHostName(), fileName );
      else 
          url = new URL( fileName ); // running locally
      SGFLoader loader = new SGFLoader();
      setSGFGame( loader.load( url.openStream() ) );

      // Clear the current game only after a successful load.
      //
      getGoban().removeAll();
      getGoban().forceRepaint();
    }
    catch( MalformedURLException mue )
    {
      couldNotLoadGame( fileName );
    }
    catch( IOException ioe )
    {
      couldNotLoadGame( fileName );
    }
    catch( SGFException sgfe )
    {
      couldNotLoadGame( fileName );
    }

    showDefaultCursor();

    nextMove( true );
    prevMove( false );
  }

  private void couldNotLoadGame( String fileName )
  {
    System.out.println( "Could not load: " + fileName );
  }

  private void openFile()
  {
    JiGoApplet applet = getJiGoApplet();
    System.out.println("host="+applet.getCodeBase().getHost()+" ROOT_DIR_PARAM="+applet.getParameter( ROOT_DIR_PARAM ) );
    FileDialog fd = new FileDialog(
      this,
      applet.getParameter( TITLE_BAR_TEXT ),
      applet.getCodeBase().getHost(),
      applet.getParameter( ROOT_DIR_PARAM ),
      applet.getCodeBase()
    );

    fd.addActionListener( this );
    fd.setSize( 320, 270 );
    fd.setVisible( true );
  }

  /** 
   * Plasters the given move on the goban.  MoveTokens contain the X and Y
   * location of the move as well as in indication of their colour.
   *
   * @param mt - A move that should be placed on the board.
   */
  private void playMove( MoveToken mt )
  {
    Goban goban = getGoban();

    // Cheat a little bit by directly accessing the member variable, since
    // the "placeStone" method requires a Location object to indicate where
    // a stone should be placed.
    //
    myLocation.x = mt.getX() - 1;
    myLocation.y = mt.getY() - 1;

    if( mt.isWhite() )
      goban.placeStone( getJiGoApplet().getWhiteStone(), myLocation );
    else
      goban.placeStone( getJiGoApplet().getBlackStone(), myLocation );
  }
  
  private void showNextMove()
  {
    // Play the next move in the game, if any remain.  If none remain,
    // disable the forward button.
    //
    MoveToken mt = getNextMove();

    if( (mt == null) || (mt.isPass()) )
    {
      nextMove( false );
      return;
    }

    playMove( mt );
    prevMove( true );

    Goban goban = getGoban();
    goban.removeAllMarks();
    goban.placeMark( CIRCLE_MARK, myLocation );
    goban.forceRepaint();
  }
  
  /**
   * Resets the game to just before the first move and then shows the
   * empty Goban.
   */
  private void showStart()
  {
    resetGame();
    getGoban().forceRepaint();
  }
  
  /**
   * Replays the game to the end and then shows the finished product.
   */
  private void showEnd()
  {
    int maxMoves = getMoves().size();

    while( getMoveIndex() != maxMoves )
      playMove( getNextMove() );

    getGoban().removeAllMarks();
    getGoban().placeMark( CIRCLE_MARK, myLocation );
    getGoban().forceRepaint();

    prevMove( true );
    nextMove( false );
  }

  private void showPrevMove()
  {
    Goban goban = getGoban();
    int prevMoveIndex = getMoveIndex() - 1;

    resetGame();

    if( prevMoveIndex == -1 )
    {
      goban.forceRepaint();
      return;
    }

    prevMove( true );

    while( getMoveIndex() != prevMoveIndex )
      playMove( getNextMove() );

    goban.placeMark( CIRCLE_MARK, myLocation );
    goban.forceRepaint();
  }

  private void resetGame()
  {
    getGoban().removeAll();
    setMoveIndex( 0 );
    nextMove( true );
    prevMove( false );
    getRules().resetCaptures();
  }

  /**
   * Helper method.  Used to attach the toolbar frame to the bottom of
   * the frame.
   */
  private void attachFrames()
  {
    Rectangle rect = getBounds();
    Toolbar tb = getToolbar();
    tb.setLocation( rect.x, rect.y - tb.getBounds().height - 1 );
    tb.setVisible( true );
    tb.toFront();
    
    InfoFrame infoFrame = getInfoFrame();
    
    if( infoFrame != null )
    {
      infoFrame.setLocation( rect.x + rect.width + 1, rect.y - 1 );
      infoFrame.setVisible( true );
      infoFrame.toFront();
    }
  }

  /**
   * Responisible for closing all related windows.  Called when the user
   * request that things shut down.  Should probably tell the applet to
   * stop.
   */
  public void windowClosing( WindowEvent we )
  {
    Toolbar tb = getToolbar();
    tb.setVisible( false );
    tb.dispose();
    
    InfoFrame infoFrame = getInfoFrame();

    if( infoFrame != null )
    {
      infoFrame.setVisible( false );
      infoFrame.dispose();
    }

    super.windowClosing( we );
  }

  public void windowIconified( WindowEvent we )
  {
    getToolbar().setVisible( false );
    getInfoFrame().setVisible( false );
  }

  public void windowActivated( WindowEvent we )
  {
    attachFrames();
  }

  public void componentMoved( ComponentEvent compEvent )
  {
    attachFrames();
  }

  public void componentResized( ComponentEvent compEvent )
  {
    attachFrames();
  }

  public void componentHidden( ComponentEvent compEvent )
  {
    getToolbar().setVisible( false );
  }

  public void componentShown( ComponentEvent compEvent )
  {
    attachFrames();
  }

  /**
   * Given an SGFTree and a place to store the moves of a game, this
   * method weeds out all the moves from the given SGFTree into a single
   * Vector of moves.  Variations are discarded.
   *
   * @param tree - The SGFTree containing an SGF variation tree.
   * @param moveStore - The place to store the moves for the game's main
   * variation.
   */
  private void extractMoves( SGFTree tree, Vector moveStore )
  {
    Enumeration trees = tree.getTrees(),
                leaves = tree.getLeaves(),
                tokens;

    while( (leaves != null) && leaves.hasMoreElements() )
    {
      SGFToken token;
      tokens = ((SGFLeaf)(leaves.nextElement())).getTokens();
      
      // true = Haven't found a move token.
      //
      boolean notFound = true;

      // While a move token hasn't been found, and there are more tokens to
      // examine ... try and find a move token in this tree's leaves to add
      // to the collective of moves (moveStore).
      //
      while( (tokens != null) && tokens.hasMoreElements() && notFound )
      {
        if( (token = (SGFToken)(tokens.nextElement())) instanceof MoveToken )
        {
          moveStore.addElement( (MoveToken)token );
          notFound = false;
        }
      }
    }

    // If there are variations, dive through the first variation, which is
    // the entire game, without extraneous variations.
    //
    if( (trees != null) && trees.hasMoreElements() )
      extractMoves( (SGFTree)(trees.nextElement()), moveStore );
  }

  private void setSGFGame( SGFGame game )
  {
    getMoves().setSize( 0 );
    extractMoves( game.getTree(), getMoves() );
    setMoveIndex( 0 );
      
    showGameInfo( game );
  }
  
  private MoveToken getNextMove()
  {
    int moveIndex = getMoveIndex();

    // If we're out of moves, then return null.
    //
    if( moveIndex == getMoves().size() )
      return null;

    setMoveIndex( moveIndex + 1 );

    return (MoveToken)(getMoves().elementAt( moveIndex ));
  }

  private void showGameInfo( SGFGame game )
  {
    setInfoFrame( new InfoFrame( game, getRules() ) );
    getInfoFrame().setVisible( true );
  }

  /**
   * Enables, or disables, the next button to indicate if there are more
   * moves left to replay in the game.
   */
  private void nextMove( boolean enable )
  {
    myNextButton.setEnabled( enable );
    myEndButton.setEnabled( enable );
  }

  /**
   * Enables, or disables, the previous button to indicate if the game is at
   * the very first move.
   */
  private void prevMove( boolean enable )
  {
    myStartButton.setEnabled( enable );
    myBackButton.setEnabled( enable );
  }
  
  private Vector getMoves() { return myMoves; }

  private void setMoveIndex( int i ) { myMoveIndex = i; }
  private int getMoveIndex() { return myMoveIndex; }

  private Rules getRules() { return myRules; }
  private void setRules( Rules rules ) { myRules = rules; }

  private void setToolbar( Toolbar tb ) { myToolbar = tb; }
  private Toolbar getToolbar() { return myToolbar; }

  private void setInfoFrame( InfoFrame infoFrame ) { myInfoFrame = infoFrame; }
  private InfoFrame getInfoFrame() { return myInfoFrame; }

  private String getHostName()
  {
    return getJiGoApplet().getCodeBase().getHost();
  }
}

