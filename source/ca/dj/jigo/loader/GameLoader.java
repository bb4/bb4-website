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

package ca.dj.jigo.loader;

import java.io.*;
import java.util.*;
import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;

/**
 * Example program showing how to read and parse the contents of an SGF
 * file.
 */
public class GameLoader
{
  private final static String UNKNOWN = "Unknown";
  private final static String DEFAULT_KOMI = "0.5";

  public static boolean show( String fileName )
  {
    try
    {
      // Loads up the SGF file, given a particular file name.
      //
      SGFGame game = SGFLoader.load(
        new FileInputStream( new File( fileName ) ) );

      showInfoTokens( game );

      showTree( game.getTree(), "" );
    }
    catch( IOException ioe )
    {
      return false;
    }
    catch( SGFException sgfe )
    {
      System.out.println( sgfe.toString() );
    }

    return true;
  }

  /**
   * Given an SGFTree, this method will attempt to display its entire
   * tree, including variations.
   *
   * @param tree - The SGFTree containing an SGF variation tree.
   * @param spaces - Used for indentation.
   */
  private static void showTree( SGFTree tree, String spaces )
  {
    Enumeration trees = tree.getTrees(),
                leaves = tree.getLeaves(),
                tokens;

    while( (leaves != null) && leaves.hasMoreElements() )
    {
      SGFToken token;
      tokens = ((SGFLeaf)(leaves.nextElement())).getTokens();
      
      // Display the moves; check the token with instanceof to extract
      // move comments, board mark-up, etc.
      //
      while( (tokens != null) && tokens.hasMoreElements() )
      {
        token = (SGFToken)(tokens.nextElement());

        if( token instanceof MoveToken )
        {
          MoveToken moveToken = (MoveToken)token;

          System.out.print( spaces );

          if( moveToken.isWhite() )
            System.out.print( "W: " );
          else
            System.out.print( "B: " );

          System.out.println( moveToken.getX() + ", " + moveToken.getY() );
        }
      }
    }

    // Display the variations ...
    //
    while( (trees != null) && trees.hasMoreElements() )
      showTree( (SGFTree)(trees.nextElement()), " " + spaces );
  }

  private static void showInfoTokens( SGFGame game )
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
        comment = ((GameCommentToken)it).getGameComment();
      else if( it instanceof KomiToken )
        komi = new Float( ((KomiToken)it).getKomi() ).toString();

      // Keep using instanceof to pluck out additional game information
      // (such as place, date, round, event, and such).
      //
    }

    System.out.println( "White : " + whiteName );
    System.out.println( "Black : " + blackName );
    System.out.println( "Result: " + result );
    System.out.println( "Komi  : " + komi );

    // ... etc. ...
  }

  public static void main( String args[] )
  {
    if( args.length == 1 )
    {
      if( !GameLoader.show( args[0] ) )
        System.out.println( "ERROR: Could not open " + args[0] );
    }
    else
      showUsage();
  }

  private static void showUsage()
  {
    System.out.println( "java ca.dj.jigo.loader.GameLoader <filename>" );
  }
}

