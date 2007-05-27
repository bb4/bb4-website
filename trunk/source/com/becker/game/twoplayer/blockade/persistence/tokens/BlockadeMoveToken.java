package com.becker.game.twoplayer.blockade.persistence.tokens;

import ca.dj.jigo.sgf.Point;
import ca.dj.jigo.sgf.tokens.SGFToken;

import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * A generic move.  
 * The superclass forPlayer1MoveToken and Player2MoveToken.
 */
public abstract class BlockadeMoveToken extends SGFToken
{
    private Point fromPoint = new Point();
    private Point toPoint = new Point();
    
    private Point wallPoint1 = null; 
    private Point wallPoint2 = null; 

    /**
     * A token the describes where a players pawn started and where it ended after moving.
     */
    public BlockadeMoveToken() { }
  
    /**
     * Parse in the wall locations.
     */
   protected boolean parseContent( StreamTokenizer st )  throws IOException
   {
       boolean parsed = parsePoint( st, fromPoint );
       if( st.nextToken() != (int)'[' )
           return false;
       parsed = (parsed && parsePoint(st, toPoint));
       if( st.nextToken() !=  StreamTokenizer.TT_WORD)
           return false;
       if (st.sval.equals("wall"))
          parseWall(st);
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
         int token = st.nextToken();  
  
         pt.x = ( coordFromChar( st.sval.charAt( 0 ) ) );
         pt.y = ( coordFromChar( st.sval.charAt( 1 ) ) );
    
        return (st.nextToken() == (int)']');
    }
  
  
  /**
   * Parses a blockade wall, sets the walls 2 points accordingly.  
   * <P>
   * The first opening '[' must have already been read; thus leaving two
   * letters and a closing ']'.  This method reads everything up to and
   * including ']'.  If a ']' follows immediately after the '[', then this
   * move is considered a pass in FF[4].
   * <P>
   * The letters are from 'a' through 'Z', inclusive, to represent row
   * (or column) 1 through 52, respectfully.
   *  
   * Returns:
   *   true - The wall was perfectly parsed.
   *   false - The wall wasn't perfectly parsed.
   */
  protected boolean parseWall( StreamTokenizer st)  throws IOException
  {         
        if( st.nextToken() != (int)'[' )
            return false;
        int token = st.nextToken();

        wallPoint1 = new Point();
        wallPoint1.x =  ( coordFromChar( st.sval.charAt( 0 ) ) );
        wallPoint1.y = ( coordFromChar( st.sval.charAt( 1 ) ) );
        
        boolean parsed =  (st.nextToken() == (int)']' && st.nextToken() == (int)'[');
    
        token = st.nextToken();

        wallPoint2 = new Point();
        wallPoint2.x =  ( coordFromChar( st.sval.charAt( 0 ) ) );
        wallPoint2.y = ( coordFromChar( st.sval.charAt( 1 ) ) );
        
        parsed = parsed && (st.nextToken() == (int)']');
        return parsed;
  }

  /**
   * Given a token whose value ranges between 'a' through 'z', or 'A'
   * through 'Z', this method returns the appropriate row/column value.  If
   * the token isn't between 'a' and 'z', or 'A' and 'Z', this returns 0;
   */
  private static int coordFromChar( int ch )
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
  protected Point getFromPoint() { return fromPoint; }
  protected Point getToPoint() { return toPoint; }

  /**
   * Returns:
   *   The X coordinate of the from position.
   */
    public int getFromX() { return fromPoint.x; }


  /**
   * Returns:
   *   The Y coordinate of the prom position.
   */
    public int getFromY() { return fromPoint.y; }

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
    
    
    public boolean hasWall() {
        return wallPoint1 != null;
    }
    
    public Point getWallPoint1() {
        return wallPoint1;
    }
    
    public Point getWallPoint2() {
        return wallPoint2;
    }

}



