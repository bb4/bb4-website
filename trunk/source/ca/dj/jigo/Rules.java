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

import java.util.Observable;
import java.util.Observer;

/**
 * The rules that govern the behaviour of a particular Goban.  The Super Ko
 * rule is not present, however it is entirely possible to create a subclass
 * which implements the rule.
 * <P>
 * By observing the Rules class, you can be notified when the number of
 * captures for either black or white has changed.
 */
public class Rules extends Observable implements Observer
{
  private final static byte UNEXAMINED = 0;
  private final static byte LIBERTY    = 1;
  private final static byte STONE      = 2;

  /**
   * A map for stones and liberties.  Used for the recursive liberty count
   * method.  Also used for removing a group of stones.
   */
  private byte myStoneMap[][];

  private Goban myGoban;

  private boolean hasIllegalKo = true,
                  hasSelfCapture = false;

  /**
   * Since we disregard Super Ko, this variable is all that's needed to keep
   * track of the previously played ko.
   */
  private Location myKoPlay;

  /**
   * Only used when informing the Goban that the stone at a given location
   * should be removed from the board.
   */
  private Location myHelperLocation = new Location( 0, 0 );

  /**
   * Default komi is 0.5 points (no komi).
   */
  private double myKomi = 0.5;

  /**
   * The rules of Go dictate how to count the score at the end of the game.
   * Thus, even though it isn't entirely intuitive, the application must be
   * able to query the number of black and white captures to this point.
   * A double is used because white is normally given a 0.5 point komi.
   */
  private double myWhiteCaptures = myKomi,
                myBlackCaptures = 0.0;

  /**
   * Creates a new instance of rules for a game of Go, given the Goban to
   * which the rules apply.
   */
  public Rules( Goban goban )
  {
    setGoban( goban );

    // The stone map must be created so we can count liberties.  Rather
    // than having it initialized each time we count, while we examine the
    // array we reset its values to UNEXAMINED as we progress.
    //
    initStoneMap( goban.getBoardSize() );
  }

  /**
   * Called when the given Goban has had a stone placed on the board.
   * The location is also given.  The first parameter is ignored, as the
   * Goban this Rule set is watching was given at instantiation.  Probably
   * not a good idea to ever call this method directly.
   */
  public void update( Observable goban, Object stoneLocation )
  {
    // Examine the Stone at on the goban at the given location.
    //
    Location location = (Location)stoneLocation;
    Stone placed = getGoban().getStone( location );

    // If the number of liberties for an orthagonal group of opposite colour
    // to the stone played is zero, then we remove the group at that location.
    //
    // Search Up.  Y = -1 (relative to original position)
    //
    location.y--;
    tryCullGroup( placed, location );

    // Search Down.  Y = +1 (relative to original position)
    //
    location.y += 2;
    tryCullGroup( placed, location );

    // Search Left.
    //   Y = 0 (relative to original position)
    //   X = -1 (relative to original position)
    //
    location.y--;
    location.x--;
    tryCullGroup( placed, location );

    // Search Right.  X = +1 (relative to original position)
    //
    location.x += 2;
    tryCullGroup( placed, location );

    // Put the X value back to where it belongs, just in case other code
    // uses the X & Y values in "stoneLocation" (which is quite likely).
    //
    location.x--;
  }

