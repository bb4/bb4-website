
package ca.dj.jigo.examples;

import java.applet.Applet;

import ca.dj.jigo.Goban;

/**
 * The purpose of this class is to demonstrate how the JiGo API can be
 * used to display a Goban in a panel on the web browser.
 */
public class JigoApplet extends Applet
{
  public void init()
  {
    add( new Goban() );
  }
}

