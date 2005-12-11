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

package ca.dj.jigo.guesser;

import java.applet.AppletContext;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import ca.dj.jigo.*;
import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;

/**
 * The frame that houses a Goban.
 */
public class GuesserFrame extends GobanFrame
  implements MouseListener
{
  private static Markup CIRCLE_MARKUP = new Markup( Markup.CIRCLE );

  public GuesserFrame( String title, JiGoApplet applet )
  {
    super( title, applet );

    addWindowListener( this );

    // Stay in touch of the Goban's mouse clicks, and mouse drags.
    //
    getGoban().addMouseListener( this );
  }

  protected void initGUI()
  {
    add( getGoban() );
    pack();
    setResizable( false );
  }

  /**
   * Called when the user has clicked to guess where the next move in the
   * game was played.  The click struck one, the mouse went down,
   * hickory-dickory dock!
   */
  public void mousePressed( MouseEvent me )
  {
    Goban goban = getGoban();
    WhiteStone wStone = getJiGoApplet().getWhiteStone();
    BlackStone bStone = getJiGoApplet().getBlackStone();
    Location clickLocation = new Location();

    // Figure out the intersection where the user clicked.
    //
    if( !goban.translateCoord( me.getX(), me.getY(), clickLocation ) )
      return;

    // If the user didn't click on a valid spot, then don't show the next
    // move.  Might want to show a static "Illegal Move" box (it knows if
    // the user has told it to never show again).
    //
    if( !validMove( clickLocation ) )
      return;

    // Get rid of all board mark-up, get the next move.
    //
    goban.removeAllMarks();
    MoveToken nextMove = getNextMove();

    // Nothing to look at here folks, go on home.
    //
    if( nextMove == null )
      return;

    // If it is a pass, the game is over and results should be shown.
    //
    if( nextMove.isPass() )
      gameOver();
    else
    {
      // Otherwise, place the stone on the board, mark that point with
      // a circle, and tabulate the correct/close guesses.
      //
      Location location = new Location( nextMove.getX(), nextMove.getY() );

      if( nextMove.isWhite() )
        goban.placeStone( wStone, location );
      else
        goban.placeStone( bStone, location );

      goban.placeMark( CIRCLE_MARKUP, location );
    }

    goban.forceRepaint();
  }

  /**
   * Returns the next move in the SGF game that was loaded.  If no game has
   * been loaded, or no more moves are available, this method returns null.
   *
   * @return The next move in the game, or null if no game/no next move.
   */
  private MoveToken getNextMove()
  {
    return new BlackMoveToken();
  }

  /**
   * Shows the results to the user, resets the game.
   */
  private void gameOver()
  {
  }

  private void loadGame( String gameName )
    throws IOException
  {
    // Figure out the applet's context, then try to load the given file
    // name.
    //
    URL url = getJiGoApplet().getCodeBase();

    InputStream is = url.openStream();
    try {
        SGFGame game = SGFLoader.load( is );
    }
    catch (SGFException e) {
        e.printStackTrace();
    }

    is.close();
  }

  /**
   * Asks the rules if the given location is a valid place to put a stone.
   */
  private boolean validMove( Location location )
  {
    return true;
  }

  public void setGame( SGFGame game )
  {
    Goban goban = getGoban();
    goban.removeAll();
    goban.forceRepaint();
  }

  public void mouseClicked( MouseEvent me ) { }
  public void mouseReleased( MouseEvent me ) { }
  public void mouseExited( MouseEvent me ) { }
  public void mouseEntered( MouseEvent me ) { }
}