  /**
   * Doesn't actually nuke the group without checking first.  If there
   * happens to be a stone at the given location, then the number of liberties
   * for the group of stones which contains the stone at that location is
   * counted.  If the number of liberties is zero, then the group at the
   * given location is removed.
   * <P>
   * For the record, this proves Yoda wrong.  "There is no try, there is either
   * do or do not."  We try.  ;-)
   *
   * @param placed - The stone that was just placed on the board.
   * @param toCheck - An intersection orthagonal to the stone that was placed. 
   */
  private void tryCullGroup( Stone placed, Location toCheck )
  {
    if( isInvalidCoord( toCheck.x, toCheck.y ) )
      return;

    Stone compare = getGoban().getStone( toCheck );

    // If the stones are opposite in colour and the liberties at the given
    // location (orthagonal to the "placed" stone) have all been squelched,
    // then the group may be safely removed from the board.
    //
    if( (compare != null) && (placed.isWhite() != compare.isWhite()) )
    {
      int dead = 0;

      resetStoneMap();

      // Once we count the liberties, we have a map of the stones.  If
      // the number of liberties is zippo, then use that same stone map
      // to remove the stones from the goban.
      //
      if( getLibertyCount( toCheck ) == 0 )
        dead = removeStones();
        
      /* Cart-Master: "Bring out your dead! Bring out your dead!"
       * Man: "Here's one."
       * Cart-Master: "Ninepence."
       * Old Man: "I'm not dead!"
       * Cart-Master: "What?"
       * Man: "Nothing. Here's your ninepence ..."
       * Old Man: "I'm not dead!"
       * Cart-Master: "Ere! 'E says 'e's not dead!"
       * Man: "Yes he is."
       * Old Man: "I'm not!"
       * Cart-Master: "'E isn't?"
       * Man: "Well ... he will be soon--he's very ill."
       * Old Man: "I'm getting better!"
       * Man: "No you're not, you'll be stone dead in a moment."
       * Cart-Master: "I can't take 'im like that! It's against regulations!"
       * Old Man: "I don't want to go on the cart ..."
       * Man: "Stop being such a baby."
       * Cart-Master: "I can't take 'im ..."
       * Old Man: "I feel fine!"
       * Man: "Well, do us a favour ..."
       * Cart-Master: "I can't!"
       * Man: "Can you hang around a couple of minutes?  He won't be long."
       * Cart-Master: "No, gotta' get to Robinson's, they lost nine today."
       * Man: "Well, when's your next round?"
       * Cart-Master: "Thursday."
       * Old Man: "I think I'll go for a walk."
       * Man (to Old Man): "You're not fooling anyone, you know."
       * Man (to Cart-Master): "Look, isn't there something you can do?"
       * Old Man: "I feel happy! I feel happy!"
       * Cart-Master gives the Old Man a blow to the head with a wooden
       * spoon; the Old Man goes limp.
       * Man throws Old Man into cart: "Ah, thanks very much."
       * Cart-Master: "Not at all. See you on Thursday!"
       */
      if( dead > 0 )
      {
        if( compare.isWhite() )
          setWhiteCaptures( getWhiteCaptures() + dead );
        else
          setBlackCaptures( getBlackCaptures() + dead );
      }
    }
  }

  /**
   * Removes dead stones according to the stones marked in the stone map.
   * Returns the number of stones removed. 
   * <P>
   * This method relies on the fact that a stone map has already been
   * created (by a call to makeStoneMap).  Since a group must be removed if
   * its liberties reach zero, and there's no easy way to tell if a group has
   * no more liberties outside of actually counting them (via a stone map),
   * this method indirectly relies on the stone map created by a call to
   * getLibertyCount.
   *
   * @return The number of stones removed.
   */
  private int removeStones()
  {
    Goban goban = getGoban();
    int length = myStoneMap.length - 1,
        dead = 0;

    for( int x = length; x >= 0; x-- )
      for( int y = length; y >= 0; y-- )
      {
        if( myStoneMap[ x ][ y ] == STONE )
        {
          myHelperLocation.x = x;
          myHelperLocation.y = y;
          goban.removeStone( myHelperLocation );
          dead++;
        }
      }
      
    return dead;
  }

  /**
   * Returns a true or false, depending on whether the given stone can be
   * legally played at the given location.
   *
   * @param stone -  The stone (and its colour) that wants to be played.
   * @param location - The intersection to check for play legality.
   * @return true - The location is a valid point for the given stone.
   * @return false - The location is not a valid point for the given stone.
   */
  public boolean canPlayStone( Stone stone, Location location )
  {
    // Simple check first: if the location is taken, then the stone may not
    // be played.
    // 
    if( getGoban().hasStone( location ) )
      return false;

    // Check against the Ko rule, make sure the move is a legal Ko move.  Ko
    // takes precidence over self-capture.  If it is a Ko play, then make
    // certain it's the correct colour's turn to play the Ko.
    //
    if( isKo( stone, location ) )
      return canPlayKo( stone, location );

    // If the placement of the is self-captured, then return true only if the
    // self-capture rule is on.
    //
    if( isSelfCapture( stone, location ) )
      return canSelfCapture();

    // A stone can be played at the given location without breaking the
    // rules, so we return true.
    //
    return true;
  }

