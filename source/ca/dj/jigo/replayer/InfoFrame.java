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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextArea;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import ca.dj.jigo.Rules;

import ca.dj.jigo.sgf.SGFGame;

import ca.dj.jigo.sgf.tokens.*;

/**
 * Shows information about a game; keeps up to date on the number of
 * captures by observing the given Rules class.
 */
public class InfoFrame extends Frame implements Observer
{
  /** Text shown when something isn't known about a game ... */
  public final static String UNKNOWN = "Unknown";
  
  /** When the komi is unknown, presume 0.0. */
  public final static String DEFAULT_KOMI = "0.0";

  private TextArea myCommentArea;
  private Label myPlace,
                myDate,
                myEvent,
                myRound,
                myResult,
                myKomi,
                myWhiteCaptures,
                myBlackCaptures;

  /**
   * An InfoFrame shows information about a game of Go loaded from an SGF
   * file.
   *
   * @param game - The SGFGame whose information should be shown.
   * @param rules - The Rules associated with the game that is being replayed.
   */
  public InfoFrame( SGFGame game, Rules rules )
  {
    // The very first thing we have to do is lay out the component, so that
    // when we display information about the game, we're accessing valid
    // widgets.
    //
    initGUI();

    // Now we can enter in information about the game.
    //
    setSGFGame( game );

    // Lastly, we want to know when the number of captured stones changes
    // for either white or black.  (And update the numbers accordingly.)
    //
    rules.addObserver( this );
  }

  /**
   * <PRE>
   * +-----------------------------------+
   * | White vs Black                    |
   * +-----------------------------------+
   * | Place:              Date:         |
   * | Event:              Round:        |
   * | Result:             Komi:         |
   * |                                   |
   * | White Captures:                   |
   * | Black Captures:                   |
   * |                                   |
   * | Comment:                          |
   * | +-------------------------------+ |
   * | |                               | |
   * | |                               | |
   * | |                               | |
   * | |                               | |
   * | |                               | |
   * | +-------------------------------+ |
   * +-----------------------------------+
   * </PRE>
   *
   * The title bar shows the player names (white always first).  The next
   * eight fields are fairly obvious.  The Comment field serves a dual
   * purpose.  The beginning of each game may contain a game comment.  Before
   * the first move is played, the game comment is revealed here.  After the
   * first move is played, the text changes to reveal comments related to
   * the currently played move (including moves in variations).
   */  
  private void initGUI()
  {
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    gbc.gridwidth = 1;
    gbc.ipadx = 4;
    gbc.ipady = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    setLayout( gbl );

    // Add the labels ...
    //
    Label label = new Label( "Place:" );

    gbc.gridheight = 1;
    gbl.setConstraints( label, gbc );
    add( label );
    myPlace = new Label( "" );
    gbl.setConstraints( myPlace, gbc );
    add( myPlace );
    
    label = new Label( "Date:" );
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbl.setConstraints( label, gbc );
    add( label );
    myDate = new Label( "" );
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbl.setConstraints( myDate, gbc );
    add( myDate );
    
    label = new Label( "Event:" );
    gbc.gridwidth = 1;
    gbl.setConstraints( label, gbc );
    add( label );
    myEvent = new Label( "" );
    gbl.setConstraints( myEvent, gbc );
    add( myEvent );
    
    label = new Label( "Round:" );
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbl.setConstraints( label, gbc );
    add( label );
    myRound = new Label( "" );
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbl.setConstraints( myRound, gbc );
    add( myRound );
    
    label = new Label( "Result:" );
    gbc.gridwidth = 1;
    gbl.setConstraints( label, gbc );
    add( label );
    myResult = new Label( "" );
    gbl.setConstraints( myResult, gbc );
    add( myResult );
    
    label = new Label( "Komi:" );
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbl.setConstraints( label, gbc );
    add( label );
    myKomi = new Label( "" );
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbl.setConstraints( myKomi, gbc );
    add( myKomi );

    label = new Label( "White Captures:" );
    gbc.gridwidth = 1;
    gbl.setConstraints( label, gbc );
    add( label );
    myWhiteCaptures = new Label( "" );
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbl.setConstraints( myWhiteCaptures, gbc );
    add( myWhiteCaptures );

    label = new Label( "Black Captures:" );
    gbc.gridwidth = 1;
    gbl.setConstraints( label, gbc );
    add( label );
    myBlackCaptures = new Label( "" );
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbl.setConstraints( myBlackCaptures, gbc );
    add( myBlackCaptures );

    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.gridwidth = 4;
    gbc.gridheight = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.BOTH;

    myCommentArea = new TextArea( 5, 50 );
    myCommentArea.setEditable( false );
    gbl.setConstraints( myCommentArea, gbc );
    add( myCommentArea );

    setSize( 600, 600 );
    pack();
    setResizable( false );
  }

  /**
   * We get notified when the number of captures changes.
   */
  public void update( Observable observedRules, Object unused )
  {
    Rules rules = (Rules)observedRules;
    
    setWhiteDead( rules.getWhiteCaptures() );
    setBlackDead( rules.getBlackCaptures() );
  }
  
  /**
   * Called to change the game we're currently watching.  Since the
   * captures are reset when the game is refreshed, and since we're
   * getting notified of changes to the number of captures by the Rules
   * class itself, we needn't reset the number of captures here, as it
   * will be done automagically in the update method.
   */
  protected void setSGFGame( SGFGame game )
  {
    Enumeration e = game.getInfoTokens();
    
    String whiteName = UNKNOWN,
           blackName = UNKNOWN,
           place     = UNKNOWN,
           date      = UNKNOWN,
           event     = UNKNOWN,
           round     = UNKNOWN,
           komi      = DEFAULT_KOMI,
           result    = UNKNOWN,
           comment   = "",
           system    = UNKNOWN;
  
    while( e.hasMoreElements() )
    {
      InfoToken it = (InfoToken)(e.nextElement());

      if( it instanceof WhiteNameToken )
        whiteName = ((NameToken)it).getName();
      else if( it instanceof BlackNameToken )
        blackName = ((NameToken)it).getName();
      else if( it instanceof ResultToken )
        result = it.toString();
      else if( it instanceof GameCommentToken )
        setComment( ((GameCommentToken)it).getGameComment() );
      else if( it instanceof KomiToken )
        komi = new Float( ((KomiToken)it).getKomi() ).toString();
    }

    // Change the title bar to reflect W vs B player.
    //
    setTitle( whiteName + " vs " + blackName );

    myPlace.setText( place );
    myDate.setText( date );
    
    myEvent.setText( event );
    myRound.setText( round );

    myResult.setText( result );
    myKomi.setText( komi );
  }
  
  /**
   * Updates the comment area with the given text string.
   *
   * @param comment - The new text to display in the comment text area.
   */
  public void setComment( String comment )
  {
    myCommentArea.setText( comment );
  }
  
  private void setWhiteDead( double dead )
  {
    myWhiteCaptures.setText( (new Double( dead )).toString() );
  }
  
  private void setBlackDead( double dead )
  {
    myBlackCaptures.setText( (new Double( dead )).toString() );
  }
}
