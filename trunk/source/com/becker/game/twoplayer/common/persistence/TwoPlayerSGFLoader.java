package com.becker.game.twoplayer.common.persistence;

import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.persistence.tokens.*;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Loads a two player game from a text file.
 *
 * @author Barry Becker
 */
public class TwoPlayerSGFLoader extends SGFLoader {
    
    /**
     * Creates a new instance of SGFTwoPlayerLoader
     */
    public TwoPlayerSGFLoader() {
    }
    
       
    /**
     * Reads an SGF token, provided a StreamTokenizer to help with parsing the
     * text into SGFTokens.
     * <P>
     * @param st - The StreamTokenizer from which to read an SGF token.
     *
     * @return An SGFToken representing a piece of information about the game.
     */
    @Override
  protected SGFToken readToken( StreamTokenizer st )  throws IOException, SGFException
  {
      SGFToken token = null;
      String tokenName = st.sval.toUpperCase();

      // moves are the most common token in an SGF file.
      if( tokenName.equals( "P1" ) )
          token = createPlayer1MoveToken();
      else if( tokenName.equals( "P2" ) )
          token = createPlayer2MoveToken();

      // Comments, notes, and figures are next most common.
      else if( tokenName.equals( "C" ) || tokenName.equals( "COMMENT" ) )
          token = new CommentToken();
      else if( tokenName.equals( "N" ) || tokenName.equals( "NAME" ) )
          token = new NodeNameToken();

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
      GameContext.log(2, "parsed token = " + tokenName + " "   + token.toString());

      return token;
  }

    protected TwoPlayerMoveToken createPlayer1MoveToken() {
        return new Player1MoveToken();
    }
    protected TwoPlayerMoveToken createPlayer2MoveToken() {
        return new Player2MoveToken();
    }
}