  /**
   * Returns true if the given stone played at the given location results in
   * a Ko capture.
   *
   * @return true - The location and stone makes for the Ko fight.
   */
  private boolean isKo( Stone stone, Location location )
  {
    return false;
  }

  /**
   * Returns true if the given stone at the given location is allowed to
   * continue the Ko.
   *
   * @return false - The stone may not play played at the Ko, yet.
   */
  private boolean canPlayKo( Stone stone, Location location )
  {
    return true;
  }

  /**
   * Returns true of playing the given stone at the given location results in
   * self-capture.
   *
   * @return true - Playing the stone at the location results in self-capture.
   */
  private boolean isSelfCapture( Stone stone, Location location )
  {
    return false;
  }

  /**
   * Returns true if illegal Ko captures are allowed.
   *
   * @return true - Illegal Ko captures are allowed.
   */
  public boolean canIllegalKo() { return hasIllegalKo; }

  /**
   * Toggles the state of illegal ko captures.
   */
  public void toggleIllegalKo() { hasIllegalKo = !hasIllegalKo; }

  /**
   * Returns true if self captures are allowed.
   *
   * @return true - Self captures are allowed.
   */
  public boolean canSelfCapture() { return hasSelfCapture; }

  /**
   * Toggles the state of self captures.
   */
  public void toggleSelfCapture() { hasSelfCapture = !hasSelfCapture; }

  /**
   * Counts the number of liberties for a group of stones at a given location.
   * The group can be a group of one stone.  Returns 0 if there are no
   * stones at the location.  In a real game of Go, 0 liberties means something
   * has died, thus can never represent the liberty count for a group of
   * stones still present on the Goban.  Since this Rules class is meant
   * to govern a game of Go, this presumption is kosher.
   * <P>
   * This method creates a stone mapping for the group of stones at the
   * given location.  The stone mapping is valid after a call to this method.
   * Before calling this method a second time, be sure to clear the
   * stone map manually.  This is because the map created by this method
   * is used by the removeStones method for efficiency's sake (no sense
   * mapping the same group of stones twice in a row).
   *
   * @return The number of liberties for the group of stones at the given
   * location.
   */
  private int getLibertyCount( Location location )
  {
    Stone stone = getGoban().getStone( location );

    // Of course, we can't count liberties for stones that aren't present.
    // (Well, we could as the algorithm yields one [1], but this isn't
    // correct behaviour, so ...)
    //
    if( stone == null )
      return 0;

    // Map the stones and their liberties for the group of stones at the
    // given location.  After we make the map, we do not clear it!  This
    // is critical because removeStones relies on the map made from a call
    // to this method.  So even though "countStoneMap" could reset the
    // map as it goes, we don't.
    //
    makeStoneMap( stone, location.x, location.y );

    // After the mapping of the group is complete, iterate over the stone
    // map to tally its liberties.  The stone map remains intact after calling
    // this method so that a 
    //
    return countStoneMap();
  }

  /**
   * Creates a new map for counting liberties of groups.  Since counting
   * liberties is performed recursively, a map of is required to track
   * liberties (and stones) that have already been counted for a group.
   *
   * @param size - The size of the board that needs to be mapped.
   */
  private void initStoneMap( int size )
  {
    // Values are initialized to 0, by default (language spec.).
    //
    myStoneMap = new byte[ size ][ size ];
  }
  
  /**
   * Resets the stone map so that all values present are set to
   * UNEXAMINED -- indicating each spot on the board has yet to receive
   * a value of LIBERTY or STONE.
   */
  private void resetStoneMap()
  {
    initStoneMap( myStoneMap.length );
  }

  private final boolean isInvalidCoord( int x, int y )
  {
    int size = getGoban().getBoardSize();
    return (x < 0) || (y < 0) || (x >= size) || (y >= size);
  }

