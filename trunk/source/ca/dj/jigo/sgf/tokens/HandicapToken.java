package ca.dj.jigo.sgf.tokens;

/**
 * User: Barry Becker
 * Date: May 7, 2005
 * Time: 5:28:01 PM
 */
public class HandicapToken extends NumberToken implements InfoToken {

   /**
    * Presume a default handicap of 0
    */
  protected float getDefault() { return 0; }

  public int getHandicap() { return (int)getNumber(); }

}
