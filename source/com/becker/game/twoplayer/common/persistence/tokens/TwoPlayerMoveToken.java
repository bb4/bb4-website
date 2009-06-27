package com.becker.game.twoplayer.common.persistence.tokens;

import ca.dj.jigo.sgf.Point;

import ca.dj.jigo.sgf.tokens.PlacementToken;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * A generic two player move.
 * The superclass for Player1MoveToken and Player2MoveToken.
 */
public abstract class TwoPlayerMoveToken extends PlacementToken {
    protected Point toPoint = new Point();
    

    /**
     * A token the describes where a players pawn started and where it ended after moving.
     */
    public TwoPlayerMoveToken() { }
  
    /**
     * Parse in the position of the piece.
     */
    @Override
   protected boolean parseContent( StreamTokenizer st )  throws IOException
   {
       boolean parsed = parsePoint( st, toPoint );
       if( st.nextToken() !=  StreamTokenizer.TT_WORD)
           return false;

       return parsed;
   }

  /**
   * Parses a point, sets the X and Y values of the PlacementToken
   * accordingly.  This can be called repeatedly for Tokens which take
   * any number of points (see: PlacementListToken).
   * <P>
   * The first opening '[' must have already been read; thus leaving two
   * letters and a closing ']'.  This method reads everything up to and
   * including ']'.  If a ']' follows immediately after the '[', then this
   * move is considered a pass in FF[4].
   * <P>
   * The letters are from 'a' through 'Z', inclusive, to represent row
   * (or column) 1 through 52, respectfully.
   * <P>
   * Returns:
   *   true - The point was perfectly parsed.
   *   false - The point wasn't perfectly parsed.
   */
    protected boolean parsePoint( StreamTokenizer st, Point pt )  throws IOException
    {
         st.nextToken();
  
         pt.x = ( coordFromChar( st.sval.charAt( 0 ) ) );
         pt.y = ( coordFromChar( st.sval.charAt( 1 ) ) );
    
        return (st.nextToken() == (int)']');
    }
  

  /**
   * Given a token whose value ranges between 'a' through 'z', or 'A'
   * through 'Z', this method returns the appropriate row/column value.  If
   * the token isn't between 'a' and 'z', or 'A' and 'Z', this returns 0;
   */
  protected static int coordFromChar( int ch )
  {
    if( (ch >= 'a') && (ch <= 'z') )
      return ch - 'a' + 1;

    if( (ch >= 'A') && (ch <= 'Z') )
      return ch - 'A' + 1;

    return 0;
  }

   /**
    * Only subclasses (and classes in this package) may get at this class's
    * Point variable.  Everybody else must use get*X() and get*Y().
    */
   protected Point getToPoint() { return toPoint; }

   /**
    * Returns:
    *   The X coordinate of the placement.
    */
    public int getToX() { return toPoint.x; }

    /**
     * Returns:
     *   The Y coordinate of the placement.
     */
    public int getToY() { return toPoint.y; }

}