  /**
   * Recursive method used to count the liberties of a group of stones
   * at a given location.  The stoneColour must not be null.  The x and y
   * values can be invalid -- e.g, (-42, 367).  At the moment, only
   * getLibertyCount calls this method.  (removeStones relies on this
   * method being called via getLibertyCount.)
   * <P>
   * Also indicates the locations of all the stones belonging to the
   * group of the stone at the given coordinates.
   *
   * @param stoneColour - The colour of stones to count liberties
   * @param location - The location 
   */
  private void makeStoneMap( Stone stoneColour, int x, int y )
  {
    // Don't go knocking where stone's aren't rocking.
    //
    if( isInvalidCoord( x, y )  )
      return;

    // If a stone has been mapped already, we go no further.  This is the
    // main recursion stopper condition (MRST).
    //
    if( myStoneMap[ x ][ y ] == STONE )
      return;

    // No stone means we've found a liberty.
    //
    if( !getGoban().hasStone( x, y ) )
    {
      myStoneMap[ x ][ y ] = LIBERTY;
      return;
    }

    // Make sure the colours are compatible before recursing.  This must
    // be checked AFTER it is known that a stone is at the given coordinates
    // since "hasWhiteStone" will return false if there is no stone, or a
    // black stone is at the given location.
    //
    if( getGoban().hasWhiteStone( x, y ) == stoneColour.isWhite() )
    {
      // We've found a stone, mark the stone map so it isn't re-recursed.
      // This is used in concert with the MRST (see above).
      //
      myStoneMap[ x ][ y ] = STONE;

      // Don't have to worry about going over the board's boundaries, since
      // we check immediately for valid values during recursion.
      //
      makeStoneMap( stoneColour, x, y - 1 );
      makeStoneMap( stoneColour, x, y + 1 );
      makeStoneMap( stoneColour, x - 1, y );
      makeStoneMap( stoneColour, x + 1, y );
    }
  }

  /**
   * Simple method to tally liberties.
   *
   * @return The number of liberties for a particular group of stones.
   */
  private int countStoneMap()
  {
    int total = 0,
        length = myStoneMap.length - 1;

    // Tally the total number of liberties.
    //
    for( int x = length; x >= 0; x-- )
      for( int y = length; y >= 0; y-- )
        total += (myStoneMap[ x ][ y ] == LIBERTY) ? 1 : 0;

    return total;
  }

  private Goban getGoban() { return myGoban; }

  /**
   * Changes the Goban that this rule set watches for new stone placements.
   */
  private void setGoban( Goban goban )
  {
    myGoban = goban;
    myGoban.addObserver( this );
  }
  
  /**
   * Returns the number of points komi used in this game.  At the moment,
   * black cannot be given a komi (reverse komi).  This feature might not
   * be implemented, as SGF files don't take reverse komi into consideration.
   * Typical komi for modern games varies between 5.5 and 8.5.  The default
   * value is 0.5.
   *
   * @return The komi white gets for black's advantage in playing first.
   */
  public double getKomi() { return myKomi; }
  
  /**
   * Allows the system to change the amount of komi given to white.
   */
  public void setKomi( double komi ) { myKomi = komi; }
  
  /**
   * Returns the number of stones that Black has captured.
   */
  public double getWhiteCaptures() { return myWhiteCaptures; }

  /**
   * Returns the number of stones that White has captured.
   */
  public double getBlackCaptures() { return myBlackCaptures; }

  /**
   * Sets the captures back to initial values (typically 0.5 and 0.0 for
   * white captures and black captures, respectively).
   */  
  public void resetCaptures()
  {
    resetWhiteCaptures();
    resetBlackCaptures();
  }

  /**
   * Sets the number of white captures back to the value of komi.
   */
  public void resetWhiteCaptures() { setWhiteCaptures( getKomi() ); }
  
  /**
   * Sets the number of black captures back to zero.
   */
  public void resetBlackCaptures() { setBlackCaptures( 0 ); }

  /**
   * Changes the number of white captures to the number given.  Notifies
   * observers.
   *
   * @captures - The number of stones black has captured.
   */
  private void setWhiteCaptures( double captures )
  {
    myWhiteCaptures = captures;
    setChanged();
    notifyObservers();
  }

  /**
   * Changes the number of white captures to the number given.  Notifies
   * observers.
   *
   * @captures - The number of stones black has captured.
   */
  private void setBlackCaptures( double captures )
  {
    myBlackCaptures = captures;
    setChanged();
    notifyObservers();
  }
}
