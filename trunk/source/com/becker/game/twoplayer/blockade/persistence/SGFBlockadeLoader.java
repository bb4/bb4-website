package com.becker.game.twoplayer.blockade.persistence;

import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;
import com.becker.game.twoplayer.blockade.persistence.tokens.*;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Loads a Blockade game from a text file.
 *
 * Created on May 27, 2007, 5:35 AM
 * @author Barry Becker
 */
public class SGFBlockadeLoader extends SGFLoader {
    
    /**
     * Creates a new instance of SGFBlockadeLoader
     */
    public SGFBlockadeLoader() {
    }
    
       
    /**
     * Reads an SGF token, provided a StreamTokenizer to help with parsing the
     * text into SGFTokens.
     * <P>
     * @param st - The StreamTokenizer from which to read an SGF token.
     *
     * @return An SGFToken representing a piece of information about the game.
     */
  protected SGFToken readToken( StreamTokenizer st )  throws IOException, SGFException
  {
      SGFToken token = null;
      String tokenName = st.sval.toUpperCase();

    // moves are the most common token in an SGF file.
    //
    if( tokenName.equals( "P1" ) )
        token = new Player1MoveToken();
    else if( tokenName.equals( "P2" ) )
        token = new Player2MoveToken();

    // Comments, notes, and figures are next most common.
    //
    else if( tokenName.equals( "C" ) || tokenName.equals( "COMMENT" ) )
        token = new CommentToken();
    else if( tokenName.equals( "N" ) || tokenName.equals( "NAME" ) )
        token = new NodeNameToken();

    /* Adding black moves and white moves is typically done at the beginning
    // of a game (initial board position).
    //
    else if (tokenName.equals( "AP1" ))
        token = new AddPlayer1Token();
    else if (tokenName.equals( "AP2" ))
        token = new AddPlayer2Token();
     */

    // Lastly, tokens that belong to the first leaf of the first variation
    // appear once.  These are intentionally placed in this position as a
    // standard convention for JiGo's SGF API.
    //
    else if( tokenName.equals( "FF" ) )
        token = new FileFormatToken();
    else if( tokenName.equals( "GM" ) || tokenName.equals( "GAME" ) )
        token = new GameTypeToken();
    else if( tokenName.equals( "SZ2" ) || tokenName.equals( "SIZE" ) )
        token = new Size2Token();    
    else if( tokenName.equals( "PLAYER1" ) )
        token = new Player1NameToken();
    else if( tokenName.equals( "PLAYER2" ) )
        token = new Player2NameToken();
    else if( tokenName.equals( "DT" ) || tokenName.equals( "DATE" ) )
        token = new DateToken();
    else if( tokenName.equals( "RE" ) || tokenName.equals( "RESULT" ) )
        token = new ResultToken();
    else if( tokenName.equals( "GC" ) )
        token = new GameCommentToken();
    else if( tokenName.equals( "GN" ) || tokenName.equals( "GAMENAME" ) )
        token = new GameNameToken();
    else if( tokenName.equals( "ID" ) )
        token = new GameIDToken();
    else if( tokenName.equals( "CA" ) )
    //  token = new CharsetToken();   // where did this token class go?
         token = new TextToken();

    // If all else fails, fail
    else
        throw new SGFException("unexpected token name:"+ tokenName);

    // Now that we know what type of token we have, ask it to parse itself.
    // Most of the parsing is done by the TextToken class.  All tokens are
    // subclasses of SGFToken.
    //
    token.parse( st );

    return token;
  }
    
}
