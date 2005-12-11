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

package ca.dj.jigo;

import ca.dj.awt.util.ClosableFrame;

import ca.dj.jigo.Goban;
import ca.dj.jigo.JiGoApplet;

/**
 * The frame that houses a Goban.  This is a useful wrapper class which is
 * used by most (if not all) JiGo applets (Designer, Replayer, Guesser,
 * Life and Death, and such).
 */
public abstract class GobanFrame extends ClosableFrame
{
  private JiGoApplet myApplet;
  private Goban myGoban;
  private GobanHighlighter myHighlighter;

  public GobanFrame( String title, JiGoApplet applet )
  {
    super( title );
    setJiGoApplet( applet );
    setGoban( new Goban(
      applet.getBoardSize(), applet.getBGImage(), applet.getWhiteStone() ) );

    if( shouldHighlight() )
      setHighlighter( new GobanHighlighter( getGoban() ) );

    initGUI();
  }

  /**
   * Each subclass must know how to layout its internal components, including
   * the Goban itself.  Each subclass must handle its size, resizing
   * ability, and packing.
   */
  protected abstract void initGUI();

  /**
   * Returns true if highlighting intersections is enabled for this
   * GobanFrame.  Subclasses can override this method in order to disable
   * highlighting (not that there's any reason to, but just in case).
   *
   * @return true - Highlighting is enabled by default.
   */
  public boolean shouldHighlight() { return true; }

  public Goban getGoban() { return myGoban; }
  private void setGoban( Goban goban ) { myGoban = goban; }

  public JiGoApplet getJiGoApplet() { return myApplet; }
  private void setJiGoApplet( JiGoApplet applet ) { myApplet = applet; }

  public GobanHighlighter getHighlighter() { return myHighlighter; }
  private void setHighlighter( GobanHighlighter gl ) { myHighlighter = gl; }
}

