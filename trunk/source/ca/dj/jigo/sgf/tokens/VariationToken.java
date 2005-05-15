package ca.dj.jigo.sgf.tokens;

/**
 * User: Barry Becker
 * Date: May 8, 2005
 * Time: 5:43:34 AM
 */
public class VariationToken extends NumberToken implements InfoToken
{
  public VariationToken() { }

  /**
   * Presume a default komi of 0.5 points.
   */
  protected float getDefault() { return (float)0; }

  public int getStyle() { return (int) getNumber(); }
}
